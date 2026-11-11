"""Session auth (web, teacher style) + JWT helpers (API) + role middleware."""
from datetime import datetime, timedelta, timezone
from functools import wraps

import bcrypt
import jwt
from flask import abort, current_app, flash, g, jsonify, redirect, request, session, url_for
from werkzeug.security import check_password_hash, generate_password_hash

from extensions import db
from models import AnonymousUser, User


# ---------------------------------------------------------------- passwords
def _is_bcrypt(h):
    return bool(h) and h[:4] in ("$2y$", "$2a$", "$2b$", "$2x$")


def hash_password(plain):
    """New hashes are bcrypt with the Laravel-compatible $2y$ prefix."""
    hashed = bcrypt.hashpw(plain.encode("utf-8"), bcrypt.gensalt(rounds=12)).decode("utf-8")
    return "$2y$" + hashed[4:]


def verify_password(stored, plain):
    """Accept Laravel/Spring bcrypt ($2y$/$2a$/$2b$) and werkzeug hashes."""
    if not stored or plain is None:
        return False
    try:
        if _is_bcrypt(stored):
            h = "$2b$" + stored[4:]
            return bcrypt.checkpw(plain.encode("utf-8")[:72], h.encode("utf-8"))
        return check_password_hash(stored, plain)
    except Exception:
        return False


def werkzeug_hash(plain):
    return generate_password_hash(plain)


# --------------------------------------------------------------- web session
def login_user(user, remember=False):
    session.clear()
    session.permanent = bool(remember)
    session["is_login"] = True
    session["user_id"] = int(user.id)
    session["username"] = user.full_name
    session["email"] = user.email
    session["role"] = user.role
    g.user = user


def logout_user():
    session.clear()
    g.user = None


def load_current_user():
    """Lazy-load the logged-in user once per request."""
    if hasattr(g, "user"):
        return g.user
    user = None
    uid = session.get("user_id")
    if uid:
        user = db.session.get(User, int(uid))
    g.user = user
    return user


def current_user_or_anon():
    return load_current_user() or AnonymousUser()


def role_of(user=None):
    user = user or load_current_user()
    return (user.role or "").upper() if user else ""


def login_required(view):
    """Laravel `auth` middleware."""

    @wraps(view)
    def wrapped(*args, **kwargs):
        user = load_current_user()
        if not user:
            if request.method == "GET":
                session["url.intended"] = request.full_path.rstrip("?")
            flash("Please log in.", "error")
            return redirect(url_for("auth.login"))
        return view(*args, **kwargs)

    return wrapped


def guest_only(view):
    """Laravel `guest` middleware."""

    @wraps(view)
    def wrapped(*args, **kwargs):
        if load_current_user():
            return redirect("/")
        return view(*args, **kwargs)

    return wrapped


def role_required(*roles):
    """Port of App\\Http\\Middleware\\RoleMiddleware (auth + role:A,B)."""
    allowed = [r.upper() for r in roles]

    def decorator(view):
        @wraps(view)
        def wrapped(*args, **kwargs):
            user = load_current_user()
            if not user:
                if request.method == "GET":
                    session["url.intended"] = request.full_path.rstrip("?")
                flash("Please log in.", "error")
                return redirect(url_for("auth.login"))
            if not user.enabled:
                logout_user()
                flash("Your account is disabled.", "error")
                return redirect(url_for("auth.login"))
            role = (user.role or "").upper()
            if role not in allowed:
                abort(403, "Unauthorized for this area.")
            # Banned / pending sellers must not use Seller Center (admins still can).
            if role == "SELLER" and "SELLER" in allowed:
                seller = user.seller
                if not seller or seller.status != "APPROVED":
                    status = seller.status if seller else ""
                    msg = {
                        "SUSPENDED": "Your seller store has been BANNED by an administrator. Selling is disabled.",
                        "BANNED": "Your seller store has been BANNED by an administrator. Selling is disabled.",
                        "PENDING": "Your seller application is still pending approval.",
                        "REJECTED": "Your seller application was rejected.",
                    }.get((status or "").upper(), "Seller account required.")
                    flash(msg, "error")
                    return redirect("/account")
            return view(*args, **kwargs)

        return wrapped

    return decorator


def ban_check():
    """before_request: a disabled account is logged out immediately."""
    user = load_current_user()
    if user is not None and not user.enabled:
        logout_user()
        flash("Your account is disabled.", "error")
        return redirect(url_for("auth.login"))
    return None


def intended(default="/"):
    return session.pop("url.intended", None) or default


# ----------------------------------------------------------------------- JWT
def create_access_token(user):
    cfg = current_app.config
    now = datetime.now(timezone.utc)
    payload = {
        "sub": str(user.id),
        "email": user.email,
        "role": user.role,
        "iat": now,
        "exp": now + timedelta(hours=cfg["JWT_EXPIRE_HOURS"]),
    }
    return jwt.encode(payload, cfg["JWT_SECRET_KEY"], algorithm="HS256")


def decode_access_token(token):
    return jwt.decode(token, current_app.config["JWT_SECRET_KEY"], algorithms=["HS256"])


def _bearer_token():
    header = request.headers.get("Authorization", "")
    if header.lower().startswith("bearer "):
        return header[7:].strip()
    return None


def jwt_optional(view):
    """Populate g.api_user when a valid Bearer token is present."""

    @wraps(view)
    def wrapped(*args, **kwargs):
        g.api_user = None
        token = _bearer_token()
        if token:
            try:
                data = decode_access_token(token)
                user = db.session.get(User, int(data["sub"]))
                if user and user.enabled:
                    g.api_user = user
            except Exception:
                g.api_user = None
        return view(*args, **kwargs)

    return wrapped


def jwt_required(view):
    """Require `Authorization: Bearer <token>`."""

    @wraps(view)
    def wrapped(*args, **kwargs):
        token = _bearer_token()
        if not token:
            return jsonify(error="missing_token", message="Authorization: Bearer <token> required"), 401
        try:
            data = decode_access_token(token)
        except jwt.ExpiredSignatureError:
            return jsonify(error="token_expired", message="Token has expired"), 401
        except jwt.InvalidTokenError:
            return jsonify(error="invalid_token", message="Invalid token"), 401
        user = db.session.get(User, int(data["sub"]))
        if not user or not user.enabled:
            return jsonify(error="forbidden", message="Account disabled or not found"), 403
        g.api_user = user
        return view(*args, **kwargs)

    return wrapped
