"""Port of Store\\StorefrontController."""
from flask import flash, redirect, render_template, request
from sqlalchemy import func
from sqlalchemy.orm import joinedload

from activity_logger import log_activity
from auth_utils import load_current_user, login_required
from blueprints.store import store_bp
from extensions import db
from helpers import back, back_with_errors, paginate, to_bool, validate
from models import Brand, Category, Product, Review, Seller, SellerFollow
from models.base import utcnow


# ------------------------------------------------------------------ home
@store_bp.get("/")
@store_bp.get("/home")
def home():
    base = Product.query.options(joinedload(Product.brand), joinedload(Product.seller)).filter(Product.active == 1)
    flash_deals = base.filter(Product.flash_deal == 1).order_by(Product.discount_percent.desc()).limit(8).all()
    featured = base.filter(Product.featured == 1).order_by(Product.sold_count.desc()).limit(8).all()
    categories = Category.query.filter(Category.active == 1).order_by(Category.sort_order).all()
    brands = Brand.query.filter(Brand.active == 1).order_by(Brand.name).limit(16).all()
    sellers = (Seller.query.filter(Seller.status == "APPROVED")
               .order_by(Seller.total_sales.desc()).limit(6).all())

    return render_template(
        "store/home.html",
        flashDeals=flash_deals, featured=featured, categories=categories, brands=brands,
        sellers=sellers, promoSlides=_build_promo_slides(categories), showcases=_build_showcases(),
    )


def _build_promo_slides(categories):
    by_slug = {c.slug: c for c in categories}
    defs = [
        ("Computers & Components", "PCS, LAPTOPS, CPUS, GPUS, AND FULL BUILDS.", "Shop Computers",
         "computers", "/uploads/products/asus-rog-strix-gaming-pc-rtx-5070.avif"),
        ("Networking & Wi-Fi", "ROUTERS, SWITCHES, MESH, AND NAS STORAGE.", "Shop Networking",
         "wifi-routers", "/uploads/products/BE9300 Tri-Band Wi-Fi 7 Router.jpg"),
        ("Gaming Consoles", "CONSOLES, CONTROLLERS, AND DIGITAL GAME CARDS.", "Shop Gaming",
         "gaming-consoles",
         "/uploads/products/Microsoft-Xbox-Series-X_b562417b-7584-4740-983b-422cb2832fb9.7ef4114105e8cbe37cc4125a3cb125f0.avif"),
        ("Cameras, Video & Drones", "ACTION CAMS, CAPTURE CARDS, AND AERIAL TECH.", "Shop Capture",
         "drones", "/uploads/products/DJI Mini 4 Pro.jpg"),
        ("Power & Pro IT", "CHARGERS, UPS, SERVERS, AND WORKSTATIONS.", "Shop Power & IT",
         "servers", "/uploads/products/Dell PowerEdge R760.jpg"),
    ]
    slides = []
    for title, subtitle, cta, slug, image in defs:
        cat = by_slug.get(slug)
        slides.append({
            "title": title, "subtitle": subtitle, "cta": cta,
            "href": f"/products?category={cat.id}" if cat else "/products",
            "image": image,
        })
    return slides


def _build_showcases():
    panels = []
    for slug, theme in (("wifi-routers", "teal"), ("drones", "blue"),
                        ("gaming-consoles", "purple"), ("earbuds", "orange")):
        cat = Category.query.filter(Category.slug == slug, Category.active == 1).first()
        if not cat:
            continue
        products = (Product.query.options(joinedload(Product.brand), joinedload(Product.seller))
                    .filter(Product.category_id == cat.id, Product.active == 1)
                    .order_by(Product.sold_count.desc()).limit(3).all())
        if not products:
            continue
        hero = products[0]
        panels.append({
            "category": cat,
            "banner_title": hero.name,
            "banner_image": hero.image_url or cat.image_url,
            "theme": theme,
            "price_from": min(float(p.price) for p in products),
            "products": products,
        })
    return panels


