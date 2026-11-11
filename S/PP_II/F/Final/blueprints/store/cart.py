"""Port of Store\\CartController (cart + wishlist actions)."""
from flask import flash, redirect, request

from auth_utils import load_current_user, login_required
from blueprints.common import safe_redirect_target, to_int
from blueprints.store import store_bp
from extensions import db
from helpers import back_with_errors, validate
from models import Product, WishlistItem
from models.base import utcnow
from services import cart_service


def _form_product_id():
    return to_int(request.form.get("product_id") or request.form.get("productId"), 0)


def _redirect(default):
    return redirect(safe_redirect_target(request.form.get("redirect") or request.args.get("redirect"), default))


@store_bp.post("/cart/add")
@login_required
def cart_add():
    user = load_current_user()
    product_id = _form_product_id()
    data, errors = validate({"quantity": "nullable|integer|min:1"})
    if product_id < 1:
        errors["product_id"] = ["The product id field is required."]
    if errors:
        return back_with_errors(errors, "/products")
    if db.session.get(Product, product_id) is None:
        flash("Product not found.", "error")
        return redirect("/products")
    selected_options = (request.form.get("selected_options") or "").strip() or None
    unit_raw = request.form.get("unit_price")
    unit_price = None
    if unit_raw not in (None, ""):
        try:
            unit_price = float(unit_raw)
        except ValueError:
            unit_price = None
    cart_service.add_to_cart(
        user.id, product_id, int(data.get("quantity") or 1),
        selected_options=selected_options, unit_price=unit_price,
    )
    flash("Product added to cart", "message")
    return _redirect("/account/cart")


@store_bp.post("/cart/update")
@login_required
def cart_update():
    user = load_current_user()
    try:
        cart_service.update_quantity(to_int(request.form.get("itemId")), to_int(request.form.get("quantity")),
                                     user_id=user.id)
    except LookupError:
        flash("Cart item not found.", "error")
    return _redirect("/account/cart")


@store_bp.post("/cart/toggle")
@login_required
def cart_toggle():
    user = load_current_user()
    try:
        cart_service.toggle_select(to_int(request.form.get("itemId")), user_id=user.id)
    except LookupError:
        flash("Cart item not found.", "error")
    return _redirect("/account/cart")


@store_bp.post("/cart/select-all")
@login_required
def cart_select_all():
    user = load_current_user()
    selected = str(request.form.get("selected", "")).lower() in ("1", "true", "on", "yes")
    cart_service.select_all(user.id, selected)
    return _redirect("/account/cart")


@store_bp.post("/cart/remove")
@login_required
def cart_remove():
    user = load_current_user()
    cart_service.remove(to_int(request.form.get("itemId")), user_id=user.id)
    return _redirect("/account/cart")


@store_bp.get("/cart")
@login_required
def cart_redirect():
    return redirect("/account/cart")


@store_bp.get("/wishlist")
@login_required
def wishlist_redirect():
    return redirect("/account/wishlist")


@store_bp.post("/wishlist/toggle")
@login_required
def wishlist_toggle():
    user = load_current_user()
    product_id = _form_product_id()
    existing = WishlistItem.query.filter_by(user_id=user.id, product_id=product_id).first()
    if existing:
        db.session.delete(existing)
    elif db.session.get(Product, product_id) is not None:
        db.session.add(WishlistItem(user_id=user.id, product_id=product_id, created_at=utcnow()))
    db.session.commit()
    return _redirect("/account/wishlist")
