"""Visa Developer API client using X-Pay Token (API Key + Shared Secret)."""

from __future__ import annotations

import base64
import hashlib
import hmac
import json
import time
from calendar import timegm
from datetime import datetime, timezone
from pathlib import Path
from typing import Any
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen

from flask import current_app


class VisaXPayError(Exception):
    """Raised when a Visa X-Pay request fails."""

    def __init__(self, message: str, status_code: int | None = None, body: Any = None):
        super().__init__(message)
        self.status_code = status_code
        self.body = body


def _looks_like_encrypted_b64(value: str) -> bool:
    """Portal-encrypted secrets are long base64 (often ~300+ chars, end with =)."""
    v = (value or "").strip()
    if len(v) < 100:
        return False
    return all(c.isalnum() or c in "+/=\n\r" for c in v)


def _load_shared_secret_from_file() -> str | None:
    """Read already-decrypted shared secret from certs/decrypted_shared_secret.bin."""
    path = Path(current_app.config.get("VISA_SHARED_SECRET_PATH") or "")
    if not path.is_file():
        return None
    raw = path.read_bytes().strip()
    if not raw:
        return None
    try:
        return raw.decode("utf-8").strip()
    except UnicodeDecodeError:
        return raw.decode("latin-1")


def _shared_secret() -> str:
    from_file = _load_shared_secret_from_file()
    if from_file:
        return from_file

    secret = (current_app.config.get("VISA_SHARED_SECRET") or "").strip()
    if not secret:
        raise VisaXPayError(
            "Shared secret missing. Put the decrypted value in "
            "certs/decrypted_shared_secret.bin, or set VISA_SHARED_SECRET in .env."
        )

    encrypted = current_app.config.get("VISA_SHARED_SECRET_ENCRYPTED") or _looks_like_encrypted_b64(
        secret
    )
    if encrypted:
        return _decrypt_shared_secret(secret)
    return secret


def _decrypt_shared_secret(encrypted_b64: str) -> str:
    """Decrypt portal-encrypted shared secret with certs/private_key.pem (RSA-OAEP SHA-256)."""
    from cryptography.hazmat.primitives import hashes, serialization
    from cryptography.hazmat.primitives.asymmetric import padding

    key_path = Path(current_app.config["VISA_PRIVATE_KEY_PATH"])
    if not key_path.is_file():
        raise VisaXPayError(f"Private key not found at {key_path}")

    try:
        private_key = serialization.load_pem_private_key(key_path.read_bytes(), password=None)
        ciphertext = base64.b64decode(encrypted_b64)
        plaintext = private_key.decrypt(
            ciphertext,
            padding.OAEP(
                mgf=padding.MGF1(algorithm=hashes.SHA256()),
                algorithm=hashes.SHA256(),
                label=None,
            ),
        )
        return plaintext.decode("utf-8").strip()
    except Exception as exc:
        raise VisaXPayError(
            "Could not decrypt VISA_SHARED_SECRET with certs/private_key.pem. "
            "Use the same key pair you uploaded to Visa Developer, or paste the "
            f"decrypted Shared Secret and set VISA_SHARED_SECRET_ENCRYPTED=false. ({exc})"
        ) from exc


def generate_x_pay_token(resource_path: str, query_string: str, body: str = "") -> str:
    """
    Match Visa Developer Python 3 sample:
    xv2:{timestamp}:{HMAC_SHA256(shared_secret, timestamp + path + query + body)}
    https://developer.visa.com/pages/working-with-visa-apis/x-pay-token
    """
    shared_secret = _shared_secret()
    timestamp = str(timegm(datetime.now(timezone.utc).timetuple()))
    pre_hash = f"{timestamp}{resource_path}{query_string}{body}"
    digest = hmac.new(
        shared_secret.encode("utf-8"),
        pre_hash.encode("utf-8"),
        hashlib.sha256,
    ).hexdigest()
    return f"xv2:{timestamp}:{digest}"


def _candidate_base_urls() -> list[str]:
    """
    Sandbox keys fail on cert host (and vice versa) with 401/9201.
    Prefer configured URL, then try the other common host.
    """
    primary = (
        current_app.config.get("VISA_API_BASE_URL") or "https://sandbox.api.visa.com"
    ).rstrip("/")
    alternates = [
        "https://sandbox.api.visa.com",
        "https://cert.api.visa.com",
    ]
    urls = [primary]
    for url in alternates:
        if url not in urls:
            urls.append(url)
    return urls


