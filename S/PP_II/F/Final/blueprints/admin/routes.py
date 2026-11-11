"""Port of Admin\\AdminController (all routes under /admin)."""
import calendar
from datetime import datetime, timezone

from flask import abort, flash, redirect, render_template, request
from sqlalchemy import func
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import joinedload

from activity_logger import log_activity
from auth_utils import hash_password
from blueprints.admin import admin_bp
from blueprints.forms import (
    PRODUCT_EXISTS, PRODUCT_MESSAGES, PRODUCT_RULES, build_gallery, collect_images, get_file,
    normalized_form, parse_gallery_keep, pricing, store_images, validate_images, variant_fields,
)
from extensions import db
from helpers import back, back_with_errors, paginate, slugify, to_bool, validate
from models import Brand, Category, Commission, Order, Product, Review, Seller, User
from models.base import utcnow
from services import country_service, order_service
from upload_service import is_image_file, save_upload

HELD = ("HELD", "IN_PROTECTION")
COUNTRY_OPTIONS = ["Cambodia", "Thailand", "Vietnam", "Laos", "Myanmar", "Singapore", "Malaysia", "Indonesia",
                   "Philippines", "China", "Japan", "South Korea", "United States", "Other"]


def _f(v):
    return float(v or 0)


def _safe_delete(model, id, label):
    try:
        model.query.filter(model.id == id).delete(synchronize_session=False)
        db.session.commit()
        log_activity("DELETE", label, f"Deleted {label} #{id}", model.__tablename__, id)
        return back(f"{label} deleted")
    except IntegrityError:
        db.session.rollback()
        return back(f"This {label.lower()} is still referenced by other records and cannot be deleted.", "error")


# --------------------------------------------------------------- dashboard
@admin_bp.get("/")
def dashboard():
    year = datetime.now(timezone.utc).year
    active_orders = Order.status.notin_(["CANCELLED", "REFUNDED"])

    def commission_sum(col, statuses):
        return _f(db.session.query(func.coalesce(func.sum(col), 0)).filter(Commission.payout_status.in_(statuses)).scalar())

    def commission_count(statuses):
        return Commission.query.filter(Commission.payout_status.in_(statuses)).count()

    stats = {
        "users": User.query.count(),
        "customers": User.query.filter_by(role="CUSTOMER").count(),
        "sellers": Seller.query.filter_by(status="APPROVED").count(),
        "pendingSellers": Seller.query.filter_by(status="PENDING").count(),
        "products": Product.query.count(),
        "orders": Order.query.count(),
        "revenue": _f(db.session.query(func.coalesce(func.sum(Order.total), 0)).filter(active_orders).scalar()),
        "platformFees": commission_sum(Commission.commission_amount, ["RELEASED"]),
        "feesInEscrow": commission_sum(Commission.commission_amount, HELD),
        "escrow": commission_sum(Commission.sale_amount, HELD),
        "escrowOrders": commission_count(HELD),
        "awaitHold": Order.query.filter_by(status="PAYMENT_CONFIRMED").count(),
        "awaitDelivered": Order.query.filter(Order.status.in_(["SHIPPED", "OUT_FOR_DELIVERY"])).count(),
        "awaitPayout": Order.query.filter(Order.status.in_(["DELIVERED", "COMPLETED"]),
                                          Order.payout_released_at.is_(None)).count(),
        "pendingOrders": Order.query.filter(Order.status.notin_(["CANCELLED", "REFUNDED", "COMPLETED"])).count(),
    }
    order_service.auto_release_eligible_orders()
    recent = (Order.query.options(joinedload(Order.user), joinedload(Order.seller))
              .order_by(Order.placed_at.desc()).limit(10).all())

    months = [calendar.month_abbr[m] for m in range(1, 13)]
    orders_by_month = {m: 0 for m in range(1, 13)}
    revenue_by_month = {m: 0.0 for m in range(1, 13)}
    for m, cnt in (db.session.query(func.month(Order.placed_at), func.count(Order.id))
                   .filter(func.year(Order.placed_at) == year).group_by(func.month(Order.placed_at))):
        orders_by_month[int(m)] = int(cnt)
    for m, total in (db.session.query(func.month(Order.placed_at), func.sum(Order.total))
                     .filter(func.year(Order.placed_at) == year, active_orders)
                     .group_by(func.month(Order.placed_at))):
        revenue_by_month[int(m)] = _f(total)

    cat_agg = (db.session.query(Category.name.label("label"),
                                func.coalesce(func.sum(Product.sold_count * Product.price), 0).label("value"))
               .join(Category, Category.id == Product.category_id)
               .group_by(Category.id, Category.name).order_by(func.sum(Product.sold_count * Product.price).desc())
               .limit(5).all())
    cat_total = max(1.0, sum(_f(r.value) for r in cat_agg))
    category_rows = [{"label": r.label, "value": _f(r.value), "percent": round(_f(r.value) / cat_total * 100, 1)}
                     for r in cat_agg]

    country_expr = func.coalesce(func.nullif(Order.shipping_country, ""), "Cambodia")
    country_agg = (db.session.query(country_expr.label("label"), func.sum(Order.total).label("value"))
                   .filter(active_orders).group_by(country_expr).order_by(func.sum(Order.total).desc())
                   .limit(5).all())
    country_total = max(1.0, sum(_f(r.value) for r in country_agg))
    country_rows = [{"label": r.label, "value": _f(r.value), "percent": round(_f(r.value) / country_total * 100, 1)}
                    for r in country_agg]
    if not country_rows:
        country_rows = [{"label": "Cambodia", "value": stats["revenue"], "percent": 100}]

    perf_rows = []
    for s in Seller.query.filter_by(status="APPROVED").order_by(Seller.total_sales.desc()).limit(5).all():
        products = max(1, int(s.total_products or 0))
        rating = s.rating or 0
        perf_rows.append({
            "name": s.store_name, "products": int(s.total_products or 0),
            "arpu": float(s.total_sales or 0) / products,
            "health": "Good" if rating >= 4.5 else ("Fair" if rating >= 3.5 else "Watch"),
        })

    return render_template(
        "admin/dashboard.html", stats=stats, recentOrders=recent, year=year, months=months,
        ordersTrend=[orders_by_month[m] for m in range(1, 13)],
        revenueTrend=[revenue_by_month[m] for m in range(1, 13)],
        categoryRows=category_rows, categoryLabels=[r["label"] for r in category_rows],
        categoryValues=[r["value"] for r in category_rows],
        funnelLabels=["Users", "Customers", "Sellers", "Products", "Orders"],
        funnelValues=[stats["users"], stats["customers"], stats["sellers"], stats["products"], stats["orders"]],
        countryRows=country_rows, countryLabels=[r["label"] for r in country_rows],
        countryValues=[r["value"] for r in country_rows], perfRows=perf_rows,
    )


