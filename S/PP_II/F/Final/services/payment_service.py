"""Online payments: Stripe Checkout Session + Visa sandbox stub.

* CREDIT_CARD / QR_PAYMENT  -> simulated online payment (same as Laravel: status PAYMENT_CONFIRMED)
* STRIPE                   -> real Stripe Checkout Session (test keys), order created on success
* VISA                     -> Visa stub: uses VISA_API_KEY and C:\\visa-keys\\public_key.pem when present;
                              in test mode a failed/unavailable API call is treated as simulated success
"""
import hashlib
import logging
import os
import uuid

import requests
import stripe
from flask import current_app

log = logging.getLogger("mmt.payment")

VISA_SANDBOX_URL = "https://sandbox.api.visa.com/vdp/helloworld"


class PaymentError(RuntimeError):
    pass


# ------------------------------------------------------------------ Stripe
def _stripe_key():
    key = current_app.config.get("STRIPE_SECRET_KEY")
    if not key:
        raise PaymentError("Stripe is not configured (STRIPE_SECRET_KEY missing).")
    stripe.api_key = key
    return key


def create_stripe_checkout_session(user, cart_items, shipping_per_seller, success_url, cancel_url,
                                   metadata=None):
    """Create a Stripe Checkout Session for the selected cart lines.

    cart_items: list[CartItem] (selected).  Shipping is charged once per seller
    (one order per seller, exactly like OrderService.place_orders).
    """
    _stripe_key()
    line_items = []
    seller_ids = set()
    for ci in cart_items:
        p = ci.product
        seller_ids.add(p.seller_id)
        unit = ci.line_unit_price() if hasattr(ci, "line_unit_price") else float(p.price)
        name = (p.name or "Product")
        if getattr(ci, "selected_options", None):
            name = f"{name} ({ci.selected_options.replace('|', ', ')})"
        line_items.append({
            "price_data": {
                "currency": "usd",
                "unit_amount": int(round(float(unit) * 100)),
                "product_data": {"name": name[:250]},
            },
            "quantity": int(ci.quantity),
        })
    if shipping_per_seller and seller_ids:
        line_items.append({
            "price_data": {
                "currency": "usd",
                "unit_amount": int(round(float(shipping_per_seller) * 100)),
                "product_data": {"name": "Shipping"},
            },
            "quantity": len(seller_ids),
        })
    if not line_items:
        raise PaymentError("No items to pay for.")

    try:
        session = stripe.checkout.Session.create(
            mode="payment",
            customer_email=user.email,
            line_items=line_items,
            success_url=success_url,
            cancel_url=cancel_url,
            client_reference_id=str(user.id),
            metadata={"user_id": str(user.id), **(metadata or {})},
        )
    except Exception as exc:  # stripe.error.* / network
        log.warning("Stripe session failed: %s", exc)
        raise PaymentError(f"Stripe error: {getattr(exc, 'user_message', None) or exc}")
    return session


def retrieve_stripe_session(session_id):
    _stripe_key()
    try:
        return stripe.checkout.Session.retrieve(session_id)
    except Exception as exc:
        raise PaymentError(f"Could not verify Stripe payment: {exc}")


def verify_stripe_webhook(payload, sig_header):
    """Optional webhook verification (STRIPE_WEBHOOK_SECRET)."""
    secret = current_app.config.get("STRIPE_WEBHOOK_SECRET")
    if not secret:
        # stub mode: accept and just parse the JSON
        import json
        return json.loads(payload)
    return stripe.Webhook.construct_event(payload, sig_header, secret)


# -------------------------------------------------------------------- Visa
def _visa_public_key_fingerprint():
    path = os.path.join(current_app.config.get("VISA_KEYS_DIR", r"C:\visa-keys"), "public_key.pem")
    if not os.path.isfile(path):
        return None
    try:
        with open(path, "rb") as fh:
            return hashlib.sha256(fh.read()).hexdigest()[:16]
    except OSError:
        return None


def charge_visa(amount, currency="USD", reference=None):
    """Visa sandbox stub. Returns dict(success, reference, simulated, message)."""
    cfg = current_app.config
    api_key = cfg.get("VISA_API_KEY")
    test_mode = cfg.get("VISA_TEST_MODE", True)
    reference = reference or f"VISA-{uuid.uuid4().hex[:12].upper()}"
    fingerprint = _visa_public_key_fingerprint()
    log.info("Visa charge ref=%s amount=%.2f %s key=%s pubkey=%s", reference, amount, currency,
             (api_key or "")[:6] + "***", fingerprint)

    if not api_key:
        if test_mode:
            return {"success": True, "simulated": True, "reference": reference,
                    "message": "Visa test mode: no API key, simulated approval."}
        raise PaymentError("Visa API key is not configured.")

    if os.getenv("VISA_LIVE_CALL", "0") == "1":
        try:
            resp = requests.get(VISA_SANDBOX_URL, headers={"apikey": api_key,
                                                          "Accept": "application/json"}, timeout=4)
            if resp.ok:
                return {"success": True, "simulated": False, "reference": reference,
                        "message": "Visa sandbox reachable."}
            raise PaymentError(f"Visa sandbox answered HTTP {resp.status_code}")
        except Exception as exc:
            if not test_mode:
                raise PaymentError(f"Visa payment failed: {exc}")
            log.warning("Visa API call failed (%s) - falling back to simulated success", exc)

    return {"success": True, "simulated": True, "reference": reference,
            "message": "Visa sandbox simulated approval (test mode)."}


# ------------------------------------------------- generic simulated methods
def simulate_online_payment(method, amount):
    """CREDIT_CARD / QR_PAYMENT are treated as instantly paid (as in the Laravel app)."""
    return {"success": True, "simulated": True, "reference": f"{method}-{uuid.uuid4().hex[:10].upper()}",
            "message": "Simulated online payment."}
