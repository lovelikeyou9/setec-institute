# [ request ] used to get data from the URL (GET request)
# example: http://127.0.0.1:5000/cart?product_id=1

# make_response() is used to create and control the HTTP response before sending it back to the browser.
# render_template() is used to show an HTML file (page) in Flask and send data to it.
from flask import Flask, render_template, request, session

from config import Config

# test_4_F # test_5_F
from extensions import db, migrate, limiter, csrf, core

from front import front_bp
from admin import admin_bp
from api import api_bp

from telegram_notify import send_telegram
from models.error_log import ErrorLog
from activity_logger import log_activity    # test_5_F

app = Flask(__name__) # when it is running. it start from [ if __name__ = '__main__' ]
app.config.from_object(Config)

db.init_app(app)
migrate.init_app(app, db)
# test_3_F
limiter.init_app(app)

# test_4_F
csrf.init_app(app)

# test_5_F Core / Cors
# Enable CORS for Specific Routes
core.init_app(app, resources= {
    # r"/api/*": {"origins": "http://127.0.0.1:5001"}
    # r"/api/*": {"origins": "http://localhost:5001"}
    r"/api/*": {"origins": "http://localhost:4200"}
})


# load model
import models

app.register_blueprint(front_bp, url_prefix="/")
app.register_blueprint(admin_bp, url_prefix="/admin")
app.register_blueprint(api_bp, url_prefix="/api")


# ==================================================
# test_3_F_homework   # telegram notification ( 404, 429, 500 )
# Browser auto-requests (favicon, Chrome F12 / Inspect, missing CSS, etc.)
# can 404 in the background while the page you see is still OK.
# Only notify for real missing pages like /fdgs or /op231.
SKIP_NOTIFY_EXT = (
    ".ico", ".css", ".js", ".map", ".png", ".jpg", ".jpeg",
    ".gif", ".svg", ".webp", ".woff", ".woff2", ".ttf", ".eot",
    ".txt", ".xml", ".json", ".webmanifest", ".html", ".htm",
)

# Exact paths browsers / DevTools often probe automatically.
SKIP_NOTIFY_PATHS = {
    "/favicon.ico",
    "/robots.txt",
    "/sitemap.xml",
    "/browserconfig.xml",
    "/manifest.json",
    "/site.webmanifest",
    "/apple-touch-icon.png",
    "/apple-touch-icon-precomposed.png",
}


def should_notify_error(code=None):
    """True only for real page visits to missing URLs — not F12 / asset probes."""
    path = (request.path or "").lower().rstrip("/") or "/"
    method = (request.method or "GET").upper()

    # Static / asset noise
    if path.startswith("/static/") or path == "/static":
        return False
    # Chrome F12 / Inspect: GET /.well-known/appspecific/com.chrome.devtools.json
    if path.startswith("/.well-known/"):
        return False
    if path in SKIP_NOTIFY_PATHS:
        return False
    if path.endswith(SKIP_NOTIFY_EXT):
        return False
    # Any file-like path (has an extension) — not a typed page like /fdgs
    last = path.rsplit("/", 1)[-1]
    if "." in last:
        return False

    # For 404: only alert when the user actually navigated to a missing page.
    # Opening Inspect / F12 fires background requests (Sec-Fetch-Dest: empty),
    # not document navigations — those must not spam Telegram.
    if code == 404:
        if method not in ("GET", "HEAD"):
            return False

        dest = (request.headers.get("Sec-Fetch-Dest") or "").lower()
        mode = (request.headers.get("Sec-Fetch-Mode") or "").lower()

        # Modern Chrome / Edge send these on every request.
        if dest:
            if dest != "document":
                return False
        if mode:
            if mode != "navigate":
                return False

        # Older / non-browser clients: only treat HTML page requests as real visits.
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
    detail = str(error)

    message = f"<b>⚠️ {title} ({code})</b>\n"
    message += f"<code>🔗 URL: {method} {path}</code>\n"
    message += f"<code>🌐 IP: {ip}</code>\n"
    message += f"<i>================================</i>\n"
    message += f"<code>ℹ️ Detail: {detail}</code>"
    send_telegram(message)


# test_3_F
# [ @app ] means you are using the Flask application object.
# [ errorhandler() ] is used in Flask to define what should happen when a specific error occurs.
# [ (e) ] is the error object that Flask automatically gives to your error-handling function.
@app.errorhandler(404)    # 404 is number status   # [ (404) ] I want this function to handle HTTP 404 errors.
def page_not_found(e):    # [ e ]
    notify_error(404, "Page Not Found", e)
    return render_template('error/404.html'), 404 # [ , 404 ] Send HTTP status code 404 back to the browser.

# test_3_F
@app.errorhandler(429)    # 429 Too Many Requests
def to_many_requests(e):
    notify_error(429, "Too Many Requests", e)

    # test_5_F homework — save ban into error_log (for report)
    try:
        log = ErrorLog(
            user_id=session.get("user_id"),
            status_code=429,
            path=request.path,
            method=request.method,
            ip=request.remote_addr or "unknown",
            message=str(e),
        )
        db.session.add(log)
        db.session.commit()
    except Exception:
        db.session.rollback()

    # also save into activity_logs (audit trail)
    log_activity(
        action="BAN",
        module="Admin",
        table_name="error_log",
        record_id=session.get("user_id"),
        description=f"User banned (429) for too many requests on {request.path}",
        new_data={
            "status_code": 429,
            "path": request.path,
            "method": request.method,
            "detail": str(e),
        },
    )

    return render_template('error/429.html'), 429

# test_3_F
@app.errorhandler(500)    # 500 Server Error
def server_error(e):
    notify_error(500, "Server Error", e)
    return render_template('error/500.html'), 500
# ==================================================


if __name__ == '__main__':
    app.run()