# ------------------------------------------------------------------- users
@admin_bp.get("/users")
def users():
    a = request.args
    q = User.query
    if a.get("q"):
        term = f"%{a.get('q')}%"
        q = q.filter(db.or_(User.full_name.like(term), User.email.like(term), User.phone.like(term)))
    if a.get("role"):
        q = q.filter(User.role == a.get("role"))
    sort = a.get("sort", "newest")
    q = {"oldest": q.order_by(User.created_at, User.id),
         "name_asc": q.order_by(User.full_name, User.id),
         "name_desc": q.order_by(User.full_name.desc(), User.id.desc())
         }.get(sort, q.order_by(User.created_at.desc(), User.id.desc()))
    return render_template("admin/users.html", users=paginate(q, 20))


def _email_taken(except_id=None):
    def check(v):
        q = User.query.filter(func.lower(User.email) == str(v).lower())
        if except_id:
            q = q.filter(User.id != except_id)
        return q.first() is not None
    return check


@admin_bp.post("/users")
def store_user():
    form = normalized_form(flags=("enabled",))
    data, errors = validate({
        "full_name": "required|string|max:120", "email": "required|email|unique",
        "phone": "nullable|string|max:30", "password": "required|string|min:6",
        "role": "required|in:ADMIN,CUSTOMER,SELLER", "enabled": "nullable|boolean",
    }, source=form, unique={"email": _email_taken()})
    if errors:
        return back_with_errors(errors, "/admin/users")
    now = utcnow()
    user = User(full_name=data["full_name"], email=data["email"], phone=data.get("phone"),
                password=hash_password(data["password"]), role=data["role"],
                enabled=bool(form["enabled"]) if "enabled" in form else True, created_at=now, updated_at=now)
    db.session.add(user)
    db.session.commit()
    log_activity("CREATE", "Users", f"Admin created user {user.email}", "users", user.id)
    return back("User created successfully")


