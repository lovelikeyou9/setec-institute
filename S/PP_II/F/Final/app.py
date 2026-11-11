"""MMT - Modern Multiple Technology  (Flask port of the Laravel KhmerStore app).

Run:  .venv\\Scripts\\python.exe app.py        (http://127.0.0.1:5000)
  or  flask --app app run
"""
import os
from urllib.parse import urlparse

from flask import (
    Flask, flash, g, redirect, render_template, request, send_from_directory, session,
)
from flask_wtf.csrf import CSRFError

from auth_utils import ban_check, current_user_or_anon, load_current_user
from config import Config
from extensions import cors, csrf, db, is_asset_request, limiter, migrate
from helpers import FormErrors, register_template_helpers
from telegram_notify import send_telegram

app = Flask(__name__, static_folder="static", static_url_path="/static")
app.url_map.strict_slashes = False
app.config.from_object(Config)

db.init_app(app)
migrate.init_app(app, db)          # kept for teacher parity - the schema is NOT created by Migrate
limiter.init_app(app)              # test_3_F
csrf.init_app(app)                 # test_4_F
cors.init_app(app, resources={     # test_5_F
    r"/api/*": {"origins": ["http://localhost:4200", "http://127.0.0.1:4200",
                            "http://localhost:3000", "http://127.0.0.1:5000"]}
})

# load models (reflect the existing MySQL tables)
import models  # noqa: E402,F401
from models import (  # noqa: E402
    CartItem, Category, ErrorLog, Notification, WishlistItem,
)
from activity_logger import log_activity  # noqa: E402

register_template_helpers(app)


# ----------------------------------------------------------------- blueprints
from blueprints.auth import auth_bp  # noqa: E402
from blueprints.store import store_bp  # noqa: E402
from blueprints.account import account_bp  # noqa: E402
from blueprints.seller import seller_bp  # noqa: E402
from blueprints.admin import admin_bp  # noqa: E402
from blueprints.api import api_bp  # noqa: E402
from blueprints.store.checkout import stripe_webhook  # noqa: E402

app.register_blueprint(auth_bp)
app.register_blueprint(store_bp)
app.register_blueprint(account_bp)
app.register_blueprint(seller_bp, url_prefix="/seller-dashboard")
app.register_blueprint(admin_bp, url_prefix="/admin")
app.register_blueprint(api_bp, url_prefix="/api")

# CSRF: all web POST forms are protected; the JSON API (JWT) and Stripe webhook are exempt
csrf.exempt(api_bp)
csrf.exempt(stripe_webhook)


# --------------------------------------------------------- Laravel-style assets
# Laravel serves public/css, public/js, public/images and public/uploads at the web root.
def _static_sub(sub):
    def view(filename):
        return send_from_directory(os.path.join(app.static_folder, sub), filename)
    view.__name__ = f"asset_{sub}"
    return view


for _sub in ("css", "js", "images", "uploads"):
    _view = limiter.exempt(_static_sub(_sub))
    app.add_url_rule(f"/{_sub}/<path:filename>", endpoint=f"asset_{_sub}", view_func=_view)


@app.get("/search/suggest")
@limiter.exempt
def search_suggest():
    """storefront.js autocomplete."""
    from models import Product
    q = (request.args.get("q") or "").strip()
    if len(q) < 2:
        return []
    rows = (Product.query.filter(Product.active == 1, Product.name.like(f"%{q}%"))
            .order_by(Product.sold_count.desc()).limit(6).all())
    return [p.name for p in rows]


# ------------------------------------------------------------- request hooks
@app.before_request
def _load_flash_state():
    if is_asset_request():
        return None
    g.old_input = session.pop("_old", None) or {}
    g.form_errors = FormErrors(session.pop("_errors", None) or {})
    if request.path.startswith("/api/"):
        return None
    return ban_check()


