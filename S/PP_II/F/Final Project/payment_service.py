"""Simple Stripe Checkout + Visa test-mode stub for Final Project."""
import logging
import stripe
from flask import current_app

log = logging.getLogger("app.payment")


class PaymentError(RuntimeError):
    pass


def create_stripe_session(cart_items, total, customer_email, success_url, cancel_url):
    key = current_app.config.get("STRIPE_SECRET_KEY")
    if not key:
        raise PaymentError("Stripe is not configured.")
    stripe.api_key = key

    line_items = []
    for item in cart_items:
        line_items.append({
            "price_data": {
                "currency": "usd",
                "unit_amount": int(round(float(item["price"]) * 100)),
                "product_data": {"name": str(item.get("title") or "Product")[:250]},
            },
            "quantity": int(item.get("qty") or 1),
        })
    if not line_items:
        raise PaymentError("No items to pay for.")

    try:
        return stripe.checkout.Session.create(
            mode="payment",
            customer_email=customer_email or None,
            line_items=line_items,
            success_url=success_url,
            cancel_url=cancel_url,
            metadata={"total": f"{float(total):.2f}"},
        )
    except Exception as exc:
        log.warning("Stripe session failed: %s", exc)
        raise PaymentError(str(exc))


def retrieve_stripe_session(session_id):
    key = current_app.config.get("STRIPE_SECRET_KEY")
    if not key:
        raise PaymentError("Stripe is not configured.")
    stripe.api_key = key
    return stripe.checkout.Session.retrieve(session_id)


def charge_visa_stub(amount, customer_name):
    """Visa sandbox stub — succeeds in VISA_TEST_MODE when API key is present."""
    api_key = current_app.config.get("VISA_API_KEY")
    test_mode = current_app.config.get("VISA_TEST_MODE", True)
    if not api_key and not test_mode:
        raise PaymentError("Visa is not configured.")
    # Simulated sandbox success for student project / offline demo
    return {
        "ok": True,
        "amount": float(amount),
        "customer": customer_name,
        "mode": "test" if test_mode else "live-stub",
        "message": "Visa payment simulated successfully",
    }
