
from admin import admin_bp
from flask import render_template, request, session, redirect, url_for
from werkzeug.security import check_password_hash
from extensions import db
from sqlalchemy import text
from functools import wraps

from extensions import limiter
from activity_logger import log_activity

def login_required(view):
    @wraps(view)
    def wrapped(*args, **kwargs):
        if not session.get("is_login"):
            return redirect(url_for("admin_bp.admin_login", next=request.path))
        return view(*args, **kwargs)

    return wrapped

# [ @limiter ] is a decorator that uses your limiter object to apply rate-limiting rules to a Flask route.
# [ limit() ] is a method of the limiter object. It is used to set a request limit for a specific Flask route.
# Allow this route to be requested only 10 times per minute per client.
@admin_bp.get('/login')
@limiter.limit("10 per minute")   # test_3_F   # refresh
def admin_login():
    module = 'login'

    # name_list = [1,2]
    # print(name_list[3])

    session.clear()
    return render_template('admin/login.html')

@admin_bp.post('/login')
@limiter.limit("5 per minute")   # test_3_F   # login
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

            # activity log — LOGIN
            log_activity(
                action="LOGIN",
                module="Auth",
                table_name="user",
                record_id=user[0],
                description=f"User '{user[2]}' logged in successfully",
                user_id=user[0],
                username=user[2],
            )
            return redirect(url_for('admin_bp.dashboard'))
        else:
            # Wrong password
            return redirect(url_for('admin_bp.admin_login'))
    else:
        return redirect(url_for('admin_bp.admin_login'))

    # return redirect(url_for('dashboard'))

@admin_bp.get('/logout')
def admin_logout():
    # activity log — LOGOUT (before session.clear)
    if session.get("is_login"):
        log_activity(
            action="LOGOUT",
            module="Auth",
            table_name="user",
            record_id=session.get("user_id"),
            description=f"User '{session.get('username')}' logged out",
        )
    session.clear()
    return redirect(url_for('admin_bp.admin_login'))
