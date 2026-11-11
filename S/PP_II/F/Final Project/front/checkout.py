
import json

from front import front_bp
from flask import (
    render_template,
    request,
    make_response,
    redirect,
    url_for,
    flash,
    current_app,
)
from catalog import get_product_by_id
from telegram_notify import send_telegram
from payment_service import create_stripe_session, retrieve_stripe_session, charge_visa_stub, PaymentError


def _load_and_enrich():
    raw = request.cookies.get("cart_list")
    cart_list = json.loads(raw) if raw else []
    enriched = []
    total = 0.0
    for item in cart_list:
        product = get_product_by_id(item.get("id"))
        if not product:
            continue
        qty = int(item.get("qty") or 1)
        price = float(product.get("price") or 0)
        row = {
            "id": product["id"],
            "qty": qty,
            "title": product.get("title"),
            "price": price,
            "image": product.get("image"),
            "brand": product.get("brand") or "",
            "category": product.get("category") or "",
        }
        total += price * qty
        enriched.append(row)
    return enriched, total


def _order_telegram(form, cart_list, total, payment_method, extra=""):
    lines = ""
    for item in cart_list:
        lines += f"<code>{item['title']} ({item['qty']}x{item['price']})</code>\n"

    message = "<b>🚨 You have New Order</b>\n"
    message += f"<b>🧔 Customer: {form.get('firstName', '')} {form.get('lastName', '')}</b>\n"
    message += f"<code>📱 Phone: {form.get('phone', '')}</code>\n"
    message += f"<code>📧 Email: {form.get('email', '')}</code>\n"
    message += f"<code>📍 Address: {form.get('address', '')}</code>\n"
    message += f"<code>💳 Payment: {payment_method}</code>\n"
    if extra:
        message += f"<code>{extra}</code>\n"
    message += "<i>================================</i>\n"
    message += f"<code>ℹ️ Products:</code>\n{lines}"
    message += "<i>================================</i>\n"
    message += f"<b>💰 Total: ${total:.2f}</b>\n"
    send_telegram(message)


@front_bp.get("/checkout")
def checkout():
    cart_list, total = _load_and_enrich()
    return render_template(
        "front/checkout.html",
        cart_list=cart_list,
        total=total,
        stripe_pk=current_app.config.get("STRIPE_PUBLISHABLE_KEY", ""),
    )


@front_bp.post("/checkout")
def do_checkout():
    cart_list, total = _load_and_enrich()
    if not cart_list:
        flash("Your cart is empty.", "warning")
        return redirect(url_for("front_bp.cart"))

    form = request.form
    payment_method = (form.get("payment_method") or "QR").upper()

    # --- Stripe Checkout Session ---
    if payment_method == "STRIPE":
        try:
            session = create_stripe_session(
                cart_items=cart_list,
                total=total,
                customer_email=form.get("email"),
                success_url=url_for("front_bp.stripe_success", _external=True)
                + "?session_id={CHECKOUT_SESSION_ID}",
                cancel_url=url_for("front_bp.checkout", _external=True),
            )
            # stash order form in cookie for success handler
            order_meta = {
                "firstName": form.get("firstName", ""),
                "lastName": form.get("lastName", ""),
                "email": form.get("email", ""),
                "phone": form.get("phone", ""),
                "address": form.get("address", ""),
                "payment_method": "STRIPE",
            }
            resp = make_response(redirect(session.url, code=303))
            resp.set_cookie("pending_order", json.dumps(order_meta), max_age=3600)
            return resp
        except PaymentError as exc:
            flash(f"Stripe error: {exc}", "danger")
            return redirect(url_for("front_bp.checkout"))

    # --- Visa stub ---
    if payment_method == "VISA":
        try:
            charge_visa_stub(
                total,
                f"{form.get('firstName', '')} {form.get('lastName', '')}".strip(),
            )
        except PaymentError as exc:
            flash(f"Visa error: {exc}", "danger")
            return redirect(url_for("front_bp.checkout"))

    _order_telegram(form, cart_list, total, payment_method)
    response = make_response(
        render_template(
            "front/checkout_success.html",
            total=total,
            payment_method=payment_method,
            customer_name=f"{form.get('firstName', '')} {form.get('lastName', '')}".strip(),
        )
    )
    response.set_cookie("cart_list", "[]")
    response.set_cookie("pending_order", "", expires=0)
    return response


@front_bp.get("/checkout/stripe/success")
def stripe_success():
    session_id = request.args.get("session_id")
    if not session_id:
        flash("Missing Stripe session.", "danger")
        return redirect(url_for("front_bp.checkout"))

    try:
        session = retrieve_stripe_session(session_id)
    except PaymentError as exc:
        flash(str(exc), "danger")
        return redirect(url_for("front_bp.checkout"))

    if getattr(session, "payment_status", None) != "paid":
        flash("Payment not completed.", "warning")
        return redirect(url_for("front_bp.checkout"))

    cart_list, total = _load_and_enrich()
    meta_raw = request.cookies.get("pending_order")
    form = json.loads(meta_raw) if meta_raw else {}
    _order_telegram(form, cart_list, total, "STRIPE", extra=f"Stripe session: {session_id}")

    response = make_response(
        render_template(
            "front/checkout_success.html",
            total=total,
            payment_method="STRIPE",
            customer_name=f"{form.get('firstName', '')} {form.get('lastName', '')}".strip(),
        )
    )
    response.set_cookie("cart_list", "[]")
    response.set_cookie("pending_order", "", expires=0)
    return response
