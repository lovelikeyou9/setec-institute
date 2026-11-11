
from front import front_bp
from flask import render_template
from product import products as pr, get_product_by_id, get_product_by_category

@front_bp.get('/products')
def products():  # put application's code here
    return render_template('front/products.html', products = pr)

@front_bp.get('/product/<int:product_id>')
def product(product_id):  # put application's code here
    # print(product_id)         # debug
    # assert False, product_id      # debug
    product = get_product_by_id(product_id)
    # assert False, product      # debug
    # (product['category']) | category is getting from product.py that hold data
    related_product = get_product_by_category(product['category'])
    # assert False, related_product
    return render_template('front/product.html', product=product, related_product=related_product)
