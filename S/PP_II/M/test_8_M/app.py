
# [ request ] used to get data from the URL (GET request)
# example: http://127.0.0.1:5000/cart?product_id=1

# make_response() is used to create and control the HTTP response before sending it back to the browser.
# render_template() is used to show an HTML file (page) in Flask and send data to it.
from flask import Flask, render_template, request, make_response, jsonify, redirect, url_for

# from F.test_5_F.product import get_product_by_category
from product import get_product_by_id, get_product_by_category, products as pr

from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate

from sqlalchemy import text

from werkzeug.security import generate_password_hash
import os
from upload_service import save_user_image



# json is converts STRING → Python object
import json

app = Flask(__name__) # when it is running. it start from [ if __name__ = '__main__' ]

app.config["SQLALCHEMY_DATABASE_URI"] = "sqlite:///mydb.sqlite3"
app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False

UPLOAD_DIR = os.path.join("static", "images", "user")
os.makedirs(UPLOAD_DIR, exist_ok=True)
ALLOWED_EXT = {"png", "jpg", "jpeg", "gif"}

db = SQLAlchemy(app)
migrate = Migrate(app, db)

class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    profile = db.Column(db.String(80), nullable=True)
    username = db.Column(db.String(80), nullable=False)
    email = db.Column(db.String(120), nullable=False, unique=True)
    password = db.Column(db.String(255), nullable=False)
    role = db.Column(db.String(120), nullable=False, default='admin')



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


@app.get('/cart') # /cart is URL (page link)
def cart():  # put application's code here

    # 👉 GET request (Query String)
    # | Method       | Use |
    # | ------------ | ------------------- |
    # | request.args | GET data from URL
    # | request.form | POST form data
    # | request.json | JSON(fetch / AJAX)
    # request.args.get('product_id') is get from URL
    # http://127.0.0.1:5000/cart?product_id=5
    # product_id = request.args.get('product_id')
    product_id = request.args.get('product_id', type=int)

    # request.cookies is used to get data stored in the browser cookies
    # request.cookies.get() returns only str (string) in Flask
    cart_list = request.cookies.get('cart_list')

    # [ loads ] converts a JSON string → Python object
    cart_list = json.loads(cart_list) if cart_list else []
    # if cart_list:
    #     cart_list = json.loads(cart_list)
    # else:
    #     cart_list = []
    # assert False, cart_list

    # for item in list(cart_list):
    #     print(item)
    # assert False, cart_list

    updated = False

    if product_id:

        # check existing IDs in cart
        # existing_ids = [int(item['id']) for item in cart_list]

        duplicate_product_ids = [int(item['id']) for item in cart_list]
        # duplicate_product_ids = []
        # for item in cart_list:
        #     duplicate_product_ids.append(item['id'])

        if product_id in duplicate_product_ids:
            for item in cart_list:
                if int(item['id']) == product_id:
                    item['qty'] += 1
                    break
        else:
            # cart_list.append({"id": product_id, "qty": 1})
            # if cart_list did not have qty in json/data like {"id": 1}
            # then i add qty {"id": 1, "qty": 1} ?

            # and it can be duplicate too like
            # when i reload page then is auto increase {"id": 1, "qrt": 1}
            # {"id": 1, "qrt": 1}
            # {"id": 1, "qrt": 1}
            # cart_list.append({"id": product_id, "qty": 1})
            # cart_list.append({"id": int(product_id), "qty": 1})
            cart_list.append({
                "id": product_id,
                "qty": 1
            })

        updated = True
        # assert False, product_id
    elif not product_id or product_id == '':
        pass
        # assert False, 'No product id provided'

    # map data
    # it called [ Merge Duplicate Product ]
    for item in cart_list:

        product = get_product_by_id(item['id'])

        if product:
            # assert False, item
            # assert False, get_product_by_id(12)
            # assert False, get_product_by_id(12)['image']
            item['image'] = get_product_by_id(item['id'])['image']
            item['title'] = get_product_by_id(item['id'])['title']
            item['price'] = get_product_by_id(item['id'])['price']
            item['category'] = get_product_by_id(item['id'])['category']
            item['description'] = get_product_by_id(item['id'])['description']

    assert False, cart_list


    # 1. Create the response object with the rendered template
    # make_response() is used to create and control the HTTP response before sending it back to the browser
    # return "text"       simple response
    # make_response()     advanced response control

    # render_template() is used to show an HTML file (page) in Flask and send data to it.

    # make_response(...)
    # So instead of just returning HTML, you now get something you can control:
    # add cookies 🍪
    # add headers
    # modify response

    # render_template('front/cart.html', cart_list=cart_list) is Load and display the HTML file cart.html to the browser.
    # or for display that data got from =cart_list
    response = make_response(render_template('front/cart.html', cart_list=cart_list))

    # data = [
    #     {"id": 1, "qty": 1},
    # ]

    # 2. Set the cookie (key, value)
    # response.set_cookie('cart_list', '[{"id": 1}]')

    # [ json.dumps(cart_list) ] Convert Python list → JSON string
    # 3. response.set_cookie(...)
    # 👉 Save that string into browser cookie.
    # cart_list is cookies 🍪

    # 1. set_cookie()
    # 👉 Creates/saves the cookie in browser
    # 2. json.dumps(cart_list)
    # 👉 Converts Python list → string
    if updated:
        response.set_cookie('cart_list', json.dumps(cart_list))

    # 3. Return the response to the browser
    # It is used to send the final result back to the browser.
    # Send the page + data + cookies + everything to user’s browser
    return response

    # return make_response('front/cart.html')
    # return render_template('front/cart.html')

