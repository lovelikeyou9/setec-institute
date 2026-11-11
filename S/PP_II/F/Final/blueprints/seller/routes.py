"""Port of Seller\\SellerDashboardController (all routes under /seller-dashboard)."""
from flask import flash, g, redirect, render_template, request
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import joinedload

from activity_logger import log_activity
from auth_utils import load_current_user
from blueprints.forms import (
    PRODUCT_EXISTS, PRODUCT_MESSAGES, PRODUCT_RULES, build_gallery, collect_images, get_file,
    normalized_form, open_modal, parse_gallery_keep, pricing, store_images, validate_images,
    variant_fields,
)
from blueprints.seller import seller_bp
from extensions import db
from helpers import back, back_with_errors, paginate, slugify, validate
from models import Brand, Category, Commission, Order, Product, Review
from models.base import utcnow
from services import order_service
from upload_service import is_image_file, save_upload

HELD = ("HELD", "IN_PROTECTION")


def _fsum(rows, attr):
    return round(sum(float(getattr(r, attr) or 0) for r in rows), 2)


def _active_categories():
    return Category.query.filter(Category.active == 1).order_by(Category.sort_order).all()


def _active_brands():
    return Brand.query.filter(Brand.active == 1).order_by(Brand.name).all()


@seller_bp.get("/")
def dashboard():
    seller = g.seller
    orders = Order.query.filter_by(seller_id=seller.id).all()
    products = Product.query.filter_by(seller_id=seller.id).all()
    commissions = Commission.query.filter_by(seller_id=seller.id).all()
    released = [c for c in commissions if c.payout_status == "RELEASED"]
    held = [c for c in commissions if c.payout_status in HELD]

    stats = {
        "totalOrders": len(orders),
        "pendingOrders": sum(1 for o in orders if o.status in
                             ("FUNDS_HELD", "SELLER_PROCESSING", "SHIPPED", "OUT_FOR_DELIVERY")),
        "totalProducts": len(products),
        "activeProducts": sum(1 for p in products if p.active),
        "totalEarnings": _fsum(released, "seller_earnings"),
        "heldEarnings": _fsum(held, "seller_earnings"),
        "revenue": _fsum([o for o in orders if o.status not in ("CANCELLED", "REFUNDED")], "subtotal"),
        "feesPaid": _fsum(released, "commission_amount"),
        "feesPending": _fsum(held, "commission_amount"),
    }
    stats["feesTotal"] = stats["feesPaid"] + stats["feesPending"]
    recent = (Order.query.options(joinedload(Order.user), joinedload(Order.items))
              .filter_by(seller_id=seller.id).order_by(Order.placed_at.desc()).limit(8).all())
    return render_template("seller/dashboard.html", seller=seller, stats=stats, recentOrders=recent)


# ---------------------------------------------------------------- products
@seller_bp.get("/products")
def products():
    seller = g.seller
    a = request.args
    category_id = a.get("category", type=int)
    brand_id = a.get("brand", type=int)
    q = (Product.query.options(joinedload(Product.brand), joinedload(Product.category))
         .filter(Product.seller_id == seller.id))
    if a.get("q"):
        q = q.filter(Product.name.like(f"%{a.get('q')}%"))
    if category_id:
        q = q.filter(Product.category_id == category_id)
    if brand_id:
        q = q.filter(Product.brand_id == brand_id)
    sort = a.get("sort", "newest")
    sort_map = {
        "price_asc": q.order_by(Product.price.asc()),
        "price_desc": q.order_by(Product.price.desc()),
        "name": q.order_by(Product.name.asc()),
        "name_asc": q.order_by(Product.name.asc()),
        "name_desc": q.order_by(Product.name.desc()),
        "oldest": q.order_by(Product.created_at.asc()),
        "newest": q.order_by(Product.created_at.desc()),
        "stock_desc": q.order_by(Product.stock.desc()),
        "popular": q.order_by(Product.sold_count.desc()),
    }
    q = sort_map.get(sort, sort_map["newest"])
    return render_template("seller/products.html", seller=seller, products=paginate(q, 20),
                           categories=_active_categories(), brands=_active_brands(),
                           selectedCategoryId=category_id, selectedBrandId=brand_id, sort=sort)


