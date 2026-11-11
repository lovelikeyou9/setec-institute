
from front import front_bp
from flask import render_template, request, make_response, jsonify
from product import products as pr, get_product_by_id
import json

@front_bp.get('/cart') # /cart is URL (page link)
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

@front_bp.post("/update_qty")
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