@admin_bp.post("/users/<int:id>/edit")
def update_user(id):
    user = db.get_or_404(User, id)
    form = normalized_form(flags=("enabled",))
    data, errors = validate({
        "full_name": "required|string|max:120", "email": "required|email|unique",
        "phone": "nullable|string|max:30", "password": "nullable|string|min:6",
        "role": "required|in:ADMIN,CUSTOMER,SELLER", "enabled": "nullable|boolean",
    }, source=form, unique={"email": _email_taken(id)})
    if errors:
        return back_with_errors(errors, "/admin/users")
    user.full_name, user.email, user.phone = data["full_name"], data["email"], data.get("phone")
    user.role = data["role"]
    user.enabled = bool(form.get("enabled", False))
    if data.get("password"):
        user.password = hash_password(data["password"])
    user.updated_at = utcnow()
    db.session.commit()
    log_activity("UPDATE", "Users", f"Admin updated user #{id}", "users", id)
    return back("User updated successfully")


@admin_bp.post("/users/<int:id>/ban")
def ban_user(id):
    User.query.filter_by(id=id).update({"enabled": 0, "updated_at": utcnow()})
    db.session.commit()
    log_activity("BAN", "Users", f"Admin banned user #{id}", "users", id)
    return back("User banned")


@admin_bp.post("/users/<int:id>/unban")
def unban_user(id):
    User.query.filter_by(id=id).update({"enabled": 1, "updated_at": utcnow()})
    db.session.commit()
    log_activity("UNBAN", "Users", f"Admin unbanned user #{id}", "users", id)
    return back("User unbanned")


@admin_bp.post("/users/<int:id>/delete")
def delete_user(id):
    user = db.session.get(User, id)
    if not user:
        return back("User deleted")
    try:
        db.session.delete(user)
        db.session.commit()
        log_activity("DELETE", "Users", f"Admin deleted user #{id}", "users", id)
        return back("User deleted")
    except IntegrityError:
        db.session.rollback()
        return back("This user has orders/products and cannot be deleted. Ban the user instead.", "error")


# ----------------------------------------------------------------- sellers
@admin_bp.get("/sellers")
def sellers():
    a = request.args
    q = Seller.query.options(joinedload(Seller.user))
    if a.get("q"):
        term = f"%{a.get('q')}%"
        q = (q.outerjoin(User, User.id == Seller.user_id)
             .filter(db.or_(Seller.store_name.like(term), Seller.owner_name.like(term),
                            Seller.location.like(term), User.email.like(term))))
    sort = a.get("sort", "newest")
    q = {"oldest": q.order_by(Seller.created_at, Seller.id),
         "name_asc": q.order_by(Seller.store_name, Seller.id),
         "name_desc": q.order_by(Seller.store_name.desc(), Seller.id.desc())
         }.get(sort, q.order_by(Seller.created_at.desc(), Seller.id.desc()))
    return render_template("admin/sellers.html", sellers=paginate(q, 20), countryOptions=COUNTRY_OPTIONS)


@admin_bp.post("/sellers")
def store_seller():
    data, errors = validate({
        "store_name": "required|string|max:150", "owner_name": "required|string|max:100",
        "email": "required|email|unique", "password": "required|string|min:6",
        "phone": "nullable|string|max:30", "country": "nullable|string|max:80",
        "location": "nullable|string|max:100", "address": "nullable|string|max:200",
        "level": "nullable|in:NEW,TOP,TRUSTED,VERIFIED",
    }, source=normalized_form(flags=()), unique={"email": _email_taken()})
    if errors:
        return back_with_errors(errors, "/admin/sellers")
    now = utcnow()
    user = User(full_name=data["owner_name"], email=data["email"], phone=data.get("phone"),
                password=hash_password(data["password"]), role="SELLER", enabled=True,
                created_at=now, updated_at=now)
    db.session.add(user)
    db.session.flush()
    db.session.add(Seller(
        user_id=user.id, store_name=data["store_name"], owner_name=data["owner_name"], phone=data.get("phone"),
        country=data.get("country"), location=data.get("location"), address=data.get("address"),
        status="APPROVED", level=data.get("level") or "VERIFIED", rating=0, followers=0, response_rate=100,
        total_products=0, total_sales=0, created_at=now, joined_at=now))
    db.session.commit()
    log_activity("CREATE", "Sellers", f"Admin created seller {data['store_name']}", "users", user.id)
    return back("Seller created successfully")


