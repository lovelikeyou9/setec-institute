"""Port of Account\\AccountController."""
from datetime import datetime

from flask import abort, flash, redirect, render_template, request
from sqlalchemy.orm import joinedload

from activity_logger import log_activity
from auth_utils import hash_password, load_current_user, login_required
from blueprints.account import account_bp
from extensions import db
from helpers import back, back_with_errors, validate
from models import Address, Notification, Order, OrderItem, WishlistItem
from models.base import utcnow
from services import cart_service
from services import order_service
from upload_service import is_image_file, save_upload

PENDING = ["PENDING_PAYMENT", "PAYMENT_CONFIRMED", "FUNDS_HELD", "SELLER_PROCESSING", "SHIPPED", "OUT_FOR_DELIVERY"]


@account_bp.get("/account")
@account_bp.get("/dashboard")
@login_required
def dashboard():
    user = load_current_user()
    if user.isAdmin():
        return redirect("/admin")
    # Only APPROVED sellers enter Seller Center.
    if user.isSeller() and user.seller and user.seller.status == "APPROVED":
        return redirect("/seller-dashboard")

    orders = Order.query.filter_by(user_id=user.id).order_by(Order.placed_at.desc()).all()
    return render_template(
        "account/dashboard.html", orders=orders,
        totalOrders=len(orders),
        pendingOrders=sum(1 for o in orders if o.status in PENDING),
        completedOrders=sum(1 for o in orders if o.status in ("DELIVERED", "COMPLETED")),
        wishlistItems=WishlistItem.query.filter_by(user_id=user.id).count(),
    )


def _checkout_groups(order_rows):
    """Group orders that were placed together (same checkout / placed_at)."""
    groups, order_map = [], {}
    for o in order_rows:
        key = _ts(o.placed_at)
        if key not in order_map:
            order_map[key] = []
            groups.append(order_map[key])
        order_map[key].append(o)
    out = []
    for group in groups:
        first = group[0]
        sellers = []
        for o in group:
            name = o.seller.store_name if o.seller else "Seller"
            if name not in sellers:
                sellers.append(name)
        statuses = list(dict.fromkeys(o.status for o in group))
        out.append({
            "placed_at": first.placed_at,
            "placed_at_ts": _ts(first.placed_at),
            "orders": group,
            "seller_count": len(group),
            "seller_names": sellers,
            "statuses": statuses,
            "status_label": (statuses[0].replace("_", " ") if len(statuses) == 1
                             else f"{len(statuses)} statuses"),
            "total": sum(float(o.total or 0) for o in group),
            "payment": first.payment_method,
            "delivery": first.delivery_type,
            "primary_order": group[0],
        })
    return out


def _ts(dt):
    if not dt:
        return 0
    return int(dt.timestamp()) if hasattr(dt, "timestamp") else 0


def _orders_at_checkout(user_id, placed_at_ts):
    """Load every seller package for one customer checkout timestamp."""
    rows = (Order.query.options(
                joinedload(Order.seller),
                joinedload(Order.items),
                joinedload(Order.commission),
            )
            .filter(Order.user_id == user_id)
            .order_by(Order.id.asc()).all())
    return [o for o in rows if _ts(o.placed_at) == int(placed_at_ts)]


