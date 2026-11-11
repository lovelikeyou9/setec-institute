from flask import Flask, render_template

# from F.test_5_F.product import get_product_by_category
from product import get_product_by_id, get_product_by_category, products as pr

app = Flask(__name__) # when it is running. it start from [ if __name__ = '__main__' ]


"""
# route is can do GET/PUT/POST/DELETE
@app.route('/user', methods=['GET', 'POST', 'PUT', 'DELETE'])

@app.get('/user')
@app.post('/user')
@app.put('/user')
@app.delete('/user')

@app.route('/') # [ / ] is route domain and it start from this
def hello_world():  # put application's code here
    return 'Hello World!'
"""

@app.get('/')
@app.get('/home')
def home():  # put application's code here
# def = function
    # return render_template('home.html')
    # render_template is a function from Flask
    return render_template('front/index.html', products = pr)

@app.get('/products')
def products():  # put application's code here
    return render_template('front/products.html', products = pr)

@app.get('/product/<int:product_id>')
def product(product_id):  # put application's code here
    # print(product_id)         # debug
    # assert False, product_id      # debug
    product = get_product_by_id(product_id)
    # assert False, product      # debug
    # (product['category']) | category is getting from product.py that hold data
    related_product = get_product_by_category(product['category'])
    # assert False, related_product
    return render_template('front/product.html', product=product, related_product=related_product)

@app.get('/cart')
def cart():  # put application's code here
    return render_template('front/cart.html')

@app.get('/checkout')
def checkout():  # put application's code here
    return render_template('front/checkout.html')

@app.get('/account')
def account():
    return render_template('front/account.html')

@app.get('/forgot-password')
def forgot_password():
    return render_template('front/forgot-password.html')

@app.get('/login')
def login():
    return render_template('front/login.html')

@app.get('/create-user')
def create_user():
    return render_template('front/create-user.html')

"""
@app.get('/api/products')
def get_all_products():
    return product
"""


if __name__ == '__main__':
    app.run()