# ---------------------------------------------------------- context processor
def _nav_menu_groups(by_slug):
    groups = [
        ("Computers & Components", "\U0001F4BB", [
            "computers", "gaming-pc", "laptop", "cpu", "gpu", "motherboard", "ram", "ssd", "hdd",
            "power-supply", "pc-case", "cooling", "monitor", "keyboard", "mouse", "headset",
            "accessories"]),
        ("Networking & Wi-Fi", "\U0001F4E1", ["networking", "wifi-routers", "network-switches", "nas-storage"]),
        ("Gaming Consoles", "\U0001F3AE", ["gaming-consoles", "controllers", "game-cards"]),
        ("Cameras, Video & Drones", "\U0001F4F7", ["cameras", "action-cams", "drones", "capture-cards",
                                                    "security-cameras"]),
        ("Power & Charging", "\u26A1", ["chargers", "ups", "power-banks", "cables"]),
        ("Electronics, Robotics & DIY", "\U0001F916", ["microcontrollers", "audio", "earbuds", "smart-tv"]),
        ("Servers & Professional IT", "\U0001F5A5\uFE0F", ["servers", "workstations", "enterprise-networking"]),
    ]
    out = []
    for title, icon, slugs in groups:
        items = []
        for slug in slugs:
            c = by_slug.get(slug)
            if c:
                items.append({"id": c.id, "name": c.name, "slug": c.slug,
                              "icon": c.icon or icon, "image_url": c.image_url})
        if items:
            out.append({"title": title, "icon": icon, "items": items})
    return out


@app.context_processor
def inject_globals():
    user = current_user_or_anon()
    ctx = {
        "current_user": user,
        "errors": getattr(g, "form_errors", FormErrors()),
        "stripe_publishable_key": app.config.get("STRIPE_PUBLISHABLE_KEY", ""),
        "navCategories": [], "navMenuGroups": [],
        "cartCount": 0, "wishlistCount": 0, "wishlistProductIds": [], "unreadNotifications": 0,
    }
    try:
        cats = Category.query.filter(Category.active == 1).order_by(Category.sort_order).all()
        ctx["navCategories"] = cats[:12]
        ctx["navMenuGroups"] = _nav_menu_groups({c.slug: c for c in cats})
        if user.is_authenticated:
            ctx["cartCount"] = CartItem.query.filter_by(user_id=user.id).count()
            ids = [r[0] for r in db.session.query(WishlistItem.product_id).filter_by(user_id=user.id).all()]
            ctx["wishlistProductIds"] = [int(i) for i in ids]
            ctx["wishlistCount"] = len(ids)
            ctx["unreadNotifications"] = Notification.query.filter_by(user_id=user.id, read_flag=0).count()
    except Exception:
        db.session.rollback()
    return ctx


# ==================================================
# test_3_F_homework   # telegram notification ( 404, 429, 500 )
# Browser auto-requests (favicon, Chrome F12 / Inspect, missing CSS, etc.)
# can 404 in the background while the page you see is still OK.
# Only notify for real missing pages like /fdgs or /op231.
SKIP_NOTIFY_EXT = (
    ".ico", ".css", ".js", ".map", ".png", ".jpg", ".jpeg",
    ".gif", ".svg", ".webp", ".avif", ".woff", ".woff2", ".ttf", ".eot",
    ".txt", ".xml", ".json", ".webmanifest", ".html", ".htm",
)

# Exact paths browsers / DevTools often probe automatically.
SKIP_NOTIFY_PATHS = {
    "/favicon.ico", "/robots.txt", "/sitemap.xml", "/browserconfig.xml", "/manifest.json",
    "/site.webmanifest", "/apple-touch-icon.png", "/apple-touch-icon-precomposed.png",
}


def should_notify_error(code=None):
    """True only for real page visits to missing URLs - not F12 / asset probes."""
    path = (request.path or "").lower().rstrip("/") or "/"
    method = (request.method or "GET").upper()

    # Static / asset noise
    if path.startswith(("/static/", "/uploads/", "/css/", "/js/", "/images/")) or path == "/static":
        return False
    # Chrome F12 / Inspect: GET /.well-known/appspecific/com.chrome.devtools.json
    if path.startswith("/.well-known/"):
        return False
    if path in SKIP_NOTIFY_PATHS:
        return False
    if path.endswith(SKIP_NOTIFY_EXT):
        return False
    # Any file-like path (has an extension) - not a typed page like /fdgs
    last = path.rsplit("/", 1)[-1]
    if "." in last:
        return False

    # For 404: only alert when the user actually navigated to a missing page.
    if code == 404:
        if method not in ("GET", "HEAD"):
            return False
        dest = (request.headers.get("Sec-Fetch-Dest") or "").lower()
        mode = (request.headers.get("Sec-Fetch-Mode") or "").lower()
        if dest and dest != "document":
            return False
        if mode and mode != "navigate":
            return False
        if not dest and not mode:
            accept = (request.headers.get("Accept") or "").lower()
            if "text/html" not in accept:
                return False
    return True