@account_bp.get("/account/orders")
@login_required
def orders():
    user = load_current_user()
    # Load all of the user's orders, then filter/group as checkouts (1 row per payment).
    q = (Order.query.options(joinedload(Order.seller), joinedload(Order.items))
         .filter(Order.user_id == user.id)
         .order_by(Order.placed_at.desc(), Order.id.asc()))
    rows = q.all()
    status = request.args.get("status") or None
    if status:
        # Keep the full checkout if any package matches the status filter.
        keep_keys = {_ts(o.placed_at) for o in rows if o.status == status}
        rows = [o for o in rows if _ts(o.placed_at) in keep_keys]
    checkouts = _checkout_groups(rows)
    sort = request.args.get("sort", "newest")
    if sort == "oldest":
        checkouts = list(reversed(checkouts))
    elif sort == "total_asc":
        checkouts = sorted(checkouts, key=lambda c: c["total"])
    elif sort == "total_desc":
        checkouts = sorted(checkouts, key=lambda c: c["total"], reverse=True)
    checkout_ts = request.args.get("checkout", type=int)
    return render_template(
        "account/orders.html",
        orders=rows,
        checkouts=checkouts,
        status=status,
        sort=sort,
        justPlaced=request.args.get("placed") in ("1", "true"),
        highlightCheckout=checkout_ts,
    )


@account_bp.get("/account/orders/checkout/<int:placed_at>")
@login_required
def order_checkout(placed_at):
    """One checkout = 1 payment; inside you see every seller package (1, 2, 3…)."""
    user = load_current_user()
    packages = _orders_at_checkout(user.id, placed_at)
    if not packages:
        abort(404)
    groups = _checkout_groups(packages)
    checkout = groups[0]
    return render_template(
        "account/order-checkout.html",
        checkout=checkout,
        orders=packages,
        justPlaced=request.args.get("placed") in ("1", "true"),
    )


@account_bp.get("/orders/<order_number>")
@login_required
def order_detail(order_number):
    user = load_current_user()
    order = (Order.query.options(joinedload(Order.items).joinedload(OrderItem.product), joinedload(Order.seller),
                                 joinedload(Order.commission), joinedload(Order.user))
             .filter(Order.order_number == order_number).first_or_404())
    owns_as_seller = bool(user.seller and user.seller.id == order.seller_id)
    if not user.isAdmin() and order.user_id != user.id and not owns_as_seller:
        abort(403)
    can_dispute = order_service.can_open_dispute(order) and order.user_id == user.id
    siblings = []
    checkout_ts = 0
    if order.placed_at and order.user_id == user.id:
        siblings = (Order.query.options(joinedload(Order.seller))
                    .filter(Order.user_id == order.user_id, Order.placed_at == order.placed_at)
                    .order_by(Order.id.asc()).all())
        checkout_ts = _ts(order.placed_at)
    return render_template(
        "account/order-detail.html", order=order, canOpenDispute=can_dispute,
        protectionDays=order_service.get_buyer_protection_days(),
        disputeReasons=["DAMAGED", "INCORRECT", "MISSING", "OTHER"],
        checkoutOrders=siblings,
        checkoutTs=checkout_ts,
        justPlaced=request.args.get("placed") in ("1", "true"),
        ownsAsSeller=owns_as_seller,
    )


@account_bp.post("/orders/<order_number>/dispute")
@login_required
def open_dispute(order_number):
    user = load_current_user()
    order = Order.query.filter_by(order_number=order_number).first_or_404()
    try:
        order_service.open_dispute(order.id, user, request.form.get("reason"), request.form.get("note"))
        log_activity("DISPUTE", "Orders", f"Dispute opened for {order_number}", "orders", order.id)
        return back("Dispute submitted. Your payment remains held until resolved.")
    except Exception as exc:
        db.session.rollback()
        return back(str(exc), "error")


@account_bp.get("/account/cart")
@login_required
def cart():
    user = load_current_user()
    return render_template("account/cart.html", groupedCart=cart_service.get_cart_grouped_by_seller(user.id),
                           subtotal=cart_service.selected_subtotal(user.id))


@account_bp.get("/account/wishlist")
@login_required
def wishlist():
    user = load_current_user()
    items = (WishlistItem.query.filter_by(user_id=user.id).order_by(WishlistItem.id.desc()).all())
    return render_template("account/wishlist.html", items=items)


