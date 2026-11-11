
# [ request ] used to get data from the URL (GET request)
# example: http://127.0.0.1:5000/cart?product_id=1

# make_response() is used to create and control the HTTP response before sending it back to the browser.
# render_template() is used to show an HTML file (page) in Flask and send data to it.
from flask import Flask, render_template

from config import Config

# json is converts STRING → Python object
import json

from extensions import db, migrate, limiter

from front import front_bp
from admin import admin_bp
from api import api_bp

app = Flask(__name__) # when it is running. it start from [ if __name__ = '__main__' ]
app.config.from_object(Config)

db.init_app(app)
migrate.init_app(app, db)
# test_3_F
limiter.init_app(app)

# load model
import models

app.register_blueprint(front_bp, url_prefix="/")
app.register_blueprint(admin_bp, url_prefix="/admin")
app.register_blueprint(api_bp, url_prefix="/api")

# test_3_F
@app.errorhandler(404)    # 404 is number status
def page_not_found(e):    # [ e ]
    return render_template('error/404.html'), 404

# test_3_F
@app.errorhandler(429)    # 404 is number status
def to_many_requests(e):
    return render_template('error/429.html'), 429

# test_3_F
@app.errorhandler(500)    # 404 is number status
def server_error(e):
    return render_template('error/500.html'), 500


# # ==================================================
# # test_3_F_homework   # telegram notification ( 404, 429, 500 )
# @app.post('/checkout')
# def do_checkout():
#     import requests
#     cart_list = request.cookies.get('cart_list')
#     # assert False, cart_list
#     cart_list = json.loads(cart_list) if cart_list else []
#     str_list = ""
#     for item in cart_list:
#         str_list += f"<code>{item['title']} ({item['qty']}x{item['price']})</code>\n"
#     # assert False, str_list
#
#     form = request.form
#     first_name = form['firstName']
#     last_name = form['lastName']
#     email = form['email']
#     phone = form['phone']
#     address = form['address']
#
#     # send notification to telegram
#     import requests
#
#     bot_token = "8534615271:AAF5OUxrVNusHF8x166X41uP0ZoNJZzsibg"
#
#     url = f"https://api.telegram.org/bot{bot_token}/sendMessage"
#
#     # message = f"New order from {first_name} {last_name} ({email}, {phone})\nAddress: {address}\n\nOrder details:\n"
#     # message = f"<b>New Order</b>\n"
#     message = f"<b>🚨You have New Order</b>\n"
#     message += f"<b>🧔Customer Name: {first_name} {last_name}</b>\n"
#     message += f"<code>📱Phone : {phone}</code>\n"
#     message += f"<code>📧Email : {email}</code>\n"
#     message += f"<code>📍Address: {address}</code>\n"
#     message += f"<i>================================</i>\n"
#     message += f"<code>ℹ️ Product: {str_list}</code>"
#     message += f"<i>================================</i>"
#     payload = {
#         "text": f"{message}",
#         "parse_mode": "HTML",
#         "chat_id": "@sv2_6_pp_ii_test_3_f",
#         "disable_web_page_preview": False,
#         "disable_notification": False,
#         "reply_to_message_id": None
#     }
#     headers = {
#         "accept": "application/json",
#         "User-Agent": "Telegram Bot SDK - (https://github.com/irazasyed/telegram-bot-sdk)",
#         "content-type": "application/json"
#     }
#
#     response = requests.post(url, json=payload, headers=headers)
#     assert False, response.text
#     # print(response.text)
#     assert False, f"Checkout data: {first_name} {last_name} {email} {phone} {address} Cart: {cart_list}"
# # ==================================================



if __name__ == '__main__':
    app.run()
