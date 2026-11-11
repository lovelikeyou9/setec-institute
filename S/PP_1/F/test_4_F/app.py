from flask import Flask, render_template
from product import products as pr

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

@app.get('/product/pro_id')
def product(pro_id):  # put application's code here
    return render_template('front/product.html')

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
