from flask import Flask, render_template, request
from bakong_khqr import KHQR

app = Flask(__name__)


@app.get('/')
def home():  # put application's code here
    return render_template('home.html')

@app.post('/buy-now')
def buy_now():
    form = request.form
    price = float(form.get('price'))

    # Without token (Offline EMVCo Generator) or with NBC Token
    khqr = KHQR("your_nbc_developer_token_here")

    # Full EMVCo parameters are required
    qr_string = khqr.create_qr(
        account_id="user_name@bank",
        merchant_name="Modern Multiple Technologies",
        merchant_city="Phnom Penh",
        amount=price,
        currency="KHR",
        store_label="Cashier-01",
        phone_number="010411311",
        bill_number="TRX012345",
        terminal_label="POS-01",
        static=False,
        expiration=1
    )

    # # Generate styled KHQR PNG image
    # image_path = khqr.qr_image(qr_string, format="png")
    # print("Saved to:", image_path)

    # Or export as Base64 Data URI for web embedding
    base64_uri = khqr.qr_image(qr_string, format="base64_uri")
    return f"<img src=' {base64_uri}'/>"

    # return render_template('home.html')

if __name__ == '__main__':
    app.run()
