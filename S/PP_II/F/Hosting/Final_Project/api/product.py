
from api import api_bp
from catalog import products_as_dicts, get_product_by_id
from flask import jsonify, abort


@api_bp.get("/products")
def products():
    return jsonify(products_as_dicts())


@api_bp.get("/products/<int:product_id>")
def product_detail(product_id):
    product = get_product_by_id(product_id)
    if not product:
        abort(404)
    return jsonify(product)
