from flask import session, request
from flask_cors import CORS
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address
from flask_migrate import Migrate
from flask_sqlalchemy import SQLAlchemy
from flask_wtf.csrf import CSRFProtect

db = SQLAlchemy()
migrate = Migrate()

# Paths that must never count against the page-request limits
_ASSET_PREFIXES = ("/static/", "/uploads/", "/css/", "/js/", "/images/")


def is_asset_request():
    path = request.path or ""
    return path.startswith(_ASSET_PREFIXES) or request.endpoint == "static"


# test_5_F homework: ban per logged-in user_id (fall back to IP when anonymous)
def get_user_id():
    user_id = session.get("user_id")
    if user_id:
        return f"user:{user_id}"
    return get_remote_address()


# test_3_F: 10,000/day and 100/minute for everybody (assets are exempt so one
# page full of product images does not burn the whole minute quota)
limiter = Limiter(
    key_func=get_remote_address,
    default_limits=["10000 per day", "100 per minute"],
    default_limits_exempt_when=is_asset_request,
    storage_uri="memory://",
)

# test_4_F
csrf = CSRFProtect()

# test_5_F
cors = CORS()
from flask import session, current_app


def _refresh_limit():
    return current_app.config.get("REFRESH_LIMIT", "10 per minute")


def _not_browsing_customer():
    """Only logged-in CUSTOMER / SELLER browsing is rate limited by the 'refresh' ban."""
    if not session.get("user_id"):
        return True
    return (session.get("role") or "").upper() == "ADMIN"


# GET page refresh limit shared by store/account/seller blueprints (keyed by user id).
refresh_limit = limiter.limit(
    _refresh_limit,
    key_func=get_user_id,
    scope="refresh",
    methods=["GET"],
    exempt_when=_not_browsing_customer,
    override_defaults=False,
)
