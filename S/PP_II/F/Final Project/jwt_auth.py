"""JWT helpers for /api (Bearer token)."""
from datetime import datetime, timedelta, timezone
from functools import wraps

import jwt
from flask import current_app, g, jsonify, request

from extensions import db
from models.user import User


def create_access_token(user):
    cfg = current_app.config
    now = datetime.now(timezone.utc)
    payload = {
        "sub": str(user.id),
        "email": user.email,
        "username": user.username,
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


def user_json(user):
    return {
        "id": user.id,
        "username": user.username,
        "email": user.email,
        "role": user.role,
        "profile": user.profile,
    }


def jwt_optional(view):
    @wraps(view)
    def wrapped(*args, **kwargs):
        g.api_user = None
        token = _bearer_token()
        if token:
            try:
                data = decode_access_token(token)
                g.api_user = db.session.get(User, int(data["sub"]))
            except Exception:
                g.api_user = None
        return view(*args, **kwargs)

    return wrapped


def jwt_required(view):
    """Require Authorization: Bearer <token>."""

    @wraps(view)
    def wrapped(*args, **kwargs):
        token = _bearer_token()
        if not token:
            return jsonify(
                error="missing_token",
                message="Authorization: Bearer <token> required",
            ), 401
        try:
            data = decode_access_token(token)
        except jwt.ExpiredSignatureError:
            return jsonify(error="token_expired", message="Token has expired"), 401
        except jwt.InvalidTokenError:
            return jsonify(error="invalid_token", message="Invalid token"), 401

        user = db.session.get(User, int(data["sub"]))
        if not user:
            return jsonify(error="forbidden", message="User not found"), 403
        g.api_user = user
        return view(*args, **kwargs)

    return wrapped