@seller_bp.get("/products/add")
def create_product():
    return render_template("seller/product-form.html", seller=g.seller,
                           categories=_active_categories(), brands=_active_brands())


def _product_failure(modal, errors=None, message=None):
    open_modal(modal)
    if message:
        flash(message, "error")
    if errors:
        return back_with_errors(errors, "/seller-dashboard/products")
    from helpers import back_with_input
    return back_with_input("/seller-dashboard/products")


@seller_bp.post("/products/add")
def store_product():
    seller = g.seller
    form = normalized_form()
    try:
        data, errors = validate(PRODUCT_RULES, source=form, exists=PRODUCT_EXISTS, messages=PRODUCT_MESSAGES)
        picked = collect_images()
        img_errors = validate_images(picked)
        if img_errors:
            errors["imageFiles"] = img_errors
        if errors:
            return _product_failure("modalAddProduct", errors)

        uploaded = store_images(picked)
        gallery = build_gallery([], uploaded)
        price, original, discount = pricing(data)
        variants = variant_fields(data)
        now = utcnow()

        db.session.add(Product(
            name=data["name"], slug=f"{slugify(data['name'])}-{int(now.timestamp()) % 100000}",
            description=data["description"], specifications=data.get("specifications"),
            option_groups=variants["option_groups"], color_options=variants["color_options"],
            storage_options=variants["storage_options"], ram_options=variants["ram_options"],
            category_id=int(data["category_id"]), brand_id=int(data["brand_id"]), seller_id=seller.id,
            price=price, original_price=original, discount_percent=discount,
            stock=int(data["stock"]), sku=data.get("sku"), warranty=data.get("warranty"),
            condition_type="NEW", image_url=gallery[0], gallery_urls=",".join(gallery),
            active=True, featured=False, flash_deal=False, rating=0, review_count=0, sold_count=0,
            created_at=now, updated_at=now,
        ))
        seller.total_products = (seller.total_products or 0) + 1
        db.session.commit()
        log_activity("CREATE", "Products", f"Seller added product '{data['name']}'")
        flash("Product added successfully", "message")
        return redirect("/seller-dashboard/products")
    except Exception as exc:
        db.session.rollback()
        return _product_failure("modalAddProduct", message=f"Could not add product: {exc}")


@seller_bp.get("/products/<int:id>/edit")
def edit_product(id):
    product = Product.query.filter_by(seller_id=g.seller.id, id=id).first_or_404()
    return render_template("seller/product-form.html", seller=g.seller, product=product,
                           categories=_active_categories(), brands=_active_brands())


@seller_bp.post("/products/<int:id>/edit")
def update_product(id):
    product = Product.query.filter_by(seller_id=g.seller.id, id=id).first_or_404()
    form = normalized_form()
    try:
        data, errors = validate(PRODUCT_RULES, source=form, exists=PRODUCT_EXISTS, messages=PRODUCT_MESSAGES)
        picked = collect_images()
        img_errors = validate_images(picked)
        if img_errors:
            errors["imageFiles"] = img_errors
        if errors:
            return _product_failure("modalEditProduct", errors)

        kept = parse_gallery_keep(form.get("galleryKeep")) if "galleryKeep" in form else product.galleryList()
        gallery = build_gallery(kept, store_images(picked))
        price, original, discount = pricing(data)
        variants = variant_fields(data)

        product.name = data["name"]
        product.description = data["description"]
        product.specifications = data.get("specifications")
        product.option_groups = variants["option_groups"]
        product.color_options = variants["color_options"]
        product.storage_options = variants["storage_options"]
        product.ram_options = variants["ram_options"]
        product.category_id = int(data["category_id"])
        product.brand_id = int(data["brand_id"])
        product.price, product.original_price, product.discount_percent = price, original, discount
        product.stock = int(data["stock"])
        product.sku = data.get("sku")
        product.warranty = data.get("warranty")
        product.image_url = gallery[0]
        product.gallery_urls = ",".join(gallery)
        product.active = bool(form.get("active", False))
        product.updated_at = utcnow()
        db.session.commit()
        log_activity("UPDATE", "Products", f"Seller updated product #{id}", "products", id)
        flash("Product updated successfully", "message")
        return redirect("/seller-dashboard/products")
    except Exception as exc:
        db.session.rollback()
        return _product_failure("modalEditProduct", message=f"Could not update product: {exc}")


