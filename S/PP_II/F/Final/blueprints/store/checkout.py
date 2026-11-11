"""Checkout (port of CartController::checkout / placeOrder) + Stripe + Visa."""
import logging

from flask import current_app, flash, jsonify, redirect, render_template, request, session, url_for

from auth_utils import load_current_user, login_required
from blueprints.store import store_bp
from extensions import db
from helpers import back_with_errors, validate
from models import Address
from services import cart_service, payment_service
from services.order_service import OrderError, place_orders
from activity_logger import log_activity

log = logging.getLogger("mmt.checkout")

PENDING_KEY = "pending_checkout"


@store_bp.get("/checkout")
@login_required
def checkout():
    user = load_current_user()
    selected = cart_service.get_selected(user.id)
    if not selected:
        flash("Select at least one product to checkout", "error")
        return redirect("/account/cart")
    grouped = {}
    for ci in selected:
        grouped.setdefault(ci.product.seller_id, []).append(ci)
    subtotal = cart_service.selected_subtotal(user.id)
    addresses = Address.query.filter_by(user_id=user.id).order_by(Address.default_address.desc()).all()
    return render_template("store/checkout.html", groupedCart=grouped, subtotal=subtotal, addresses=addresses)


def _address_string(d):
    return (d["street"] + (", " + d["commune"] if d.get("commune") else "") +
            f", {d['district']}, {d['province']}, {d['country'].strip()}")


def _finish(orders, message="Order placed successfully!"):
    """One DB order per seller; multi-seller carts open the checkout detail (all sellers)."""
    if not orders:
        flash(message, "message")
        return redirect("/account/orders")
    if len(orders) == 1:
        flash(message, "message")
        return redirect(f"/orders/{orders[0].order_number}?placed=true")

    flash(
        f"{message} Your cart had {len(orders)} sellers — open the checkout to see each store’s package.",
        "message",
    )
    ts = int(orders[0].placed_at.timestamp()) if orders[0].placed_at else 0
    return redirect(f"/account/orders/checkout/{ts}?placed=1")


@store_bp.post("/checkout/place")
@login_required
def place_order():
    user = load_current_user()
    data, errors = validate({
        "fullName": "required|string|max:120",
        "phone": "required|string|max:30",
        "province": "required|string|max:80",
        "district": "required|string|max:80",
        "commune": "nullable|string|max:80",
        "street": "required|string|max:250",
        "country": "required|string|max:80",
        "deliveryType": "required|in:STANDARD,EXPRESS",
        "paymentMethod": "required|in:CREDIT_CARD,VISA,STRIPE,QR_PAYMENT,CASH_ON_DELIVERY",
    })
    if errors:
        return back_with_errors(errors, "/checkout")

    method = data["paymentMethod"]
    delivery = data["deliveryType"]
    selected = cart_service.get_selected(user.id)
    if not selected:
        flash("No items selected", "error")
        return redirect("/checkout")

    shipping_each = 5.00 if delivery == "EXPRESS" else 2.00
    address = _address_string(data)
    country = data["country"].strip()
    args = (user, data["fullName"], data["phone"], address, country, delivery)

    try:
        if method == "STRIPE":
            return _start_stripe(user, selected, shipping_each, args)

        if method == "VISA":
            total = float(cart_service.selected_subtotal(user.id)) + shipping_each * len({c.product.seller_id for c in selected})
            result = payment_service.charge_visa(total, "USD")
            log_activity("PAYMENT", "Checkout", f"VISA {result['reference']} simulated={result['simulated']}")
        elif method in ("CREDIT_CARD", "QR_PAYMENT"):
            payment_service.simulate_online_payment(method, 0)

        orders = place_orders(*args, method)
        log_activity("ORDER", "Checkout", f"Placed {len(orders)} order(s) via {method}", "orders", orders[0].id)
        return _finish(orders)
    except (OrderError, payment_service.PaymentError) as exc:
        flash(str(exc), "error")
        return redirect("/checkout")
    except Exception as exc:  # same catch-all as the Laravel controller
        log.exception("place_order failed")
        db.session.rollback()
        flash(str(exc), "error")
        return redirect("/checkout")


# ------------------------------------------------------------------ Stripe
def _start_stripe(user, selected, shipping_each, args):
    base = request.host_url.rstrip("/")
    success = base + url_for("store.stripe_success") + "?session_id={CHECKOUT_SESSION_ID}"
    cancel = base + url_for("store.stripe_cancel")
    sess = payment_service.create_stripe_checkout_session(
        user, selected, shipping_each, success, cancel, metadata={"delivery": args[5]})
    session[PENDING_KEY] = {
        "stripe_session_id": sess.id,
        "recipient_name": args[1], "recipient_phone": args[2], "shipping_address": args[3],
        "shipping_country": args[4], "delivery_type": args[5],
    }
    return redirect(sess.url, code=303)


@store_bp.get("/checkout/stripe/success")
@login_required
def stripe_success():
    user = load_current_user()
    pending = session.get(PENDING_KEY)
    session_id = request.args.get("session_id")
    if not pending or not session_id or pending.get("stripe_session_id") != session_id:
        flash("No pending Stripe checkout found.", "error")
        return redirect("/checkout")
    try:
        sess = payment_service.retrieve_stripe_session(session_id)
        if getattr(sess, "payment_status", None) != "paid":
            flash("Stripe payment was not completed.", "error")
            return redirect("/checkout")
        session.pop(PENDING_KEY, None)
        orders = place_orders(user, pending["recipient_name"], pending["recipient_phone"],
                              pending["shipping_address"], pending["shipping_country"],
                              pending["delivery_type"], "STRIPE")
        log_activity("PAYMENT", "Checkout", f"Stripe session {session_id} paid", "orders", orders[0].id)
        return _finish(orders, "Payment received via Stripe. Order placed successfully!")
    except (OrderError, payment_service.PaymentError) as exc:
        flash(str(exc), "error")
        return redirect("/checkout")


@store_bp.get("/checkout/stripe/cancel")
@login_required
def stripe_cancel():
    session.pop(PENDING_KEY, None)
    flash("Stripe payment cancelled. Your cart is unchanged.", "error")
    return redirect("/checkout")


@store_bp.post("/stripe/webhook")
@store_bp.post("/webhook/stripe")  # Stripe CLI: stripe listen --forward-to localhost:5000/webhook/stripe
def stripe_webhook():
    """Stub webhook (CSRF exempt). Verifies the signature when STRIPE_WEBHOOK_SECRET is set."""
    try:
        event = payment_service.verify_stripe_webhook(
            request.get_data(),
            request.headers.get("Stripe-Signature", ""),
        )
    except Exception as exc:
        return jsonify(error=str(exc)), 400
    etype = event.get("type") if hasattr(event, "get") else getattr(event, "type", None)
    current_app.logger.info("Stripe webhook received: %s", etype)
    return jsonify(received=True)
