
from front import front_bp
from flask import render_template
from product import products as pr

@front_bp.get('/')
@front_bp.get('/home')
def home():  # put application's code here
# def = function
    # return render_template('home.html')
    # render_template is a function from Flask
    return render_template('front/index.html', products = pr)