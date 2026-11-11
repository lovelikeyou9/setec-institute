
from front import front_bp
from flask import render_template, abort
from catalog import products_as_dicts, get_product_by_id, get_product_by_category


@front_bp.get("/products")
def products():
    return render_template("front/products.html", products=products_as_dicts())


@front_bp.get("/product/<int:product_id>")
def product(product_id):
    product = get_product_by_id(product_id)
    if not product:
        abort(404)
    related_product = [
        p for p in get_product_by_category(product.get("category"))
        if p["id"] != product["id"]
    ][:4]
    return render_template(
        "front/product.html",
        product=product,
        related_product=related_product,
    )
