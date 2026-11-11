from flask import (
    Blueprint,
    current_app,
    flash,
    redirect,
    render_template,
    request,
    url_for,
    jsonify,
)
from flask_login import login_required, current_user

from extensions import db, csrf
from forms import CheckoutForm, VisaPayForm
from models import Order, OrderItem, CartItem
from services.stripe_service import (
    create_checkout_session,
    build_line_items,
    construct_webhook_event,
    generate_checkout_qr_data_uri,
)
from services.visa_xpay_service import VisaXPayError, process_visa_payment
from utils import get_or_create_cart, flash_form_errors

checkout_bp = Blueprint("checkout", __name__)


@checkout_bp.route("/checkout", methods=["GET", "POST"])
@login_required
def checkout():
    cart = get_or_create_cart(current_user)
    if not cart.items:
        flash("Your cart is empty.", "warning")
        return redirect(url_for("product.product_list"))

    for item in cart.items:
        if item.quantity > item.product.stock_quantity:
            flash(f"Not enough stock for {item.product.name}.", "danger")
            return redirect(url_for("cart.view_cart"))

    form = CheckoutForm()
    if request.method == "GET":
        form.full_name.data = current_user.name
        form.email.data = current_user.email
        form.phone.data = current_user.phone or ""
        form.payment_method.data = "stripe"

    shipping = current_app.config["SHIPPING_COST"]
    subtotal = cart.subtotal
    total = subtotal + shipping

    if form.validate_on_submit():
        shipping_address = (
            f"{form.address.data.strip()}, {form.city.data.strip()}, "
            f"{form.country.data.strip()}"
        )
        method = form.payment_method.data

        order = Order(
            user_id=current_user.id,
            total_amount=total,
            shipping_cost=shipping,
            shipping_address=shipping_address,
            customer_name=form.full_name.data.strip(),
            customer_email=form.email.data.strip().lower(),
            customer_phone=form.phone.data.strip(),
            status="Pending",
            payment_status="Pending",
            payment_method=method,
        )
        db.session.add(order)
        db.session.flush()

        for item in cart.items:
            db.session.add(
                OrderItem(
                    order_id=order.id,
                    product_id=item.product_id,
                    product_name=item.product.name,
                    price=item.product.effective_price,
                    quantity=item.quantity,
                )
            )

        db.session.commit()

        if method == "visa":
            return _pay_with_visa(order, form)

        if method == "visa_qr":
            pay_url = url_for("checkout.visa_pay", order_id=order.id, _external=True)
            qr_data_uri = generate_checkout_qr_data_uri(pay_url)
            return render_template(
                "payment_qr.html",
                order=order,
                checkout_url=pay_url,
                qr_data_uri=qr_data_uri,
                provider="Visa",
                open_label="Open Visa Pay",
            )

        try:
            line_items = build_line_items(order.items)
            session = create_checkout_session(order, line_items, shipping)
            order.stripe_session_id = session.id
            order.payment_method = method
            db.session.commit()

            if method == "stripe_qr":
                qr_data_uri = generate_checkout_qr_data_uri(session.url)
                return render_template(
                    "payment_qr.html",
                    order=order,
                    checkout_url=session.url,
                    qr_data_uri=qr_data_uri,
                    provider="Stripe",
                    open_label="Open Stripe Checkout",
                )

            return redirect(session.url, code=303)
        except Exception as exc:
            current_app.logger.exception("Stripe session error: %s", exc)
            flash(
                "Unable to start Stripe Checkout. Check your Stripe test keys in .env.",
                "danger",
            )
            return redirect(url_for("checkout.checkout"))

    if form.is_submitted() and not form.validate():
        flash_form_errors(form)

    return render_template(
        "checkout.html",
        form=form,
        cart=cart,
        subtotal=subtotal,
        shipping=shipping,
        total=total,
    )


def _pay_with_visa(order, form):
    card = {
        "number": form.card_number.data or "",
        "exp_month": (form.card_exp_month.data or "").strip(),
        "exp_year": (form.card_exp_year.data or "").strip(),
        "cvc": (form.card_cvc.data or "").strip(),
    }
    try:
        tx_id = process_visa_payment(order, card)
        order.visa_transaction_id = tx_id
        order.payment_method = order.payment_method or "visa"
        db.session.commit()
        _mark_order_paid_local(order)
        flash("Visa payment successful.", "success")
        return redirect(url_for("checkout.payment_success", order_id=order.id))
    except VisaXPayError as exc:
        current_app.logger.warning("Visa X-Pay payment failed: %s", exc)
        order.payment_status = "Failed"
        order.status = "Cancelled"
        db.session.commit()
        flash(
            "Visa payment failed. Check VISA_API_KEY / VISA_SHARED_SECRET in .env "
            f"({exc}).",
            "danger",
        )
        return redirect(url_for("checkout.checkout"))
    except Exception as exc:
        current_app.logger.exception("Visa payment error: %s", exc)
        order.payment_status = "Failed"
        order.status = "Cancelled"
        db.session.commit()
        flash("Unable to process Visa payment. See server logs for details.", "danger")
        return redirect(url_for("checkout.checkout"))


