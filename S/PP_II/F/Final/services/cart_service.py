"""Port of App\\Services\\CartService (+ variant unit_price)."""
from sqlalchemy.orm import joinedload

from extensions import db
from models import CartItem, Product
from models.base import utcnow
from services.product_variants import resolve_unit_price


def get_cart(user_id):
    return (
        CartItem.query.options(joinedload(CartItem.product).joinedload(Product.seller),
                               joinedload(CartItem.product).joinedload(Product.brand))
        .filter(CartItem.user_id == user_id)
        .order_by(CartItem.id.desc())
        .all()
    )


def get_cart_grouped_by_seller(user_id):
    """{seller_id: [CartItem, ...]} preserving newest-first order."""
    grouped = {}
    for ci in get_cart(user_id):
        grouped.setdefault(ci.product.seller_id, []).append(ci)
    return grouped


def get_selected(user_id):
    return (
        CartItem.query.options(joinedload(CartItem.product).joinedload(Product.seller))
        .filter(CartItem.user_id == user_id, CartItem.selected == 1)
        .all()
    )


def selected_subtotal(user_id):
    return sum(ci.line_unit_price() * ci.quantity for ci in get_selected(user_id))


def count(user_id):
    return int(db.session.query(db.func.coalesce(db.func.sum(CartItem.quantity), 0))
               .filter(CartItem.user_id == user_id).scalar() or 0)


def add_to_cart(user_id, product_id, quantity=1, selected_options=None, unit_price=None):
    product = db.session.get(Product, product_id)
    if product is None:
        raise LookupError("Product not found")

    opts = (selected_options or "").strip() or None
    if unit_price is None:
        price = resolve_unit_price(product, opts)
    else:
        try:
            price = round(float(unit_price), 2)
        except (TypeError, ValueError):
            price = resolve_unit_price(product, opts)

    # Same product + same selected options merge into one line
    q = CartItem.query.filter_by(user_id=user_id, product_id=product_id)
    if opts:
        q = q.filter(CartItem.selected_options == opts)
    else:
        q = q.filter(db.or_(CartItem.selected_options.is_(None), CartItem.selected_options == ""))
    item = q.first()
    if item:
        item.quantity += max(1, quantity)
        item.selected = True
        item.unit_price = price
        if opts:
            item.selected_options = opts
        db.session.commit()
        return item
    item = CartItem(
        user_id=user_id, product_id=product.id, quantity=max(1, quantity),
        selected=True, selected_options=opts, unit_price=price, created_at=utcnow(),
    )
    db.session.add(item)
    db.session.commit()
    return item


def update_quantity(item_id, quantity, user_id=None):
    item = db.session.get(CartItem, item_id)
    if item is None or (user_id is not None and item.user_id != user_id):
        raise LookupError("Cart item not found")
    if quantity <= 0:
        db.session.delete(item)
    else:
        item.quantity = quantity
    db.session.commit()


def toggle_select(item_id, user_id=None):
    item = db.session.get(CartItem, item_id)
    if item is None or (user_id is not None and item.user_id != user_id):
        raise LookupError("Cart item not found")
    item.selected = not item.selected
    db.session.commit()


def select_all(user_id, selected):
    CartItem.query.filter(CartItem.user_id == user_id).update(
        {CartItem.selected: 1 if selected else 0}, synchronize_session=False)
    db.session.commit()


def remove(item_id, user_id=None):
    q = CartItem.query.filter(CartItem.id == item_id)
    if user_id is not None:
        q = q.filter(CartItem.user_id == user_id)
    q.delete(synchronize_session=False)
    db.session.commit()
