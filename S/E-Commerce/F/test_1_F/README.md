# TechStore — Flask Computer Store

Professional e-commerce site for computer hardware.

**Stack:** HTML/CSS/JS + Bootstrap 5 · Python Flask · MySQL + SQLAlchemy · Flask-Login · Stripe Checkout (Test Mode) + Visa X-Pay Token

**Path:** `C:\Users\Admin\OneDrive\Desktop\S\E-Commerce\F\test_1_F`

---

## 1. Configure `.env`

Edit `.env` and set your MySQL password, Stripe **test** keys, and Visa X-Pay credentials:

```env
MYSQL_PASSWORD=your_mysql_password
MYSQL_DATABASE=ec_test_1_f

STRIPE_PUBLISHABLE_KEY=pk_test_...
STRIPE_SECRET_KEY=sk_test_...
STRIPE_WEBHOOK_SECRET=whsec_...

# Visa Developer → Project → Credentials → X-Pay Token
VISA_API_KEY=...
VISA_SHARED_SECRET=...
VISA_API_BASE_URL=https://cert.api.visa.com
VISA_PAYMENT_MODE=demo
VISA_SHARED_SECRET_ENCRYPTED=false
```

Never put `sk_test_...` or Visa secrets in frontend JavaScript. Never commit `.env`.

If the Shared Secret from the portal is still RSA-encrypted, set `VISA_SHARED_SECRET_ENCRYPTED=true` and keep `certs/private_key.pem` (the key you used when uploading the public key to Visa).

---

## 2. Install & run

```powershell
cd C:\Users\Admin\OneDrive\Desktop\S\E-Commerce\F\test_1_F
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
python app.py
```

Open: http://127.0.0.1:5000

On first start, tables are created and sample products/users are seeded.

### Demo accounts

| Role     | Email                  | Password     |
|----------|------------------------|--------------|
| Admin    | admin@techstore.com    | admin123     |
| Customer | customer@techstore.com | customer123  |

## 3. Stripe webhooks (required for Stripe Paid status)

```powershell
stripe listen --forward-to 127.0.0.1:5000/webhook/stripe
```

Copy `whsec_...` into `.env`, restart Flask.

Test card: `4242 4242 4242 4242` · any future expiry · any CVC

**Stripe orders are marked Paid only by the webhook** (`checkout.session.completed`), not by the success page.

### Stripe QR Code

On checkout, choose **Stripe QR Code** → the app creates a Checkout Session and shows a QR of the Stripe URL. Scan with your phone (or use **Open Stripe Checkout**). Keep `stripe listen` running so the order becomes Paid.

### Visa QR Code

Choose **Visa QR Code** → shows a QR for `/payment/visa/<order_id>`. Open it (or scan if your phone can reach the server), enter card `4111111111111111`, and pay. The waiting page auto-redirects when Paid.

## 4. Visa (X-Pay Token) — second payment method

Checkout lets the customer pick **Stripe** or **Credit or debit card (Visa X-Pay)**.

| Mode (`VISA_PAYMENT_MODE`) | Behavior |
|----------------------------|----------|
| `demo` (default) | Calls Visa **Hello World** with your API Key + X-Pay Token to prove credentials, then marks the order Paid |
| `cybersource` | POSTs a CyberSource authorization on Visa sandbox (requires CyberSource / Payments on your Visa project) |

Visa sandbox test card example: `4111111111111111` · expiry in the future · any CVC.

X-Pay docs: https://developer.visa.com/pages/working-with-visa-apis/x-pay-token

## Project layout

```
app.py, config.py, extensions.py, forms.py, seed.py
models/     user, category, product, cart, order
routes/     auth, main, product, cart, checkout, order, admin
services/   stripe_service.py, visa_xpay_service.py
templates/  storefront + admin/
static/     css, js, images
certs/      optional RSA keys for decrypting Visa shared secret
```

## Features

- Home: hero, categories, featured / best sellers / new
- Products: search, category/brand/price filters, sort, pagination
- Product detail + related products
- Cart, checkout with **Stripe** or **Visa X-Pay**
- Auth (register/login/account) with hashed passwords
- Orders + admin dashboard (products, categories, orders, customers)
