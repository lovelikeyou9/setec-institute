import base64
import io

import qrcode
import stripe
from flask import current_app, url_for


def init_stripe():
    stripe.api_key = current_app.config["STRIPE_SECRET_KEY"]


def create_checkout_session(order, line_items, shipping_cost):
    """Create a Stripe Checkout Session for the given order."""
    init_stripe()

    kwargs = {
        "mode": "payment",
        "payment_method_types": ["card"],
        "line_items": line_items,
        "customer_email": order.customer_email,
        "success_url": url_for("checkout.payment_success", order_id=order.id, _external=True)
        + "&session_id={CHECKOUT_SESSION_ID}",
        "cancel_url": url_for("checkout.payment_cancel", order_id=order.id, _external=True),
        "metadata": {
            "order_id": str(order.id),
            "user_id": str(order.user_id),
        },
    }

    if float(shipping_cost) > 0:
        kwargs["shipping_options"] = [
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

    return stripe.checkout.Session.create(**kwargs)


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


def generate_checkout_qr_data_uri(checkout_url: str) -> str:
    """Build a PNG QR code (data URI) that opens the Stripe Checkout URL."""
    qr = qrcode.QRCode(version=None, box_size=8, border=2)
    qr.add_data(checkout_url)
    qr.make(fit=True)
    img = qr.make_image(fill_color="black", back_color="white")
    buffer = io.BytesIO()
    img.save(buffer, format="PNG")
    encoded = base64.b64encode(buffer.getvalue()).decode("ascii")
    return f"data:image/png;base64,{encoded}"
