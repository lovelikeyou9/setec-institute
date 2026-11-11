
# [ request ] used to get data from the URL (GET request)
# example: http://127.0.0.1:5000/cart?product_id=1

# make_response() is used to create and control the HTTP response before sending it back to the browser.
# render_template() is used to show an HTML file (page) in Flask and send data to it.
from flask import Flask, render_template, request, make_response, jsonify, redirect, url_for, session, flash
from functools import wraps

from product import get_product_by_id, get_product_by_category, products as pr
from users import (
    create_user as register_user,
    delete_user,
    get_all_users,
    get_user_by_id,
    update_user_status,
    user_full_name,
    user_initials,
    verify_user,
)

# json is converts STRING → Python object
import json

app = Flask(__name__)
app.secret_key = 'voltedge-dev-secret-change-in-production'


def login_required(view):
    @wraps(view)
    def wrapped(*args, **kwargs):
        if not session.get('user_id'):
            flash('Please log in to continue.', 'warning')
            return redirect(url_for('login', next=request.path))
        return view(*args, **kwargs)
    return wrapped


def admin_required(view):
    @wraps(view)
    def wrapped(*args, **kwargs):
        if session.get('role') != 'admin':
            flash('Admin login required.', 'warning')
            return redirect(url_for('admin_login', next=request.path))
        return view(*args, **kwargs)
    return wrapped


@app.context_processor
def inject_auth():
    user = get_user_by_id(session['user_id']) if session.get('user_id') else None
    return {
        'current_user': user,
        'is_logged_in': user is not None,
        'is_admin': session.get('role') == 'admin',
    }


def _get_cart_from_cookie():
    raw = request.cookies.get('cart_list')
    if not raw:
        return []
    try:
        data = json.loads(raw)
        return data if isinstance(data, list) else []
    except (json.JSONDecodeError, TypeError):
        return []


def _set_cart_cookie(response, cart_list):
    response.set_cookie(
        'cart_list',
        json.dumps(_normalize_cart(cart_list)),
        path='/',
        max_age=60 * 60 * 24 * 30,
    )
    return response


def _normalize_cart(cart_list):
    """Keep only id + qty in cart storage."""
    return [{"id": int(item["id"]), "qty": int(item["qty"])} for item in cart_list]


def _enrich_cart(cart_list):
    for item in cart_list:
        product = get_product_by_id(item['id'])
        if product:
            item['image'] = product['image']
            item['title'] = product['title']
            item['price'] = product['price']
            item['category'] = product['category']
            item['description'] = product['description']
    return cart_list


def _calc_cart_total(cart_list):
    total = 0
    for item in cart_list:
        if 'price' in item:
            total += float(item['price']) * float(item['qty'])
        else:
            product = get_product_by_id(item['id'])
            if product:
                total += float(product['price']) * float(item['qty'])
    return total


def _cart_summary(cart_list):
    """Build summary numbers from minimal or enriched cart items."""
    total = _calc_cart_total(cart_list)
    item_count = sum(int(item['qty']) for item in cart_list)
    shipping = 0 if total >= 500 else 5
    return {
        'total': total,
        'item_count': item_count,
        'shipping': shipping,
        'grand_total': total + shipping,
    }


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
    related_product = [p for p in get_product_by_category(product['category']) if p['id'] != product_id]
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
    cart_list = _get_cart_from_cookie()
    # if cart_list:
    #     cart_list = json.loads(cart_list)
    # else:
    #     cart_list = []
    # assert False, cart_list

    # for item in list(cart_list):
    #     print(item)
    # assert False, cart_list

    if product_id:

        duplicate_product_ids = [int(item['id']) for item in cart_list]

        if product_id in duplicate_product_ids:
            for item in cart_list:
                if int(item['id']) == product_id:
                    item['qty'] += 1
                    break
        else:
            cart_list.append({
                "id": product_id,
                "qty": 1
            })

        cart_list = _normalize_cart(cart_list)
        resp = make_response(redirect(url_for('cart')))
        _set_cart_cookie(resp, cart_list)
        return resp

    cart_list = _enrich_cart(_normalize_cart(cart_list))
    summary = _cart_summary(cart_list)

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
    response = make_response(render_template(
        'front/cart.html',
        cart_list=cart_list,
        total=summary['total'],
        item_count=summary['item_count'],
        shipping=summary['shipping'],
        grand_total=summary['grand_total'],
    ))


    return response