# -------------------------------------------------------------- products
@store_bp.get("/products")
def products():
    args = request.args
    category_id = args.get("category", type=int)
    brand_id = args.get("brand", type=int)
    q = (Product.query.options(joinedload(Product.brand), joinedload(Product.category),
                               joinedload(Product.seller))
         .filter(Product.active == 1))

    term = (args.get("q") or "").strip()
    if term:
        like = f"%{term}%"
        q = q.filter(db.or_(Product.name.like(like), Product.description.like(like)))
    if category_id:
        q = q.filter(Product.category_id == category_id)
    if brand_id:
        q = q.filter(Product.brand_id == brand_id)
    if args.get("minPrice"):
        q = q.filter(Product.price >= args.get("minPrice", type=float, default=0))
    if args.get("maxPrice"):
        q = q.filter(Product.price <= args.get("maxPrice", type=float, default=0))
    if to_bool(args.get("inStock", "0")):
        q = q.filter(Product.stock > 0)

    sort = args.get("sort", "recommended")
    if sort == "price_asc":
        q = q.order_by(Product.price.asc())
    elif sort == "price_desc":
        q = q.order_by(Product.price.desc())
    elif sort == "newest":
        q = q.order_by(Product.created_at.desc())
    elif sort == "rating":
        q = q.order_by(Product.rating.desc(), Product.review_count.desc())
    elif sort == "sold":
        q = q.order_by(Product.sold_count.desc())
    else:
        q = q.order_by(Product.featured.desc(), Product.sold_count.desc())

    page = paginate(q, 12)
    categories = Category.query.filter(Category.active == 1).order_by(Category.sort_order).all()
    # Brand list: when a category is chosen, only brands that sell in that category
    brand_q = Brand.query.filter(Brand.active == 1)
    if category_id:
        brand_ids = (db.session.query(Product.brand_id)
                     .filter(Product.active == 1, Product.category_id == category_id,
                             Product.brand_id.isnot(None))
                     .distinct())
        brand_q = brand_q.filter(Brand.id.in_(brand_ids))
    brands = brand_q.order_by(Brand.name).all()
    # Keep the selected brand visible even if it has 0 matches (so the radio stays checked)
    if brand_id and not any(b.id == brand_id for b in brands):
        extra = db.session.get(Brand, brand_id)
        if extra:
            brands = list(brands) + [extra]
            brands.sort(key=lambda b: (b.name or "").lower())

    selected = db.session.get(Category, category_id) if category_id else None
    return render_template(
        "store/products.html", products=page, categories=categories, brands=brands,
        selectedCategory=selected, selectedBrandId=brand_id, sort=sort,
    )


@store_bp.get("/products/<slug>")
def product_detail(slug):
    product = (Product.query.join(Seller, Product.seller_id == Seller.id)
               .options(joinedload(Product.brand), joinedload(Product.category),
                        joinedload(Product.seller).joinedload(Seller.user))
               .filter(Product.slug == slug, Product.active == 1, Seller.status == "APPROVED")
               .first_or_404())
    reviews = (Review.query.options(joinedload(Review.user))
               .filter(Review.product_id == product.id, Review.hidden == 0)
               .order_by(Review.created_at.desc()).all())
    related = (Product.query.options(joinedload(Product.brand))
               .filter(Product.category_id == product.category_id, Product.id != product.id,
                       Product.active == 1).limit(4).all())
    following = False
    user = load_current_user()
    if user and product.seller:
        following = SellerFollow.query.filter_by(user_id=user.id, seller_id=product.seller_id).first() is not None
    return render_template("store/product-detail.html", product=product, reviews=reviews,
                           related=related, following=following)


@store_bp.get("/deals")
def deals():
    deals_ = (Product.query.options(joinedload(Product.brand), joinedload(Product.seller))
              .filter(Product.active == 1, Product.flash_deal == 1)
              .order_by(Product.discount_percent.desc()).all())
    return render_template("store/deals.html", flashDeals=deals_)


@store_bp.get("/brands")
def brands():
    rows = Brand.query.filter(Brand.active == 1).order_by(Brand.name).all()
    return render_template("store/brands.html", brands=rows)


@store_bp.get("/brands/<slug>")
def brand_show(slug):
    brand = Brand.query.filter(Brand.slug == slug).first_or_404()
    page = paginate(Product.query.options(joinedload(Product.seller))
                    .filter(Product.brand_id == brand.id, Product.active == 1)
                    .order_by(Product.sold_count.desc()), 12)
    return render_template("store/brand-show.html", brand=brand, products=page)


@store_bp.get("/categories")
def categories():
    rows = Category.query.filter(Category.active == 1).order_by(Category.sort_order).all()
    return render_template("store/categories.html", categories=rows)


@store_bp.get("/categories/<slug>")
def category_show(slug):
    category = Category.query.filter(Category.slug == slug).first_or_404()
    page = paginate(Product.query.options(joinedload(Product.brand), joinedload(Product.seller))
                    .filter(Product.category_id == category.id, Product.active == 1)
                    .order_by(Product.sold_count.desc()), 12)
    return render_template("store/category-show.html", category=category, products=page)


@store_bp.get("/sellers")
def sellers():
    page = paginate(Seller.query.filter(Seller.status == "APPROVED").order_by(Seller.total_sales.desc()), 12)
    return render_template("store/sellers.html", sellers=page)


