"""JWT API auth routes."""
from flask import current_app, g, jsonify, request
from werkzeug.security import check_password_hash

from api import api_bp
from extensions import db, limiter
from jwt_auth import create_access_token, jwt_required, user_json
from models.user import User
from activity_logger import log_activity


@api_bp.get("/health")
def health():
    try:
        db.session.execute(db.text("SELECT 1"))
        db_ok = True
    except Exception:
        db_ok = False
    return jsonify(status="ok" if db_ok else "degraded", app="VoltEdge", database=db_ok)


@api_bp.post("/login")
@limiter.limit(lambda: current_app.config.get("LOGIN_LIMIT", "5 per minute"))
def api_login():
    body = request.get_json(silent=True) or request.form or {}
    email = (body.get("email") or "").strip().lower()
    username = (body.get("username") or "").strip()
    password = body.get("password") or ""

    if not password or (not email and not username):
        return jsonify(
            error="validation",
            message="email (or username) and password are required",
        ), 422

    user = None
    if email:
        user = User.query.filter(db.func.lower(User.email) == email).first()
    if not user and username:
        user = User.query.filter_by(username=username).first()

    if not user or not check_password_hash(user.password, password):
        log_activity(
            action="LOGIN_FAILED",
            module="API",
            description=f"API login failed for {email or username}",
        )
        return jsonify(error="invalid_credentials", message="Invalid email/username or password"), 401

    token = create_access_token(user)
    log_activity(
        action="LOGIN",
        module="API",
        table_name="user",
        record_id=user.id,
        description=f"API JWT login {user.username}",
        user_id=user.id,
        username=user.username,
    )
    return jsonify(
        access_token=token,
        token_type="Bearer",
        expires_in=current_app.config["JWT_EXPIRE_HOURS"] * 3600,
        user=user_json(user),
    )


@api_bp.get("/me")
@jwt_required
def api_me():
    return jsonify(user=user_json(g.api_user))
