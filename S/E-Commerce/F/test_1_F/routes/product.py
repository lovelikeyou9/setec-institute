from flask import Blueprint, render_template, request, abort
from sqlalchemy import or_

from models import Product, Category

product_bp = Blueprint("product", __name__)


@product_bp.route("/products")
def product_list():
    page = request.args.get("page", 1, type=int)
    per_page = 12
    q = request.args.get("q", "").strip()
    category_id = request.args.get("category", type=int)
    brand = request.args.get("brand", "").strip()
    min_price = request.args.get("min_price", type=float)
    max_price = request.args.get("max_price", type=float)
    sort = request.args.get("sort", "newest")

    query = Product.query

    if q:
        like = f"%{q}%"
        query = query.filter(
            or_(
                Product.name.ilike(like),
                Product.brand.ilike(like),
                Product.description.ilike(like),
            )
        )
    if category_id:
        query = query.filter_by(category_id=category_id)
    if brand:
        query = query.filter_by(brand=brand)
    if min_price is not None:
        query = query.filter(Product.price >= min_price)
    if max_price is not None:
        query = query.filter(Product.price <= max_price)

    if sort == "price_asc":
        query = query.order_by(Product.price.asc())
    elif sort == "price_desc":
        query = query.order_by(Product.price.desc())
    elif sort == "popularity":
        query = query.order_by(Product.sales_count.desc())
    else:
        query = query.order_by(Product.created_at.desc())

    pagination = query.paginate(page=page, per_page=per_page, error_out=False)
    categories = Category.query.order_by(Category.name).all()
    brands = (
        Product.query.with_entities(Product.brand)
        .distinct()
        .order_by(Product.brand)
        .all()
    )
    brands = [b[0] for b in brands]

    return render_template(
        "products.html",
        products=pagination.items,
        pagination=pagination,
        categories=categories,
        brands=brands,
        filters={
            "q": q,
            "category": category_id,
            "brand": brand,
            "min_price": min_price if min_price is not None else "",
            "max_price": max_price if max_price is not None else "",
            "sort": sort,
        },
    )


@product_bp.route("/products/<int:product_id>")
def product_detail(product_id):
    product = Product.query.get_or_404(product_id)
    related = (
        Product.query.filter(
            Product.category_id == product.category_id,
            Product.id != product.id,
        )
        .order_by(Product.sales_count.desc())
        .limit(4)
        .all()
    )
    return render_template("product_detail.html", product=product, related=related)
