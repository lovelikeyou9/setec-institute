from flask import Blueprint, flash, redirect, request, url_for, render_template
from flask_login import login_required, current_user

from extensions import db
from models import Product, CartItem
from utils import get_or_create_cart

cart_bp = Blueprint("cart", __name__)


@cart_bp.route("/cart")
@login_required
def view_cart():
    from flask import current_app

    cart = get_or_create_cart(current_user)
    shipping = current_app.config["SHIPPING_COST"] if cart.items else 0
    subtotal = cart.subtotal
    total = subtotal + shipping
    return render_template(
        "cart.html",
        cart=cart,
        subtotal=subtotal,
        shipping=shipping,
        total=total,
    )


@cart_bp.route("/cart/add", methods=["POST"])
@login_required
def add_to_cart():
    product_id = request.form.get("product_id", type=int)
    quantity = request.form.get("quantity", 1, type=int) or 1
    buy_now = request.form.get("buy_now") == "1"

    product = Product.query.get_or_404(product_id)
    if not product.in_stock:
        flash("This product is out of stock.", "warning")
        return redirect(request.referrer or url_for("product.product_list"))

    if quantity < 1:
        quantity = 1
    if quantity > product.stock_quantity:
        quantity = product.stock_quantity
        flash(f"Only {product.stock_quantity} units available.", "warning")

    cart = get_or_create_cart(current_user)
    item = CartItem.query.filter_by(cart_id=cart.id, product_id=product.id).first()
    if item:
        item.quantity = min(item.quantity + quantity, product.stock_quantity)
    else:
        db.session.add(CartItem(cart_id=cart.id, product_id=product.id, quantity=quantity))

    db.session.commit()
    flash(f"Added {product.name} to your cart.", "success")

    if buy_now:
        return redirect(url_for("checkout.checkout"))
    return redirect(request.referrer or url_for("cart.view_cart"))


@cart_bp.route("/cart/update", methods=["POST"])
@login_required
def update_cart():
    item_id = request.form.get("item_id", type=int)
    quantity = request.form.get("quantity", type=int)
    cart = get_or_create_cart(current_user)
    item = CartItem.query.filter_by(id=item_id, cart_id=cart.id).first_or_404()

    if quantity is None or quantity < 1:
        db.session.delete(item)
        flash("Item removed from cart.", "info")
    else:
        max_qty = item.product.stock_quantity
        item.quantity = min(quantity, max_qty)
        flash("Cart updated.", "success")

    db.session.commit()
    return redirect(url_for("cart.view_cart"))


@cart_bp.route("/cart/remove", methods=["POST"])
@login_required
def remove_from_cart():
    item_id = request.form.get("item_id", type=int)
    cart = get_or_create_cart(current_user)
    item = CartItem.query.filter_by(id=item_id, cart_id=cart.id).first_or_404()
    db.session.delete(item)
    db.session.commit()
    flash("Item removed from cart.", "info")
    return redirect(url_for("cart.view_cart"))
