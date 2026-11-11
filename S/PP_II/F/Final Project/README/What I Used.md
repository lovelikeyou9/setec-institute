**`Final` — what it uses only**

Flask port of KhmerStore (MMT marketplace) with **real Stripe + JWT API + Telegram + rate limits**.

---

### Architecture
- Monolith
- MVC-style (Models + Jinja views + Blueprint routes)
- Blueprint modular layout (`auth`, `store`, `account`, `seller`, `admin`, `api`)
- Service layer (`cart`, `order`, `payment`, `country`, `product_variants`)
- Server-Side Rendering (Jinja2)
- Dual auth: **session (web)** + **JWT (API)**
- REST JSON API (`/api`)
- Multi-vendor marketplace
- RBAC (`ADMIN` / `SELLER` / `CUSTOMER`)
- Escrow order workflow (3% commission, 3-day buyer protection)
- CORS for API (localhost:4200 / 3000 / 5000)

---

### Auth & security
- Flask session login (cookie)
- JWT (`PyJWT`, HS256, Bearer token)
- bcrypt (`$2y$` Laravel-compatible) + werkzeug hash verify
- CSRF (`Flask-WTF` / `CSRFProtect`) — exempt: `/api/*`, Stripe webhook
- Flask-Limiter (rate limiting)
  - default 10000/day + 100/min
  - login: 5/min
  - refresh (CUSTOMER/SELLER GET): 10/min
- Role decorators / middleware (`login_required`, role checks)
- Ban / activity logging on rate-limit abuse
- Error pages (404 / 429 / 500)

---

### Backend stack
- Python 3 + virtualenv (`.venv`)
- Flask 3.1
- Flask-SQLAlchemy + SQLAlchemy 2
- Flask-Migrate / Alembic (wired; schema not auto-created)
- PyMySQL → MySQL (`final_pp_ii_mmt`)
- Jinja2 templates (~70 HTML)
- python-dotenv (`.env`)
- requests (HTTP client)
- Pillow (image upload validation)
- WTForms / Flask-WTF
- email-validator
- cryptography / cffi
- Werkzeug

---

### Integrations (real / configured)
- **Stripe** Checkout Session + webhook signature verify
- **Visa** sandbox stub (`VISA_API_KEY`, keys dir, test mode)
- **Telegram Bot** alerts (404 / 429 / 500)

---

### Payments
- `CASH_ON_DELIVERY` (simulated)
- `CREDIT_CARD` / `QR_PAYMENT` (simulated)
- `STRIPE` (real Checkout)
- `VISA` (sandbox / simulated success in test mode)

---

### API endpoints used
- `POST /api/login` → JWT
- `GET /api/health`
- `GET /api/me`
- `GET /api/products`, `/api/products/<id>`
- `GET /api/categories`
- `GET /api/cart`, `/api/orders`

---

### Models
- User, Seller, SellerFollow, Address  
- Product, Category, Brand, Review  
- CartItem, WishlistItem, Notification  
- Order, OrderItem, Commission  
- ErrorLog, ActivityLog  
- MysqlBit type decorator  

---

### App modules / features
- Auth (login/register/logout)
- Storefront, cart, wishlist, checkout
- Account (orders, addresses, settings, notifications)
- Seller dashboard
- Admin (users, sellers, products, orders, countries, …)
- Search suggest
- File uploads (`upload_service`)
- Activity logger
- Telegram notify
- Country analytics service

---

### Frontend
- Jinja2 (from Blade)
- Custom CSS (`storefront.css`, `dashboard.css`, …)
- Vanilla JS (`storefront.js`, `product-form.js`)
- Chart.js 4.4.1 (CDN)
- Google Fonts (same storefront CSS pattern)

---

### Tooling / runtime
- `app.py` on **http://127.0.0.1:5000**
- `run.bat`
- Seed scripts (`seed_demo_users.py`, …)
- IntelliJ/PyCharm (`.idea`)
- Optional PHP helper (`clone_db.php`) for DB clone

---

**One line:** Flask monolith marketplace · Jinja SSR · session + CSRF for web · JWT for API · bcrypt · rate limits · Telegram · **real Stripe** · Visa stub · MySQL/SQLAlchemy · escrow orders.

*(My `.env` holds live-looking Stripe/Telegram/Visa secrets — don’t commit or share that file.)*