@admin_bp.post("/sellers/<int:id>/delete")
def delete_seller(id):
    seller = db.get_or_404(Seller, id)
    user_id = seller.user_id
    try:
        db.session.delete(seller)
        db.session.flush()
        if user_id:
            User.query.filter_by(id=user_id, role="SELLER").delete(synchronize_session=False)
        db.session.commit()
        log_activity("DELETE", "Sellers", f"Admin deleted seller #{id}", "sellers", id)
        return back("Seller deleted successfully")
    except IntegrityError:
        db.session.rollback()
        return back("This seller has products/orders and cannot be deleted. Ban the store instead.", "error")


@admin_bp.get("/seller-applications")
def applications():
    apps = (Seller.query.options(joinedload(Seller.user)).filter_by(status="PENDING")
            .order_by(Seller.created_at.desc()).all())
    return render_template("admin/applications.html", applications=apps)


@admin_bp.post("/sellers/<int:id>/approve")
def approve_seller(id):
    seller = db.get_or_404(Seller, id)
    seller.status = "APPROVED"
    seller.joined_at = seller.joined_at or utcnow()
    if seller.user:
        seller.user.role = "SELLER"
    db.session.commit()
    log_activity("APPROVE", "Sellers", f"Approved seller #{id}", "sellers", id)
    return back("Seller approved")


@admin_bp.post("/sellers/<int:id>/reject")
def reject_seller(id):
    seller = db.get_or_404(Seller, id)
    seller.status = "REJECTED"
    seller.rejection_reason = request.form.get("reason") or "Rejected"
    if seller.user and seller.user.role == "SELLER":
        seller.user.role = "CUSTOMER"
    db.session.commit()
    log_activity("REJECT", "Sellers", f"Rejected seller #{id}", "sellers", id)
    return back("Seller rejected")


@admin_bp.post("/sellers/<int:id>/ban")
def ban_seller(id):
    """Ban the store only - the user can still log in and shop as a customer."""
    seller = db.get_or_404(Seller, id)
    seller.status = "SUSPENDED"
    if seller.user:
        seller.user.role = "CUSTOMER"
        seller.user.enabled = True
    Product.query.filter_by(seller_id=seller.id).update({"active": 0, "updated_at": utcnow()})
    db.session.commit()
    log_activity("BAN", "Sellers", f"Banned store #{id}", "sellers", id)
    return back("Store banned. They can still log in and shop as a customer, but cannot sell until unbanned.")


@admin_bp.post("/sellers/<int:id>/unban")
def unban_seller(id):
    seller = db.get_or_404(Seller, id)
    seller.status = "APPROVED"
    if seller.user:
        seller.user.role = "SELLER"
        seller.user.enabled = True
    Product.query.filter_by(seller_id=seller.id).update({"active": 1, "updated_at": utcnow()})
    db.session.commit()
    log_activity("UNBAN", "Sellers", f"Unbanned store #{id}", "sellers", id)
    return back("Store unbanned. Seller role restored \u2014 they can sell again.")


@admin_bp.post("/sellers/<int:id>/edit")
def update_seller(id):
    seller = db.get_or_404(Seller, id)
    data, errors = validate({
        "store_name": "required|string|max:150", "owner_name": "required|string|max:100",
        "phone": "nullable|string|max:30", "location": "nullable|string|max:100",
        "address": "nullable|string|max:200", "country": "nullable|string|max:80",
        "status": "required|in:APPROVED,PENDING,REJECTED,SUSPENDED",
        "level": "required|in:NEW,TOP,TRUSTED,VERIFIED",
    }, source=normalized_form(flags=()))
    if errors:
        return back_with_errors(errors, "/admin/sellers")
    for k, v in data.items():
        setattr(seller, k, v)
    if seller.user:
        if data["status"] == "APPROVED":
            seller.user.role, seller.user.enabled = "SELLER", True
        else:
            seller.user.role = "CUSTOMER"
    if data["status"] == "SUSPENDED":
        Product.query.filter_by(seller_id=seller.id).update({"active": 0, "updated_at": utcnow()})
    elif data["status"] == "APPROVED":
        Product.query.filter_by(seller_id=seller.id).update({"active": 1, "updated_at": utcnow()})
    db.session.commit()
    log_activity("UPDATE", "Sellers", f"Updated seller #{id}", "sellers", id)
    return back("Seller updated")