@checkout_bp.route("/payment/visa/<int:order_id>", methods=["GET", "POST"])
@login_required
def visa_pay(order_id):
    """Card entry page opened from Visa QR (or Open Visa Pay)."""
    order = Order.query.filter_by(id=order_id, user_id=current_user.id).first_or_404()
    if order.payment_status == "Paid":
        return redirect(url_for("checkout.payment_success", order_id=order.id))
    if order.payment_status != "Pending":
        flash("This order can no longer be paid.", "warning")
        return redirect(url_for("order.order_detail", order_id=order.id))

    form = VisaPayForm()
    if form.validate_on_submit():
        return _pay_with_visa(order, form)
    if form.is_submitted() and not form.validate():
        flash_form_errors(form)

    return render_template("visa_pay.html", order=order, form=form)


@checkout_bp.route("/checkout/create-session", methods=["POST"])
@login_required
def create_session():
    """Alias for checkout POST — creates Stripe Checkout Session or Visa payment."""
    return checkout()


@checkout_bp.route("/payment/success")
@login_required
def payment_success():
    """
    Success landing page.
    Stripe orders become Paid via webhook; Visa orders are marked Paid immediately.
    """
    order_id = request.args.get("order_id", type=int)
    order = Order.query.filter_by(id=order_id, user_id=current_user.id).first_or_404()
    return render_template("payment_success.html", order=order)


@checkout_bp.route("/payment/cancel")
@login_required
def payment_cancel():
    order_id = request.args.get("order_id", type=int)
    order = Order.query.filter_by(id=order_id, user_id=current_user.id).first()
    if order and order.payment_status == "Pending":
        order.payment_status = "Failed"
        order.status = "Cancelled"
        db.session.commit()
    flash("Payment was cancelled. You can try again from your cart.", "warning")
    return render_template("payment_cancel.html", order=order)


@checkout_bp.route("/payment/qr/<int:order_id>/status")
@login_required
def payment_qr_status(order_id):
    """JSON status for the Stripe QR page (poll until Paid via webhook)."""
    order = Order.query.filter_by(id=order_id, user_id=current_user.id).first_or_404()
    return jsonify(
        {
            "order_id": order.id,
            "payment_status": order.payment_status,
            "status": order.status,
            "paid": order.payment_status == "Paid",
        }
    )


@checkout_bp.route("/webhook/stripe", methods=["POST"])
@csrf.exempt
def stripe_webhook():
    payload = request.get_data()
    sig_header = request.headers.get("Stripe-Signature", "")

    try:
        event = construct_webhook_event(payload, sig_header)
    except Exception as exc:
        current_app.logger.warning("Webhook signature verification failed: %s", exc)
        return jsonify({"error": "Invalid signature"}), 400

    if event["type"] == "checkout.session.completed":
        session = event["data"]["object"]
        _mark_order_paid(session)
    elif event["type"] == "checkout.session.expired":
        session = event["data"]["object"]
        order = Order.query.filter_by(stripe_session_id=session.get("id")).first()
        if order and order.payment_status == "Pending":
            order.payment_status = "Failed"
            order.status = "Cancelled"
            db.session.commit()

    return jsonify({"status": "ok"}), 200


def _mark_order_paid(session):
    order_id = session.get("metadata", {}).get("order_id")
    order = None
    if order_id:
        order = Order.query.get(int(order_id))
    if not order:
        order = Order.query.filter_by(stripe_session_id=session.get("id")).first()
    if not order:
        current_app.logger.error("Webhook: order not found for session %s", session.get("id"))
        return

    if order.payment_status == "Paid":
        return

    order.payment_status = "Paid"
    order.status = "Paid"
    order.payment_method = order.payment_method or "stripe"
    order.stripe_session_id = session.get("id")
    order.stripe_payment_intent_id = session.get("payment_intent")
    _clear_cart_and_stock(order)
    db.session.commit()
    current_app.logger.info("Order #%s marked PAID via Stripe webhook", order.id)


def _mark_order_paid_local(order):
    if order.payment_status == "Paid":
        return
    order.payment_status = "Paid"
    order.status = "Paid"
    _clear_cart_and_stock(order)
    db.session.commit()
    current_app.logger.info("Order #%s marked PAID via Visa X-Pay", order.id)


def _clear_cart_and_stock(order):
    cart = get_or_create_cart(order.user)
    for item in list(cart.items):
        db.session.delete(item)

    for order_item in order.items:
        product = order_item.product
        if product:
            product.stock_quantity = max(0, product.stock_quantity - order_item.quantity)
            product.sales_count = (product.sales_count or 0) + order_item.quantity
