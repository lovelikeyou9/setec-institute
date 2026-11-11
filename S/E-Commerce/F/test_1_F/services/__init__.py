import stripe
from flask import current_app, url_for


def init_stripe():
    stripe.api_key = current_app.config["STRIPE_SECRET_KEY"]


def create_checkout_session(order, line_items, shipping_cost):
    """Create a Stripe Checkout Session for the given order."""
    init_stripe()

    session = stripe.checkout.Session.create(
        mode="payment",
        payment_method_types=["card"],
        line_items=line_items,
        customer_email=order.customer_email,
        success_url=url_for("checkout.payment_success", order_id=order.id, _external=True)
        + "&session_id={CHECKOUT_SESSION_ID}",
        cancel_url=url_for("checkout.payment_cancel", order_id=order.id, _external=True),
        metadata={
            "order_id": str(order.id),
            "user_id": str(order.user_id),
        },
        shipping_options=[
            {
                "shipping_rate_data": {
                    "type": "fixed_amount",
                    "fixed_amount": {
                        "amount": int(round(float(shipping_cost) * 100)),
                        "currency": "usd",
                    },
                    "display_name": "Standard Shipping",
                }
            }
        ]
        if float(shipping_cost) > 0
        else None,
    )
    return session


def build_line_items(order_items):
    items = []
    for item in order_items:
        items.append(
            {
                "price_data": {
                    "currency": "usd",
                    "unit_amount": int(round(float(item.price) * 100)),
                    "product_data": {
                        "name": item.product_name,
                    },
                },
                "quantity": item.quantity,
            }
        )
    return items


def construct_webhook_event(payload, sig_header):
    init_stripe()
    return stripe.Webhook.construct_event(
        payload,
        sig_header,
        current_app.config["STRIPE_WEBHOOK_SECRET"],
    )
