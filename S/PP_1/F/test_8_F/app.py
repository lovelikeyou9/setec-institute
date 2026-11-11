
# [ request ] used to get data from the URL (GET request)
# example: http://127.0.0.1:5000/cart?product_id=1

# make_response() is used to create and control the HTTP response before sending it back to the browser.
# render_template() is used to show an HTML file (page) in Flask and send data to it.
from flask import Flask, render_template, request, make_response, jsonify

# from F.test_5_F.product import get_product_by_category
from product import get_product_by_id, get_product_by_category, products as pr

# json is converts STRING → Python object
import json

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

    # assert False, cart_list


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

    bot_token = "8824165750:AAGjJcnPQdq_eXlJ-cQNYMZDSLMsmOBauOQ"

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
    message += f"<i>================================</i>"
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

    response = jsonify({"qty": updated_qty})
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



if __name__ == '__main__':
    app.run()