@seller_bp.post("/products/<int:id>/delete")
def delete_product(id):
    product = Product.query.filter_by(seller_id=g.seller.id, id=id).first_or_404()
    try:
        db.session.delete(product)
        if g.seller.total_products:
            g.seller.total_products -= 1
        db.session.commit()
        log_activity("DELETE", "Products", f"Seller deleted product #{id}", "products", id)
        return back("Product deleted successfully")
    except IntegrityError:
        db.session.rollback()
        return back("This product is part of existing orders and cannot be deleted. Deactivate it instead.",
                    "error")


# ------------------------------------------------------------------ orders
@seller_bp.get("/orders")
def orders():
    seller = g.seller
    q = (Order.query.options(joinedload(Order.user), joinedload(Order.items), joinedload(Order.commission))
         .filter(Order.seller_id == seller.id))
    sort = request.args.get("sort", "newest")
    q = {"oldest": q.order_by(Order.placed_at), "total_desc": q.order_by(Order.total.desc())
         }.get(sort, q.order_by(Order.placed_at.desc()))
    return render_template("seller/orders.html", seller=seller, orders=q.all())


def _order_action(fn, success, *args):
    try:
        fn(*args)
        return back(success)
    except Exception as exc:
        db.session.rollback()
        return back(str(exc), "error")


@seller_bp.post("/orders/<int:id>/accept")
def accept_order(id):
    return _order_action(order_service.seller_accept_order, "Stock confirmed \u2014 order is now Processing.",
                         id, g.seller.id)


@seller_bp.post("/orders/<int:id>/decline-stock")
def decline_order(id):
    return _order_action(order_service.seller_decline_out_of_stock,
                         "Order declined (out of stock). Customer will be refunded.", id, g.seller.id)


@seller_bp.post("/orders/<int:id>/items/<int:item_id>/decline")
def decline_order_item(id, item_id):
    try:
        order = order_service.seller_decline_item(id, item_id, g.seller.id)
        if order.status == "REFUNDED":
            return back("All items refused. Order cancelled and customer refunded.")
        return back("Item refused (out of stock). That line was refunded; remaining items stay on the order.")
    except Exception as exc:
        db.session.rollback()
        return back(str(exc), "error")


@seller_bp.post("/orders/<int:id>/status")
def update_order_status(id):
    return _order_action(order_service.seller_update_fulfillment, "Status updated.",
                         id, g.seller.id, request.form.get("status"))


# ------------------------------------------------------- earnings / reviews
@seller_bp.get("/earnings")
def earnings():
    seller = g.seller
    commissions = (Commission.query.options(joinedload(Commission.order))
                   .filter_by(seller_id=seller.id).order_by(Commission.created_at.desc()).all())
    released = [c for c in commissions if c.payout_status == "RELEASED"]
    held = [c for c in commissions if c.payout_status in HELD]
    return render_template(
        "seller/earnings.html", seller=seller, commissions=commissions,
        totalEarnings=_fsum(released, "seller_earnings"), heldEarnings=_fsum(held, "seller_earnings"),
        feesPaid=_fsum(released, "commission_amount"), feesPending=_fsum(held, "commission_amount"))