@app.get('/checkout')
def checkout():  # put application's code here

    cart_list = request.cookies.get('cart_list')

    cart_list = json.loads(cart_list) if cart_list else []

    # total price
    total = 0
    for item in cart_list:
        total += float(item['price']) * float(item['qty'])
    # assert False, total

    # assert False, cart_list

    return render_template('front/checkout.html', cart_list=cart_list, total=total)

@app.post('/checkout')
def do_checkout():
    import requests
    cart_list = request.cookies.get('cart_list')
    # assert False, cart_list
    cart_list = json.loads(cart_list) if cart_list else []

    # Total checkout
    total = 0
    for item in cart_list:
        total += float(item["price"]) * float(item["qty"])

    str_list = ""
    for item in cart_list:
        str_list += f"<code>{item['title']} ({item['qty']}x{item['price']})</code>\n"
    # assert False, str_list

    form = request.form
    first_name = form['firstName']
    last_name = form['lastName']
    email = form['email']
    phone = form['phone']
    address = form['address']

    # send notification to telegram
    import requests

    bot_token = "token"

    url = f"https://api.telegram.org/bot{bot_token}/sendMessage"

    # message = f"New order from {first_name} {last_name} ({email}, {phone})\nAddress: {address}\n\nOrder details:\n"
    # message = f"<b>New Order</b>\n"
    message = f"<b>🚨You have New Order</b>\n"
    message += f"<b>🧔Customer Name: {first_name} {last_name}</b>\n"
    message += f"<code>📱Phone : {phone}</code>\n"
    message += f"<code>📧Email : {email}</code>\n"
    message += f"<code>📍Address: {address}</code>\n"
    message += f"<i>================================</i>\n"
    message += f"<code>ℹ️ Product: {str_list}</code>"
    message += f"<i>================================</i>\n"
    message += f"<b>💰 Total: ${total:.2f}</b>\n"
    payload = {
        "text": f"{message}",
        "parse_mode": "HTML",
        "chat_id": "@sv26shopchouyamata",
        "disable_web_page_preview": False,
        "disable_notification": False,
        "reply_to_message_id": None
    }
    headers = {
        "accept": "application/json",
        "User-Agent": "Telegram Bot SDK - (https://github.com/irazasyed/telegram-bot-sdk)",
        "content-type": "application/json"
    }

    response = requests.post(url, json=payload, headers=headers)
    assert False, response.text
    # print(response.text)
    assert False, f"Checkout data: {first_name} {last_name} {email} {phone} {address} Cart: {cart_list}"

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


@app.get('/admin/dashboard')
def dashboard():
    module = 'dashboard'
    return render_template('admin/dashboard/dashboard.html', module=module)

@app.get('/admin/user')
def user():
    module = 'user'
    sql = text("SELECT * FROM user")
    result = db.session.execute(sql)
    rows = [dict(row._mapping) for row in result]
    return render_template('admin/user/user.html', module=module, users=rows)


@app.get('/admin/user/add')
def add_user():
    module = 'user'
    return render_template('admin/user/add.html', module=module)