# ---------------------------------------------------------------- products
@admin_bp.get("/products")
def products():
    a = request.args
    category_id = a.get("category", type=int)
    brand_id = a.get("brand", type=int)
    q = Product.query.options(joinedload(Product.brand), joinedload(Product.category), joinedload(Product.seller))
    if a.get("q"):
        q = q.filter(Product.name.like(f"%{a.get('q')}%"))
    if category_id:
        q = q.filter(Product.category_id == category_id)
    if brand_id:
        q = q.filter(Product.brand_id == brand_id)
    return render_template(
        "admin/products.html",
        products=paginate(q.order_by(Product.created_at.desc()), 20),
        categories=Category.query.order_by(Category.name).all(),
        brands=Brand.query.order_by(Brand.name).all(),
        selectedCategoryId=category_id, selectedBrandId=brand_id,
    )


@admin_bp.get("/products/<int:id>/edit")
def edit_product(id):
    product = db.get_or_404(Product, id)
    return render_template("admin/product-form.html", product=product,
                           categories=Category.query.order_by(Category.name).all(),
                           brands=Brand.query.order_by(Brand.name).all())


@admin_bp.post("/products/<int:id>/edit")
def update_product(id):
    product = db.get_or_404(Product, id)
    form = normalized_form()
    data, errors = validate(PRODUCT_RULES, source=form, exists=PRODUCT_EXISTS, messages=PRODUCT_MESSAGES)
    picked = collect_images()
    img_errors = validate_images(picked)
    if img_errors:
        errors["imageFiles"] = img_errors
    if errors:
        return back_with_errors(errors, f"/admin/products/{id}/edit")

    kept = parse_gallery_keep(form.get("galleryKeep")) if "galleryKeep" in form else product.galleryList()
    gallery = build_gallery(kept, store_images(picked))
    price, original, discount = pricing(data)
    variants = variant_fields(data)

    product.name, product.description = data["name"], data["description"]
    product.specifications = data.get("specifications")
    product.option_groups = variants["option_groups"]
    product.color_options = variants["color_options"]
    product.storage_options = variants["storage_options"]
    product.ram_options = variants["ram_options"]
    product.category_id, product.brand_id = int(data["category_id"]), int(data["brand_id"])
    product.price, product.original_price, product.discount_percent = price, original, discount
    product.stock = int(data["stock"])
    product.sku, product.warranty = data.get("sku"), data.get("warranty")
    product.image_url, product.gallery_urls = gallery[0], ",".join(gallery)
    product.active = bool(form.get("active", False))
    product.updated_at = utcnow()
    db.session.commit()
    log_activity("UPDATE", "Products", f"Admin updated product #{id}", "products", id)
    flash("Product updated", "message")
    return redirect("/admin/products")


@admin_bp.post("/products/<int:id>/delete")
def delete_product(id):
    product = db.session.get(Product, id)
    if not product:
        return back("Product deleted")
    try:
        db.session.delete(product)
        db.session.commit()
        log_activity("DELETE", "Products", f"Admin deleted product #{id}", "products", id)
        return back("Product deleted")
    except IntegrityError:
        db.session.rollback()
        return back("This product is part of existing orders and cannot be deleted. Deactivate it instead.", "error")


# -------------------------------------------------------------- categories
@admin_bp.get("/categories")
def categories():
    a = request.args
    q = Category.query
    if a.get("q"):
        term = f"%{a.get('q')}%"
        q = q.filter(db.or_(Category.name.like(term), Category.slug.like(term), Category.icon.like(term)))
    sort = a.get("sort", "sort")
    q = {"name_asc": q.order_by(Category.name), "name_desc": q.order_by(Category.name.desc()),
         "newest": q.order_by(Category.id.desc()), "oldest": q.order_by(Category.id)
         }.get(sort, q.order_by(Category.sort_order, Category.name))
    return render_template("admin/categories.html", categories=q.all())


@admin_bp.post("/categories")
def store_category():
    data, errors = validate({"name": "required|string|max:80", "icon": "nullable|string|max:50"},
                            source=normalized_form())
    if errors:
        return back_with_errors(errors, "/admin/categories")
    top = db.session.query(func.max(Category.sort_order)).scalar() or 0
    try:
        db.session.add(Category(name=data["name"], slug=slugify(data["name"]), icon=data.get("icon") or "tag",
                                active=True, sort_order=top + 1))
        db.session.commit()
    except IntegrityError:
        db.session.rollback()
        return back("A category with that name already exists.", "error")
    return back("Category added")