@app.get('/checkout')
def checkout():  # put application's code here

    cart_list = _enrich_cart(_get_cart_from_cookie())
    total = _calc_cart_total(cart_list)
    ordered = request.args.get('ordered') == '1'

    return render_template('front/checkout.html', cart_list=cart_list, total=total, ordered=ordered)

@app.post('/checkout')
def do_checkout():
    import requests
    cart_list = _enrich_cart(_get_cart_from_cookie())

    total = _calc_cart_total(cart_list)

    str_list = ""
    for item in cart_list:
        str_list += f"<code>{item['title']} ({item['qty']}x{item['price']})</code>\n"

    form = request.form
    first_name = form['firstName']
    last_name = form['lastName']
    email = form['email']
    phone = form['phone']
    address = form['address']

    bot_token = "token"
    url = f"https://api.telegram.org/bot{bot_token}/sendMessage"

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

    if bot_token and bot_token != "token":
        try:
            requests.post(url, json=payload, headers=headers, timeout=3)
        except Exception:
            pass

    resp = make_response(redirect(url_for('checkout', ordered='1')))
    _set_cart_cookie(resp, [])
    return resp

@app.get('/account')
@login_required
def account():
    user = get_user_by_id(session['user_id'])
    return render_template('front/account.html', user=user)

@app.get('/forgot-password')
def forgot_password():
    return render_template('front/forgot-password.html')

@app.route('/login', methods=['GET', 'POST'])
def login():
    if session.get('user_id') and session.get('role') == 'customer':
        return redirect(url_for('account'))

    if request.method == 'POST':
        email = request.form.get('email', '').strip()
        password = request.form.get('password', '')

        user, error = verify_user(email, password)
        if error:
            flash(error, 'danger')
            return render_template('front/login.html')

        if user['role'] == 'admin':
            flash('Please use the admin login page for administrator access.', 'warning')
            return render_template('front/login.html')

        session.clear()
        session['user_id'] = user['id']
        session['role'] = user['role']
        session['user_name'] = user_full_name(user)
        session['user_initials'] = user_initials(user)

        next_url = request.args.get('next') or url_for('account')
        return redirect(next_url)

    return render_template('front/login.html')

@app.route('/create-user', methods=['GET', 'POST'])
def create_user():
    if session.get('user_id') and session.get('role') == 'customer':
        return redirect(url_for('account'))

    if request.method == 'POST':
        first_name = request.form.get('firstName', '')
        last_name = request.form.get('lastName', '')
        email = request.form.get('email', '')
        password = request.form.get('password', '')
        confirm = request.form.get('confirmPassword', '')

        if password != confirm:
            flash('Passwords do not match.', 'danger')
            return render_template('front/create-user.html')

        user, error = register_user(first_name, last_name, email, password, role='customer')
        if error:
            flash(error, 'danger')
            return render_template('front/create-user.html')

        session.clear()
        session['user_id'] = user['id']
        session['role'] = user['role']
        session['user_name'] = user_full_name(user)
        session['user_initials'] = user_initials(user)
        flash('Account created successfully!', 'success')
        return redirect(url_for('account'))

    return render_template('front/create-user.html')


@app.get('/logout')
def logout():
    session.clear()
    flash('You have been logged out.', 'success')
    return redirect(url_for('home'))


def _get_dashboard_stats():
    categories = {}
    total_reviews = 0
    total_price = 0

    for p in pr:
        cat = p['category']
        categories[cat] = categories.get(cat, 0) + 1
        total_reviews += p['rating']['count']
        total_price += p['price']

    max_count = max(categories.values()) if categories else 1
    category_list = [
        {'name': name, 'count': count, 'percent': round(count / max_count * 100)}
        for name, count in sorted(categories.items(), key=lambda x: -x[1])
    ]

    top_products = sorted(pr, key=lambda x: x['rating']['rate'], reverse=True)[:5]

    return {
        'total_products': len(pr),
        'total_categories': len(categories),
        'avg_price': f"{total_price / len(pr):.2f}" if pr else "0.00",
        'total_reviews': total_reviews,
    }, category_list, top_products