# @app.post('/admin/user/add')
# def do_add_user():
#     # profile = request.form['profile']
#     username = request.form['username']
#     email = request.form['email']
#     password = request.form['password']
#     role = request.form['role']
#
#     u = User(profile='avatar.png', username=username, email=email, password=password, role=role)
#
#     # assert False, u
#     # assert False, vars(u)
#
#     db.session.add(u)
#     db.session.commit()
#     return redirect(url_for('user'))


@app.post('/admin/user/add')
def do_add_user():
    form = request.form
    file = request.files.get("image")

    password = generate_password_hash(form["password"])

    # save user first so we have id (needed for 1_org_user.png / 1_thum_user.png)
    user = User(
        profile=None,
        username=form.get('username'),
        email=form.get('email'),
        password=password,
        role=form.get('role')
    )
    db.session.add(user)
    db.session.commit()

    filename = save_user_image(file, user.id, UPLOAD_DIR, ALLOWED_EXT)
    if filename:
        user.profile = filename
        db.session.commit()

    return redirect(url_for('user'))



@app.get('/admin/user/edit/<int:user_id>')
def edit_user(user_id):
    module = 'user'
    sql = text("SELECT * FROM user WHERE id = :user_id")
    result = db.session.execute(sql, {"user_id": user_id}).fetchone()
    user = None
    if result:
        user = dict(result._mapping)
    else:
        return redirect(url_for('user'))
    return render_template('admin/user/edit.html', module=module, user=user)

@app.post('/admin/user/edit')
def do_edit_user():
    module = 'user'
    form = request.form
    user = User.query.get(form.get('user_id'))
    if not user:
        return redirect(url_for('user'))

    file = request.files.get("image")
    filename = save_user_image(file, user.id, UPLOAD_DIR, ALLOWED_EXT)
    if filename:
        user.profile = filename

    user.username = form.get('username')
    user.email = form.get('email')
    user.role = form.get('role')
    if form.get('password') is not None and form.get('password') != '':
        user.password = generate_password_hash(form.get('password'))

    db.session.commit()
    return redirect(url_for('user'))

@app.get('/admin/user/confirm-delete/<int:user_id>')
def confirm_delete(user_id):
    module = 'user'
    sql = text("SELECT * FROM user WHERE id = :user_id")
    result = db.session.execute(sql, {"user_id": user_id}).fetchone()
    user = None
    if result:
        user = dict(result._mapping)
    else:
        return redirect(url_for('user'))
    return render_template('admin/user/confirm_delete.html', module=module, user=user)



"""
@app.get('/api/products')
def get_all_products():
    return product
"""

@app.post("/update_qty")
def update_qty():

    # cart_list = '[{"id": 1, "qty": 2}, {"id": 2, "qty": 1}]'
    cart_list = request.cookies.get('cart_list')
    # print(cart_list)
    # after convert
    # cart_list = [
    #     {"id": 1, "qty": 2},
    #     {"id": 2, "qty": 1}
    # ]
    cart_list = json.loads(cart_list) if cart_list else []

    # item_id = request.form["id"] is get id from click
    # item_id = request.form["id"]    # "1"
    item_id = int(request.form["id"])
    # item_id = int(request.form["id"])
    action = request.form["action"] # "increase"

    updated_qty = 0

    for item in cart_list:
        if item["id"] == item_id:

            if action == "increase":
                item["qty"] += 1


            elif action == "decrease" and item['qty'] > 1:
                item["qty"] -= 1

            updated_qty = item["qty"]
            # print(updated_qty)
            break

    response = jsonify({
        "qty": updated_qty,
        "action": action
    })
    # response = jsonify({"qty": updated_qty})
    response.set_cookie('cart_list', json.dumps(cart_list))
    return response


@app.post("/remove_item")
def remove_item():
    cart_list = request.cookies.get('cart_list')
    cart_list = json.loads(cart_list) if cart_list else []

    item_id = int(request.form["id"])

    # remove item
    cart_list = [item for item in cart_list if item["id"] != item_id]

    response = jsonify({"success": True})
    response.set_cookie('cart_list', json.dumps(cart_list))

    return response

@app.post('/admin/user/delete')
def delete_user():
    module = 'user'
    form = request.form
    user_id = form.get('user_id')
    # assert False, user_id
    user = User.query.get(user_id)
    if not user:
        return redirect(url_for('user'))
    db.session.delete(user)
    db.session.commit()
    # assert False, form
    return redirect(url_for('user'))

if __name__ == '__main__':
    app.run()
