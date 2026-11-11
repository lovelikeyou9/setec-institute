
from front import front_bp
from flask import render_template, request, make_response
from product import products as pr, get_product_by_id
import json

@front_bp.get('/checkout')
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

@front_bp.post('/checkout')
def do_checkout():
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
    from telegram_notify import send_telegram

    message = f"<b>🚨You have New Order</b>\n"
    message += f"<b>🧔Customer Name: {first_name} {last_name}</b>\n"
    message += f"<code>📱Phone : {phone}</code>\n"
    message += f"<code>📧Email : {email}</code>\n"
    message += f"<code>📍Address: {address}</code>\n"
    message += f"<i>================================</i>\n"
    message += f"<code>ℹ️ Product: {str_list}</code>"
    message += f"<i>================================</i>\n"
    message += f"<b>💰 Total: ${total:.2f}</b>\n"
    send_telegram(message)

    return render_template('front/checkout.html', cart_list=[], total=0)