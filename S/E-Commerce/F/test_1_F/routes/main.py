from flask import Blueprint, render_template

from models import Category, Product

main_bp = Blueprint("main", __name__)


@main_bp.route("/")
def index():
    categories = Category.query.order_by(Category.name).all()
    featured = (
        Product.query.filter_by(is_featured=True)
        .order_by(Product.created_at.desc())
        .limit(8)
        .all()
    )
    if len(featured) < 4:
        featured = Product.query.order_by(Product.created_at.desc()).limit(8).all()

    best_sellers = Product.query.order_by(Product.sales_count.desc()).limit(8).all()
    new_products = Product.query.order_by(Product.created_at.desc()).limit(8).all()

    home_categories = []
    preferred = [
        "Laptops",
        "Gaming PCs",
        "Graphics Cards",
        "CPUs",
        "RAM",
        "SSD",
        "Monitors",
        "Accessories",
    ]
    by_name = {c.name: c for c in categories}
    for name in preferred:
        if name in by_name:
            home_categories.append(by_name[name])
    if len(home_categories) < 8:
        for c in categories:
            if c not in home_categories:
                home_categories.append(c)
            if len(home_categories) >= 8:
                break

    return render_template(
        "index.html",
        categories=home_categories,
        featured=featured,
        best_sellers=best_sellers,
        new_products=new_products,
    )