@seller_bp.get("/reviews")
def reviews():
    seller = g.seller
    rows = (Review.query.join(Product, Review.product_id == Product.id)
            .options(joinedload(Review.product), joinedload(Review.user))
            .filter(Product.seller_id == seller.id).order_by(Review.created_at.desc()).all())
    return render_template("seller/reviews.html", seller=seller, reviews=rows)


def _counts(model_col, ids):
    counts = {}
    for i in ids:
        counts[i] = Product.query.filter(Product.seller_id == g.seller.id, model_col == i).count()
    return counts


@seller_bp.get("/categories")
def categories():
    cats = _active_categories()
    return render_template("seller/categories.html", seller=g.seller, categories=cats,
                           counts=_counts(Product.category_id, [c.id for c in cats]))


@seller_bp.get("/brands")
def brands():
    rows = _active_brands()
    return render_template("seller/brands.html", seller=g.seller, brands=rows,
                           counts=_counts(Product.brand_id, [b.id for b in rows]))


# ---------------------------------------------------------------- settings
@seller_bp.get("/settings")
def settings():
    return render_template("seller/settings.html", seller=g.seller)


@seller_bp.post("/settings")
def update_settings():
    seller = g.seller
    form = normalized_form(flags=())
    data, errors = validate({
        "store_name": "required|string|max:150",
        "owner_name": "required|string|max:100",
        "phone": "nullable|string|max:30",
        "location": "nullable|string|max:100",
        "address": "nullable|string|max:200",
        "country": "nullable|string|max:80",
        "description": "nullable|string|max:1000",
        "logo_url": "nullable|string|max:500",
        "banner_url": "nullable|string|max:500",
    }, source=form)
    logo, banner = get_file("logo"), get_file("banner")
    if logo and not is_image_file(logo, 4):
        errors.setdefault("logo", []).append("The logo must be an image up to 4 MB.")
    if banner and not is_image_file(banner, 6):
        errors.setdefault("banner", []).append("The banner must be an image up to 6 MB.")
    if errors:
        return back_with_errors(errors, "/seller-dashboard/settings")

    for key in ("store_name", "owner_name", "phone", "location", "address", "country", "description"):
        setattr(seller, key, data.get(key))
    if logo:
        seller.logo_url = save_upload(logo, "logo")
    elif data.get("logo_url"):
        seller.logo_url = data["logo_url"]
    if banner:
        seller.banner_url = save_upload(banner, "avatars/covers")
    elif data.get("banner_url"):
        seller.banner_url = data["banner_url"]
    db.session.commit()
    log_activity("UPDATE", "Seller", "Store profile updated", "sellers", seller.id)
    return back("Store profile updated successfully")


@seller_bp.post("/settings/personal")
def update_personal():
    seller, user = g.seller, load_current_user()
    form = normalized_form(flags=())
    data, errors = validate({
        "full_name": "required|string|max:120",
        "phone": "nullable|string|max:30",
        "avatar_url": "nullable|string|max:500",
        "cover_url": "nullable|string|max:500",
    }, source=form)
    avatar, cover = get_file("avatar"), get_file("cover")
    if avatar and not is_image_file(avatar, 4):
        errors.setdefault("avatar", []).append("The avatar must be an image up to 4 MB.")
    if cover and not is_image_file(cover, 6):
        errors.setdefault("cover", []).append("The cover must be an image up to 6 MB.")
    if errors:
        return back_with_errors(errors, "/seller-dashboard/settings")

    user.full_name = data["full_name"]
    user.phone = data.get("phone")
    if avatar:
        user.avatar_url = save_upload(avatar, "avatars")
    elif data.get("avatar_url"):
        user.avatar_url = data["avatar_url"]
    if cover:
        user.cover_url = save_upload(cover, "avatars/covers")
    elif data.get("cover_url"):
        user.cover_url = data["cover_url"]
    user.updated_at = utcnow()
    if seller.owner_name != data["full_name"]:
        seller.owner_name = data["full_name"]
    db.session.commit()
    return back("Personal profile updated successfully")