def notify_error(code, title, error):
    if not should_notify_error(code):
        return
    path = request.path
    method = request.method
    ip = request.remote_addr or "unknown"
    detail = getattr(error, "description", None) or str(error)

    message = f"<b>\u26A0\uFE0F {title} ({code})</b>\n"
    message += f"<code>\U0001F517 URL: {method} {path}</code>\n"
    message += f"<code>\U0001F310 IP: {ip}</code>\n"
    message += f"<code>\U0001F464 User: {session.get('username') or 'guest'}</code>\n"
    message += "<i>================================</i>\n"
    message += f"<code>\u2139\uFE0F Detail: {detail}</code>"
    send_telegram(message)


def _wants_json():
    return request.path.startswith("/api/")


@app.errorhandler(404)
def page_not_found(e):
    if _wants_json():
        return {"error": "not_found", "message": "Resource not found"}, 404
    notify_error(404, "Page Not Found", e)
    return render_template("error/404.html"), 404


@app.errorhandler(403)
def forbidden(e):
    if _wants_json():
        return {"error": "forbidden", "message": getattr(e, "description", "Forbidden")}, 403
    return render_template("error/403.html", message=getattr(e, "description", "")), 403


@app.errorhandler(429)
def too_many_requests(e):
    """Refresh / login ban: Telegram + error_log + activity_logs (BAN) + 429 page."""
    login_ban = request.path.rstrip("/") == "/login"
    title = "Login Ban - Too Many Login Attempts" if login_ban else "Too Many Requests (Refresh Ban)"
    notify_error(429, title, e)

    try:
        db.session.rollback()
        db.session.add(ErrorLog(
            user_id=session.get("user_id"),
            status_code=429,
            path=(request.path or "")[:500],
            method=request.method,
            ip=request.remote_addr or "unknown",
            message=str(e)[:2000],
        ))
        db.session.commit()
    except Exception:
        db.session.rollback()

    log_activity(
        action="BAN",
        module="Auth" if login_ban else "Store",
        table_name="error_log",
        record_id=session.get("user_id"),
        description=f"User banned (429) for too many requests on {request.path}",
        new_data={"status_code": 429, "path": request.path, "method": request.method,
                  "detail": str(e)},
    )
    if _wants_json():
        return {"error": "too_many_requests", "message": str(e)}, 429
    return render_template("error/429.html", login_ban=login_ban), 429


@app.errorhandler(500)
def server_error(e):
    try:
        db.session.rollback()
    except Exception:
        pass
    notify_error(500, "Server Error", getattr(e, "original_exception", None) or e)
    if _wants_json():
        return {"error": "server_error", "message": "Internal server error"}, 500
    return render_template("error/500.html"), 500


@app.errorhandler(CSRFError)
def csrf_error(e):
    """Missing/invalid CSRF token.

    Always return HTTP 400 (do not 302 to home) so Postman / curl clearly show
    that the request was blocked. Browsers get error/400.html; API-style clients
    (Accept: application/json or /api/*) get JSON.
    """
    detail = getattr(e, "description", None) or "The CSRF token is missing or invalid."
    accept = (request.headers.get("Accept") or "").lower()
    if _wants_json() or "application/json" in accept:
        return {"error": "csrf", "message": detail}, 400
    return render_template("error/400.html", message=detail), 400
# ==================================================


if __name__ == "__main__":
    app.run(host="127.0.0.1", port=5000,
            debug=os.getenv("FLASK_DEBUG", "0") == "1")
