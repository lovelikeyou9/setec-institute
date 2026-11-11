"""Port of Auth\\AuthController (login / register / logout)."""
from flask import current_app, flash, redirect, render_template, request

from activity_logger import log_activity
from auth_utils import (
    guest_only, intended, login_required, login_user, logout_user,
)
from blueprints.auth import auth_bp
from extensions import db, limiter
from helpers import back_with_errors, back_with_input, to_bool, validate
from models import User
from models.base import utcnow


def _login_limit():
    return current_app.config.get("LOGIN_LIMIT", "5 per minute")


@auth_bp.get("/login")
@guest_only
def login():
    return render_template("auth/login.html")


@auth_bp.post("/login")
@limiter.limit(_login_limit)            # test_3_F: login brute-force ban (429 + Telegram)
@guest_only
def login_post():
    data, errors = validate({"email": "required|email", "password": "required"})
    if errors:
        return back_with_errors(errors, "/login")

    user = User.query.filter(User.email == data["email"]).first()
    if user and not user.enabled:
        flash("Your account is disabled.", "error")
        return back_with_input("/login")

    if user and user.check_password(request.form.get("password", "")):
        login_user(user, remember=to_bool(request.form.get("remember")))
        log_activity("LOGIN", "Auth", f"User '{user.email}' logged in", "users", user.id, user_id=user.id)

        if user.isAdmin():
            return redirect(intended("/admin"))
        # Approved sellers -> Seller Center. Suspended stores keep login as customers.
        if user.isApprovedSeller():
            return redirect(intended("/seller-dashboard"))
        seller = user.seller
        if seller and (seller.status or "").upper() in ("SUSPENDED", "BANNED"):
            flash(
                "Your seller store has been BANNED by an administrator. "
                "You can still log in and shop as a customer, but you cannot sell "
                "until an admin restores your store.",
                "error",
            )
            return redirect("/account")
        return redirect(intended("/account"))

    log_activity("LOGIN_FAILED", "Auth", f"Failed login for '{data['email']}'", "users",
                 user.id if user else None, user_id=user.id if user else None)
    flash("Invalid email or password.", "error")
    return back_with_input("/login")


@auth_bp.get("/register")
@guest_only
def register():
    return render_template("auth/register.html")


@auth_bp.post("/register")
@guest_only
def register_post():
    data, errors = validate(
        {
            "full_name": "required|string|max:120",
            "email": "required|email|max:100|unique",
            "phone": "nullable|string|max:30",
            "password": "required|string|min:6|confirmed",
        },
        unique={"email": lambda v: User.query.filter(User.email == v).first() is not None},
    )
    if errors:
        return back_with_errors(errors, "/register")

    user = User(
        full_name=data["full_name"], email=data["email"], phone=data.get("phone"),
        role="CUSTOMER", enabled=True, created_at=utcnow(), updated_at=utcnow(),
    )
    user.set_password(request.form["password"])
    db.session.add(user)
    db.session.commit()

    login_user(user)
    log_activity("REGISTER", "Auth", f"New customer '{user.email}' registered", "users", user.id,
                 user_id=user.id)
    flash("Welcome to MMT!", "message")
    return redirect("/account")


@auth_bp.post("/logout")
@login_required
def logout():
    from flask import session
    uid, email = session.get("user_id"), session.get("email")
    log_activity("LOGOUT", "Auth", f"User '{email}' logged out", "users", uid, user_id=uid)
    logout_user()
    flash("Logged out.", "message")
    return redirect("/")