@app.get('/admin')
def admin_redirect():
    if session.get('role') == 'admin':
        return redirect(url_for('dashboard'))
    return redirect(url_for('admin_login'))


@app.route('/admin/login', methods=['GET', 'POST'])
def admin_login():
    if session.get('role') == 'admin':
        return redirect(url_for('dashboard'))

    if request.method == 'POST':
        email = request.form.get('email', '').strip()
        password = request.form.get('password', '')

        user, error = verify_user(email, password)
        if error:
            flash(error, 'danger')
            return render_template('admin/login.html')

        if user['role'] != 'admin':
            flash('Access denied. Administrator account required.', 'danger')
            return render_template('admin/login.html')

        session.clear()
        session['user_id'] = user['id']
        session['role'] = user['role']
        session['user_name'] = user_full_name(user)
        session['user_initials'] = user_initials(user)

        next_url = request.args.get('next') or url_for('dashboard')
        return redirect(next_url)

    return render_template('admin/login.html')


@app.get('/admin/logout')
def admin_logout():
    session.clear()
    flash('Admin logged out.', 'success')
    return redirect(url_for('admin_login'))


@app.get('/admin/dashboard')
@admin_required
def dashboard():
    module = 'dashboard'
    stats, categories, top_products = _get_dashboard_stats()
    return render_template(
        'admin/dashboard/dashboard.html',
        module=module,
        stats=stats,
        categories=categories,
        top_products=top_products,
    )

@app.get('/admin/user')
@admin_required
def user():
    module = 'user'
    users = get_all_users()
    return render_template('admin/user/user.html', module=module, users=users)


@app.post('/admin/user/<int:user_id>/toggle-status')
@admin_required
def admin_toggle_user_status(user_id):
    user = get_user_by_id(user_id)
    if not user:
        flash('User not found.', 'danger')
        return redirect(url_for('user'))

    new_status = 'inactive' if user['status'] == 'active' else 'active'
    ok, error = update_user_status(user_id, new_status)
    if error:
        flash(error, 'danger')
    else:
        flash(f"User status updated to {new_status}.", 'success')
    return redirect(url_for('user'))


@app.post('/admin/user/<int:user_id>/delete')
@admin_required
def admin_delete_user(user_id):
    ok, error = delete_user(user_id)
    if error:
        flash(error, 'danger')
    else:
        flash('User deleted.', 'success')
    return redirect(url_for('user'))


"""
@app.get('/api/products')
def get_all_products():
    return product
"""

@app.post("/update_qty")
def update_qty():
    cart_list = _normalize_cart(_get_cart_from_cookie())

    item_id = int(request.form["id"])
    action = request.form["action"]

    found = False
    updated_qty = 0
    line_total = 0

    for item in cart_list:
        if item["id"] == item_id:
            if action == "increase":
                item["qty"] += 1
            elif action == "decrease" and item["qty"] > 1:
                item["qty"] -= 1

            updated_qty = item["qty"]
            product = get_product_by_id(item_id)
            if product:
                line_total = float(product["price"]) * item["qty"]
            found = True
            break

    if not found:
        return jsonify({"success": False, "error": "Item not found"}), 404

    summary = _cart_summary(cart_list)

    response = jsonify({
        "success": True,
        "qty": updated_qty,
        "line_total": f"{line_total:.2f}",
        "total": f"{summary['total']:.2f}",
        "item_count": summary["item_count"],
        "shipping": f"{summary['shipping']:.2f}",
        "grand_total": f"{summary['grand_total']:.2f}",
        "free_shipping": summary["shipping"] == 0,
    })
    _set_cart_cookie(response, cart_list)
    return response


@app.post("/remove_item")
def remove_item():
    cart_list = _normalize_cart(_get_cart_from_cookie())

    item_id = int(request.form["id"])
    cart_list = [item for item in cart_list if item["id"] != item_id]

    summary = _cart_summary(cart_list)

    response = jsonify({
        "success": True,
        "total": f"{summary['total']:.2f}",
        "item_count": summary["item_count"],
        "shipping": f"{summary['shipping']:.2f}",
        "grand_total": f"{summary['grand_total']:.2f}",
        "free_shipping": summary["shipping"] == 0,
        "empty": len(cart_list) == 0,
    })
    _set_cart_cookie(response, cart_list)
    return response


if __name__ == '__main__':
    app.run()