# --------------------------------------------------------------- addresses
ADDRESS_RULES = {
    "full_name": "required|string|max:120",
    "phone": "required|string|max:30",
    "province": "required|string|max:80",
    "district": "required|string|max:80",
    "commune": "nullable|string|max:80",
    "street": "required|string|max:250",
    "country": "required|string|max:80",
}


@account_bp.get("/account/addresses")
@login_required
def addresses():
    user = load_current_user()
    rows = Address.query.filter_by(user_id=user.id).order_by(Address.default_address.desc()).all()
    return render_template("account/addresses.html", addresses=rows)


@account_bp.post("/account/addresses")
@login_required
def save_address():
    user = load_current_user()
    data, errors = validate(ADDRESS_RULES)
    if errors:
        return back_with_errors(errors, "/account/addresses")
    Address.query.filter_by(user_id=user.id).update({"default_address": 0})
    db.session.add(Address(user_id=user.id, default_address=True, created_at=utcnow(), **data))
    db.session.commit()
    return back("Address added successfully")


@account_bp.post("/account/addresses/<int:id>/edit")
@login_required
def update_address(id):
    user = load_current_user()
    addr = Address.query.filter_by(user_id=user.id, id=id).first_or_404()
    data, errors = validate(ADDRESS_RULES)
    if errors:
        return back_with_errors(errors, "/account/addresses")
    for k, v in data.items():
        setattr(addr, k, v)
    db.session.commit()
    return back("Address updated successfully")


@account_bp.post("/account/addresses/<int:id>/delete")
@login_required
def delete_address(id):
    user = load_current_user()
    Address.query.filter_by(user_id=user.id, id=id).delete()
    db.session.commit()
    return back("Address deleted successfully")


# ----------------------------------------------------------- notifications
@account_bp.get("/account/notifications")
@login_required
def notifications():
    user = load_current_user()
    rows = (Notification.query.filter_by(user_id=user.id)
            .order_by(Notification.created_at.desc()).limit(50).all())
    return render_template("account/notifications.html", notifications=rows)


@account_bp.post("/account/notifications/<int:id>/read")
@login_required
def mark_notification_read(id):
    user = load_current_user()
    n = Notification.query.filter_by(user_id=user.id, id=id).first_or_404()
    n.read_flag = True
    db.session.commit()
    if n.link and n.link.startswith("/") and not n.link.startswith("//"):
        return redirect(n.link)
    return back()


# ---------------------------------------------------------------- settings
@account_bp.get("/account/settings")
@login_required
def settings():
    return render_template("account/settings.html")


@account_bp.post("/account/settings")
@login_required
def update_settings():
    user = load_current_user()
    data, errors = validate({
        "full_name": "required|string|max:120",
        "phone": "nullable|string|max:30",
        "avatar_url": "nullable|string|max:500",
        "cover_url": "nullable|string|max:500",
        "password": "nullable|string|min:6|confirmed",
    })
    avatar, cover = request.files.get("avatar"), request.files.get("cover")
    if avatar and avatar.filename and not is_image_file(avatar, 4):
        errors.setdefault("avatar", []).append("The avatar field must be an image (max 4 MB).")
    if cover and cover.filename and not is_image_file(cover, 6):
        errors.setdefault("cover", []).append("The cover field must be an image (max 6 MB).")
    if errors:
        return back_with_errors(errors, "/account/settings")

    if avatar and avatar.filename:
        data["avatar_url"] = save_upload(avatar, "avatars")
    if cover and cover.filename:
        data["cover_url"] = save_upload(cover, "avatars/covers")

    user.full_name = data["full_name"]
    user.phone = data.get("phone") or user.phone
    if data.get("avatar_url"):
        user.avatar_url = data["avatar_url"]
    if data.get("cover_url"):
        user.cover_url = data["cover_url"]
    if data.get("password"):
        user.password = hash_password(data["password"])
    user.updated_at = utcnow()
    db.session.commit()
    log_activity("UPDATE", "Account", "Profile updated", "users", user.id)
    return back("Profile updated successfully")
