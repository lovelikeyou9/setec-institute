
from admin import admin_bp
from flask import render_template, request, session, redirect, url_for
from werkzeug.security import check_password_hash
from extensions import db
from sqlalchemy import text
from functools import wraps

def login_required(view):
    @wraps(view)
    def wrapped(*args, **kwargs):
        if not session.get("is_login"):
            return redirect(url_for("admin_login", next=request.path))
        return view(*args, **kwargs)

    return wrapped

@admin_bp.get('/login')
def admin_login():
    module = 'login'
    return render_template('/login.html')

@admin_bp.post('/login')
def admin_do_login():
    module = 'login'
    # assert False, request.form
    form = request.form
    username = form.get('username').strip()
    password = form.get('password')

    sql = text("SELECT * FROM user WHERE username = :username")
    user = db.session.execute(sql, {"username": username}).fetchone()
    # assert False, user
    # assert False, check_password_hash(user[4], password)

    if user:
        # check password
        # pass
        if check_password_hash(user[4], password):
            session.clear()
            # session['user_id'] = user[0]
            session['is_login'] = True
            session['user_id'] = user[0]
            session['profile'] = user[1]
            session['username'] = user[2]
            session['email'] = user[3]
            return redirect(url_for('dashboard'))
        else:
            # Wrong password
            return redirect(url_for('admin_login'))
    else:
        return redirect(url_for('admin_login'))

    # return redirect(url_for('dashboard'))