@admin_bp.post("/categories/<int:id>/edit")
def update_category(id):
    cat = db.get_or_404(Category, id)
    form = normalized_form()
    data, errors = validate({"name": "required|string|max:80", "icon": "nullable|string|max:50",
                             "active": "nullable|boolean"}, source=form)
    if errors:
        return back_with_errors(errors, "/admin/categories")
    try:
        cat.name, cat.slug = data["name"], slugify(data["name"])
        cat.icon = data.get("icon") or cat.icon
        cat.active = bool(form.get("active", False))
        db.session.commit()
    except IntegrityError:
        db.session.rollback()
        return back("A category with that name already exists.", "error")
    return back("Category updated")


@admin_bp.post("/categories/<int:id>/delete")
def delete_category(id):
    return _safe_delete(Category, id, "Category")


# ------------------------------------------------------------------ brands
@admin_bp.get("/brands")
def brands():
    a = request.args
    q = Brand.query
    if a.get("q"):
        term = f"%{a.get('q')}%"
        q = q.filter(db.or_(Brand.name.like(term), Brand.slug.like(term)))
    sort = a.get("sort", "name_asc")
    q = {"name_desc": q.order_by(Brand.name.desc()), "newest": q.order_by(Brand.id.desc()),
         "oldest": q.order_by(Brand.id)}.get(sort, q.order_by(Brand.name))
    return render_template("admin/brands.html", brands=q.all())


def _brand_logo(data, current=None):
    logo = get_file("logo")
    if logo:
        return save_upload(logo, "logo")
    return data.get("logo_url") or current


def _brand_errors(errors):
    logo = get_file("logo")
    if logo and not is_image_file(logo, 4):
        errors.setdefault("logo", []).append("The logo must be an image up to 4 MB.")
    return errors


@admin_bp.post("/brands")
def store_brand():
    data, errors = validate({"name": "required|string|max:80", "logo_url": "nullable|string|max:500"},
                            source=normalized_form())
    if _brand_errors(errors):
        return back_with_errors(errors, "/admin/brands")
    try:
        db.session.add(Brand(name=data["name"], slug=slugify(data["name"]), logo_url=_brand_logo(data), active=True))
        db.session.commit()
    except IntegrityError:
        db.session.rollback()
        return back("A brand with that name already exists.", "error")
    return back("Brand added")


@admin_bp.post("/brands/<int:id>/edit")
def update_brand(id):
    brand = db.get_or_404(Brand, id)
    form = normalized_form()
    data, errors = validate({"name": "required|string|max:80", "logo_url": "nullable|string|max:500",
                             "active": "nullable|boolean"}, source=form)
    if _brand_errors(errors):
        return back_with_errors(errors, "/admin/brands")
    try:
        brand.name, brand.slug = data["name"], slugify(data["name"])
        brand.logo_url = _brand_logo(data, brand.logo_url)
        brand.active = bool(form.get("active", False))
        db.session.commit()
    except IntegrityError:
        db.session.rollback()
        return back("A brand with that name already exists.", "error")
    return back("Brand updated")


@admin_bp.post("/brands/<int:id>/delete")
def delete_brand(id):
    return _safe_delete(Brand, id, "Brand")


# ------------------------------------------------------------------ orders
def _ts(dt):
    return calendar.timegm(dt.timetuple()) if dt else 0


@admin_bp.get("/orders")
def orders():
    order_service.auto_release_eligible_orders()
    rows = (Order.query.options(joinedload(Order.user), joinedload(Order.seller), joinedload(Order.items),
                                joinedload(Order.commission))
            .order_by(Order.placed_at.desc(), Order.id.desc()).all())
    groups = {}
    for o in rows:   # one checkout = same customer + same placed_at (one order per seller)
        groups.setdefault((o.user_id, _ts(o.placed_at)), []).append(o)
    checkouts = []
    for (user_id, ts), group in groups.items():
        first = group[0]
        statuses = list(dict.fromkeys(o.status for o in group))
        checkouts.append({
            "user_id": user_id, "placed_at_ts": ts,
            "customer": first.recipient_name or (first.user.full_name if first.user else "\u2014"),
            "email": first.user.email if first.user else "",
            "payment": first.payment_method, "placed_at": first.placed_at, "seller_count": len(group),
            "item_count": sum(i.quantity for o in group for i in o.items),
            "total": sum(_f(o.total) for o in group), "statuses": statuses,
            "status_label": statuses[0].replace("_", " ") if len(statuses) == 1 else f"{len(statuses)} statuses",
        })
    return render_template("admin/orders.html", checkouts=checkouts)


