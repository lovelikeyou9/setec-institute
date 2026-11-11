from functools import wraps

from flask import abort, flash, redirect, url_for
from flask_login import current_user

from extensions import db
from models import Cart


def admin_required(view):
    @wraps(view)
    def wrapped(*args, **kwargs):
        if not current_user.is_authenticated:
            return redirect(url_for("auth.login"))
        if not current_user.is_admin:
            abort(403)
        return view(*args, **kwargs)

    return wrapped


def get_or_create_cart(user):
    if not user.cart:
        cart = Cart(user_id=user.id)
        db.session.add(cart)
        db.session.commit()
        return cart
    return user.cart


def flash_form_errors(form):
    for field, errors in form.errors.items():
        for error in errors:
            flash(f"{getattr(form, field).label.text}: {error}", "danger")