@store_bp.get("/sellers/<int:id>")
def seller_store(id):
    seller = (Seller.query.options(joinedload(Seller.user))
              .filter(Seller.id == id, Seller.status == "APPROVED").first_or_404())
    page = paginate(Product.query.options(joinedload(Product.brand))
                    .filter(Product.seller_id == seller.id, Product.active == 1)
                    .order_by(Product.sold_count.desc()), 12)
    user = load_current_user()
    following = bool(user and SellerFollow.query.filter_by(user_id=user.id, seller_id=seller.id).first())
    return render_template("store/seller-store.html", seller=seller, products=page, following=following)


@store_bp.post("/sellers/<int:id>/follow")
@login_required
def follow_seller(id):
    user = load_current_user()
    seller = Seller.query.filter(Seller.id == id, Seller.status == "APPROVED").first_or_404()
    exists = SellerFollow.query.filter_by(user_id=user.id, seller_id=seller.id).first()
    if exists:
        db.session.delete(exists)
        seller.followers = max(0, (seller.followers or 0) - 1)
        db.session.commit()
        return back("Unfollowed store.")
    db.session.add(SellerFollow(user_id=user.id, seller_id=seller.id, created_at=utcnow()))
    seller.followers = (seller.followers or 0) + 1
    db.session.commit()
    return back("Following store.")


# ----------------------------------------------------------- become seller
@store_bp.get("/become-seller")
def become_seller_form():
    return render_template("store/become-seller.html")


@store_bp.post("/become-seller")
@login_required
def become_seller():
    user = load_current_user()
    existing = user.seller
    existing_status = (existing.status or "").strip().upper() if existing else ""
    # Rejected applicants may re-submit; other statuses cannot.
    if existing and existing_status != "REJECTED":
        flash("You already have a seller application or store.", "error")
        return redirect("/become-seller")

    data, errors = validate({
        "store_name": "required|string|max:150",
        "owner_name": "required|string|max:100",
        "phone": "nullable|string|max:30",
        "location": "nullable|string|max:100",
        "address": "nullable|string|max:200",
        "country": "nullable|string|max:80",
        "description": "nullable|string|max:1000",
    })
    if errors:
        return back_with_errors(errors, "/become-seller")

    now = utcnow()
    if existing and existing_status == "REJECTED":
        seller = existing
        seller.store_name = data["store_name"]
        seller.owner_name = data["owner_name"]
        seller.phone = data.get("phone") or user.phone
        seller.location = data.get("location")
        seller.address = data.get("address")
        seller.country = data.get("country")
        seller.description = data.get("description")
        seller.status = "PENDING"
        seller.rejection_reason = None
        seller.created_at = now
        log_msg = f"Re-applied seller application '{seller.store_name}'"
        flash_msg = "Application re-submitted. Waiting for admin approval."
    else:
        seller = Seller(
            user_id=user.id, store_name=data["store_name"], owner_name=data["owner_name"],
            phone=data.get("phone") or user.phone, location=data.get("location"),
            address=data.get("address"), country=data.get("country"), description=data.get("description"),
            status="PENDING", level="NEW", rating=0, followers=0, total_products=0, total_sales=0,
            created_at=now, joined_at=now,
        )
        db.session.add(seller)
        log_msg = f"Seller application '{seller.store_name}'"
        flash_msg = "Seller application submitted. Waiting for admin approval."

    # Keep CUSTOMER role until admin approves (avoids /account <-> seller-dashboard redirect loop).
    if user.role != "ADMIN":
        user.role = "CUSTOMER"
    db.session.commit()
    log_activity("APPLY", "Seller", log_msg, "sellers", seller.id)
    flash(flash_msg, "message")
    return redirect("/become-seller")


# ----------------------------------------------------------------- reviews
@store_bp.post("/products/<slug>/review")
@login_required
def submit_review(slug):
    user = load_current_user()
    product = Product.query.filter(Product.slug == slug).first_or_404()
    data, errors = validate({"rating": "required|integer|min:1|max:5", "comment": "nullable|string|max:2000"})
    if errors:
        return back_with_errors(errors, f"/products/{slug}")

    db.session.add(Review(
        product_id=product.id, user_id=user.id, rating=int(data["rating"]),
        comment=data.get("comment"), hidden=False, verified_purchase=False, created_at=utcnow(),
    ))
    db.session.flush()
    avg = (db.session.query(func.avg(Review.rating))
           .filter(Review.product_id == product.id, Review.hidden == 0).scalar())
    product.rating = round(float(avg or 0), 1)
    product.review_count = (Review.query.filter(Review.product_id == product.id, Review.hidden == 0).count())
    db.session.commit()
    return back("Review submitted.")