@admin_bp.get("/orders/checkout/<int:userId>/<int:placedAt>")
def order_checkout(userId, placedAt):
    order_service.auto_release_eligible_orders()
    stamp = datetime.fromtimestamp(placedAt, timezone.utc).replace(tzinfo=None)
    rows = (Order.query.options(joinedload(Order.user), joinedload(Order.seller), joinedload(Order.items),
                                joinedload(Order.commission))
            .filter(Order.user_id == userId, Order.placed_at == stamp).order_by(Order.id).all())
    if not rows:
        abort(404)
    first = rows[0]
    checkout = {
        "user_id": userId, "placed_at_ts": placedAt,
        "customer": first.recipient_name or (first.user.full_name if first.user else "\u2014"),
        "email": first.user.email if first.user else "",
        "phone": first.recipient_phone or (first.user.phone if first.user else ""),
        "address": first.shipping_address, "payment": first.payment_method,
        "delivery": first.delivery_type, "placed_at": first.placed_at,
        "total": sum(_f(o.total) for o in rows), "seller_count": len(rows),
    }
    return render_template("admin/order-checkout.html", checkout=checkout, orders=rows)


def _order_action(fn, success, log_action, id):
    try:
        fn(id)
        log_activity(log_action, "Orders", f"{log_action} order #{id}", "orders", id)
        return back(success)
    except Exception as exc:
        db.session.rollback()
        return back(str(exc), "error")


@admin_bp.post("/orders/<int:id>/confirm-hold")
def confirm_hold(id):
    return _order_action(order_service.admin_confirm_hold,
                         "Payment confirmed \u2014 funds held. Seller can confirm stock.", "HOLD", id)


@admin_bp.post("/orders/<int:id>/confirm-delivered")
def confirm_delivered(id):
    return _order_action(order_service.admin_confirm_delivered,
                         "Delivery confirmed. Buyer protection started.", "DELIVERED", id)


@admin_bp.post("/orders/<int:id>/release-payout")
def release_payout(id):
    return _order_action(order_service.force_release_payout, "3% commission deducted \u2014 seller paid.",
                         "PAYOUT", id)


@admin_bp.post("/orders/<int:id>/refund")
def refund_order(id):
    return _order_action(order_service.refund_order, "Order refunded \u2014 held funds returned.", "REFUND", id)


# ------------------------------------------------------------- commissions
@admin_bp.get("/commissions")
def commissions():
    rows = (Commission.query.options(joinedload(Commission.order), joinedload(Commission.seller))
            .order_by(Commission.created_at.desc()).all())
    released = [c for c in rows if c.payout_status == "RELEASED"]
    held = [c for c in rows if c.payout_status in HELD]
    return render_template(
        "admin/commissions.html", commissions=rows,
        totalPlatform=sum(_f(c.commission_amount) for c in released),
        feesInEscrow=sum(_f(c.commission_amount) for c in held),
        escrowSales=sum(_f(c.sale_amount) for c in held), heldCount=len(held))


# --------------------------------------------------------------- countries
@admin_bp.get("/countries")
def countries():
    sort = request.args.get("sort", "money_desc")
    return render_template("admin/countries.html", countries=country_service.countries_list(sort), sort=sort)


@admin_bp.get("/countries/<path:name>")
def country_detail(name):
    try:
        data = country_service.country_profile(name, request.args.get("sellerSort", "revenue_desc"),
                                               request.args.get("buyerSort", "spent_desc"))
        return render_template("admin/country-detail.html", **data)
    except Exception as exc:
        flash(str(exc), "error")
        return redirect("/admin/countries")


# ----------------------------------------------------------------- reviews
@admin_bp.get("/reviews")
def reviews():
    rows = (Review.query.options(joinedload(Review.product), joinedload(Review.user))
            .order_by(Review.created_at.desc()).all())
    return render_template("admin/reviews.html", reviews=rows)


@admin_bp.post("/reviews/<int:id>/hide")
def hide_review(id):
    Review.query.filter_by(id=id).update({"hidden": 1})
    db.session.commit()
    return back("Review hidden")


@admin_bp.get("/settings")
def settings():
    return render_template("admin/settings.html")
