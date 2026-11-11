# MMT - Modern Multiple Technology (Flask)

Flask + MySQL port of the Laravel *KhmerStore* marketplace: same database, same flow, same UI
(Blade templates converted to Jinja, CSS classes unchanged). Teacher patterns (session login,
CSRF, Flask-Limiter, Telegram error alerts, error pages) are taken from `test_5_F`.

## Run

```bat
run.bat
:: or
.venv\Scripts\python.exe app.py          :: http://127.0.0.1:5000
.venv\Scripts\python.exe -m flask --app app run
```

Settings live in `.env` (DB URI, `SECRET_KEY`, Telegram, Stripe, Visa, JWT, rate limits).
The database `final_pp_ii_mmt` already exists (cloned from the Laravel DB). The app never
creates/alters tables (`flask db` is wired but not used).

## Demo accounts

Create/reset them with `.venv\Scripts\python.exe seed_demo_users.py`
(add `--reset-laravel` to also give the three Laravel sample accounts known passwords):

| Role     | Email               | Password       |
|----------|---------------------|----------------|
| ADMIN    | admin@mmt.local     | Admin123!      |
| CUSTOMER | customer@mmt.local  | Customer123!   |
| SELLER   | seller@mmt.local    | Seller123!     |

The seller has an APPROVED `sellers` row ("MMT Demo Store"). With `--reset-laravel`:
`admin@computerhub.com` = Admin123!, `customer@computerhub.com` = Customer123!,
`techzone@computerhub.com` = Seller123! (TechZone owns the seeded products/orders).

Passwords: bcrypt (`$2y$`, Laravel compatible) and werkzeug hashes are both accepted.

## Layout

```
app.py config.py extensions.py helpers.py auth_utils.py telegram_notify.py activity_logger.py upload_service.py
models/       users, sellers, catalog, commerce, logs (reflect the existing tables; bit(1) via TypeDecorator)
services/     cart_service, order_service (escrow), product_variants, payment_service, country_service
blueprints/   auth, store, account, seller (/seller-dashboard), admin (/admin), api (/api)
templates/    Jinja (converted from Blade)        static/ css js images uploads
```

## Security

* **CSRF** on every web POST (`csrf_token` in each form). Exempt: `/api/*` and `/stripe/webhook`.
* **Flask-Limiter**: default 10000/day + 100/min (static assets exempt); logged-in CUSTOMER/SELLER
  GET pages: **10 per minute** ("refresh" limit, admins and guests exempt); `POST /login` and
  `POST /api/login`: **5 per minute**. On 429: Telegram alert + `error_log` row + activity `BAN` +
  `error/429.html`.
* **Telegram** only for 404 (real page visits, no assets/bots), 429 (refresh / login ban) and 500.
* **JWT** (`/api`): `POST /api/login {email,password}` -> `access_token`; send `Authorization: Bearer <token>`.
  `GET /api/health` (public), `/api/me` (JWT), `/api/products` + `/api/products/<id>` + `/api/categories`
  (public, JWT optional), `/api/cart`, `/api/orders` (JWT).
* Roles: ADMIN, SELLER (APPROVED only), CUSTOMER; disabled users (`users.enabled = 0`) are logged out.

## Order flow (100% escrow, like Laravel `OrderService`)

Place order -> `PAYMENT_CONFIRMED` (online) / `PENDING_PAYMENT` (COD) -> admin *confirm hold*
`FUNDS_HELD` -> seller *accept* `SELLER_PROCESSING` -> `SHIPPED` -> `OUT_FOR_DELIVERY` -> admin
*confirm delivered* `DELIVERED` (3-day buyer protection) -> payout released `COMPLETED`
(3% commission). Disputes, refunds and out-of-stock decline are supported. One order per seller.

## Payments

* `CASH_ON_DELIVERY`, `CREDIT_CARD`, `QR_PAYMENT`: same as Laravel (card/QR are simulated).
* `STRIPE`: real Stripe Checkout Session (test keys) -> `/checkout/stripe/success` verifies
  `payment_status == paid` and then creates the orders. Test card `4242 4242 4242 4242`.
  `/stripe/webhook` is a stub (verifies the signature if `STRIPE_WEBHOOK_SECRET` is set).
* `VISA`: stub in `services/payment_service.py` (reads `VISA_API_KEY` and `C:\visa-keys\public_key.pem`);
  in test mode (`VISA_TEST_MODE=1`) a failed/unavailable API call is a simulated success. Set
  `VISA_LIVE_CALL=1` to really call the Visa sandbox.

## Notes / known gaps

* `_blade_src/` holds the original Blade files plus the one-shot converter (`_convert.py` then
  `_postfix.py`). Re-running it regenerates `templates/` and overwrites manual template edits.
* `JWT_SECRET_KEY` in `.env` is only 20 bytes; PyJWT warns (works). Use 32+ chars in production.
* Deleting users/products/sellers that already have orders is blocked by foreign keys; the UI shows a
  message (ban/deactivate instead).
* No email sending, no real Visa/QR gateways, Stripe webhook does not create orders (success URL does).
* Flask's dev server is used; for production run behind waitress/gunicorn and set `FLASK_DEBUG=0`.
