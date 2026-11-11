from flask import Flask, render_template
from product import product

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
def home():  # put application's code here
# def = function
    # return render_template('home.html')
    # render_template is a function from Flask
    return render_template('front/home.html')

@app.get('/product')
def product():  # put application's code here
    return render_template('front/{{ url_for('product') }}')

@app.get('/product-detail')
def product_detail():  # put application's code here
    return render_template('front/product-detail.html')

@app.get('/cart')
def cart():  # put application's code here
    return render_template('front/cart.html')

@app.get('/checkout')
def checkout():  # put application's code here
    return render_template('front/checkout.html')

"""
@app.get('/api/products')
def get_all_products():
    return product
"""


if __name__ == '__main__':
    app.run()
