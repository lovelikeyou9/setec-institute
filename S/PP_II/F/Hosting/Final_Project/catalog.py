"""Catalog helpers — prefer DB products; fall back to seed list in product.py."""
import re

from extensions import db
from models.brand import Brand
from models.category import Category
from models.product_model import Product


def slugify(text):
    text = (text or "").strip().lower()
    text = re.sub(r"[^a-z0-9]+", "-", text)
    return text.strip("-") or "item"


def products_as_dicts(active_only=True):
    q = Product.query
    if active_only:
        q = q.filter_by(is_active=True)
    rows = q.order_by(Product.id.asc()).all()
    if rows:
        return [p.to_dict() for p in rows]
    # fallback to hardcoded FakeStore list
    from product import products as seed
    return list(seed)


def get_product_by_id(product_id):
    product = Product.query.get(product_id)
    if product and product.is_active:
        return product.to_dict()
    from product import get_product_by_id as seed_get
    return seed_get(product_id)


def get_product_by_category(category_name):
    if not category_name:
        return []
    cat = Category.query.filter_by(name=category_name).first()
    if cat:
        return [p.to_dict() for p in Product.query.filter_by(category_id=cat.id, is_active=True).all()]
    from product import get_product_by_category as seed_get
    return seed_get(category_name)


def ensure_catalog_seeded():
    """Create brand/category/product tables data from product.py once."""
    if Product.query.count() > 0:
        return

    from product import products as seed

    default_brand = Brand.query.filter_by(slug="voltedge").first()
    if not default_brand:
        default_brand = Brand(
            name="VoltEdge",
            slug="voltedge",
            description="Default store brand",
        )
        db.session.add(default_brand)
        db.session.flush()

    cat_cache = {}
    for item in seed:
        cat_name = item.get("category") or "Uncategorized"
        if cat_name not in cat_cache:
            cat = Category.query.filter_by(name=cat_name).first()
            if not cat:
                cat = Category(name=cat_name, slug=slugify(cat_name))
                db.session.add(cat)
                db.session.flush()
            cat_cache[cat_name] = cat

        rating = item.get("rating") or {}
        product = Product(
            id=item["id"],
            title=item["title"],
            price=float(item["price"]),
            description=item.get("description") or "",
            image=item.get("image") or "",
            rating_rate=float(rating.get("rate") or 0),
            rating_count=int(rating.get("count") or 0),
            is_active=True,
            category_id=cat_cache[cat_name].id,
            brand_id=default_brand.id,
        )
        db.session.add(product)

    db.session.commit()