def visa_request(
    method: str,
    url_path: str,
    resource_path: str,
    body: dict | None = None,
    timeout: int = 30,
    try_alternate_hosts: bool = True,
) -> dict:
    """
    Authenticated Visa API call.

    url_path: full path after host, e.g. /vdp/helloworld
    resource_path: path used in X-Pay hash (helloworld skips /vdp/)
    """
    api_key = (current_app.config.get("VISA_API_KEY") or "").strip()
    if not api_key:
        raise VisaXPayError(
            "VISA_API_KEY is missing. Add it to .env from Visa Developer → Credentials → X-Pay Token."
        )
    if _looks_like_encrypted_b64(api_key):
        raise VisaXPayError(
            "VISA_API_KEY looks encrypted/base64 (too long). "
            "Copy the short plain API Key from Credentials → X-Pay Token."
        )

    query_string = f"apikey={api_key}"
    payload = "" if body is None else json.dumps(body, separators=(",", ":"))
    bases = _candidate_base_urls() if try_alternate_hosts else [
        (current_app.config.get("VISA_API_BASE_URL") or "https://sandbox.api.visa.com").rstrip("/")
    ]

    last_error: VisaXPayError | None = None
    for base_url in bases:
        x_pay_token = generate_x_pay_token(resource_path, query_string, payload)
        url = f"{base_url}{url_path}?{query_string}"
        headers = {
            "Accept": "application/json",
            "X-PAY-TOKEN": x_pay_token,
        }
        data = None
        if method.upper() != "GET":
            headers["Content-Type"] = "application/json"
            data = payload.encode("utf-8")

        req = Request(url, data=data, headers=headers, method=method.upper())
        try:
            with urlopen(req, timeout=timeout) as resp:
                raw = resp.read().decode("utf-8")
                current_app.config["VISA_API_BASE_URL"] = base_url
                return json.loads(raw) if raw else {}
        except HTTPError as exc:
            err_body = exc.read().decode("utf-8", errors="replace")
            try:
                parsed = json.loads(err_body) if err_body else err_body
            except json.JSONDecodeError:
                parsed = err_body
            last_error = VisaXPayError(
                f"Visa API HTTP {exc.code} @ {base_url}: {err_body[:400]}",
                status_code=exc.code,
                body=parsed,
            )
            if exc.code != 401 or not try_alternate_hosts:
                raise last_error from exc
            continue
        except URLError as exc:
            last_error = VisaXPayError(f"Visa API connection error @ {base_url}: {exc.reason}")
            if not try_alternate_hosts:
                raise last_error from exc
            continue

    raise last_error or VisaXPayError("Visa API request failed on all hosts.")


def test_connection() -> dict:
    """Call VDP Hello World to verify API Key + Shared Secret / X-Pay Token."""
    return visa_request(
        method="GET",
        url_path="/vdp/helloworld",
        resource_path="helloworld",
        body=None,
        try_alternate_hosts=True,
    )


def _card_type(number: str) -> str:
    """CyberSource card type codes: Visa=001, Mastercard=002, Amex=003."""
    n = "".join(ch for ch in number if ch.isdigit())
    if n.startswith("4"):
        return "001"
    if n.startswith(("51", "52", "53", "54", "55")) or n.startswith("2"):
        return "002"
    if n.startswith(("34", "37")):
        return "003"
    return "001"


def authorize_payment(order, card: dict) -> dict:
    """
    Charge / authorize via Visa Developer CyberSource Payments (X-Pay).

    Requires CyberSource (or Payments) product on your Visa project.
    Set VISA_PAYMENT_MODE=cybersource in .env.
    """
    name_parts = (order.customer_name or "Customer").strip().split(None, 1)
    first_name = name_parts[0]
    last_name = name_parts[1] if len(name_parts) > 1 else "Customer"

    address = (order.shipping_address or "").split(",")
    address1 = address[0].strip() if address else "1 Market St"
    locality = address[1].strip() if len(address) > 1 else "San Francisco"
    country = address[-1].strip() if len(address) > 2 else "US"
    if len(country) > 2:
        country = "US"

    body = {
        "clientReferenceInformation": {"code": f"ORDER-{order.id}"},
        "processingInformation": {"commerceIndicator": "internet"},
        "orderInformation": {
            "amountDetails": {
                "totalAmount": f"{float(order.total_amount):.2f}",
                "currency": "USD",
            },
            "billTo": {
                "firstName": first_name[:60],
                "lastName": last_name[:60],
                "address1": address1[:60],
                "locality": locality[:50],
                "administrativeArea": "CA",
                "postalCode": "94105",
                "country": country[:2].upper(),
                "email": order.customer_email,
                "phoneNumber": (order.customer_phone or "0000000000")[:15],
            },
        },
        "paymentInformation": {
            "card": {
                "number": "".join(ch for ch in card["number"] if ch.isdigit()),
                "expirationMonth": card["exp_month"].zfill(2),
                "expirationYear": card["exp_year"],
                "securityCode": card["cvc"],
                "type": _card_type(card["number"]),
            }
        },
    }

    return visa_request(
        method="POST",
        url_path="/cybersource/payments/v1/authorizations",
        resource_path="payments/v1/authorizations",
        body=body,
        try_alternate_hosts=True,
    )


def process_visa_payment(order, card: dict) -> str:
    """
    Run Visa payment for an order. Returns a transaction / reference id.

    Modes (VISA_PAYMENT_MODE):
      - demo: verify X-Pay with Hello World, then accept payment (class/sandbox)
      - cybersource: real CyberSource authorization on Visa sandbox
    """
    mode = (current_app.config.get("VISA_PAYMENT_MODE") or "demo").strip().lower()

    if mode == "cybersource":
        result = authorize_payment(order, card)
        tx_id = (
            result.get("id")
            or result.get("transactionId")
            or result.get("reconciliationId")
            or f"visa-cs-{order.id}-{int(time.time())}"
        )
        return str(tx_id)

    hello = test_connection()
    stamp = hello.get("timestamp") or hello.get("message") or int(time.time())
    return f"visa-demo-{order.id}-{stamp}"
