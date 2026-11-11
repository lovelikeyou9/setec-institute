
import json
from admin import admin_bp
from flask import render_template, request, make_response, redirect, url_for, jsonify, session
# from .auth import login_required
from admin.auth import login_required
from sqlalchemy import text
from extensions import db, limiter, get_user_id
from werkzeug.security import generate_password_hash
from werkzeug.utils import secure_filename
from models.user import User
import os

from helper import ALLOWED_EXT, UPLOAD_DIR

from upload_service import save_user_image
from activity_logger import log_activity


# After login: refresh admin user page more than 10 times / minute -> 429 ban (by user_id)
@admin_bp.get('/user')
@login_required
@limiter.limit("10 per minute", key_func=get_user_id, scope="admin_panel") # test_5_F
def user():
    module = 'user'
    sql = text("SELECT * FROM user")
    result = db.session.execute(sql)
    rows = [dict(row._mapping) for row in result]
    return render_template('admin/user/user.html', module=module, users=rows)

@admin_bp.get('/user/add')
@login_required
@limiter.limit("10 per minute", key_func=get_user_id, scope="admin_panel") # test_5_F
def add_user():
    module = 'user'
    return render_template('admin/user/add.html', module=module)

@admin_bp.post('/user/add')
@login_required
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

    # activity log — INSERT user
    log_activity(
        action="INSERT",
        module="User",
        table_name="user",
        record_id=user.id,
        description=f"Created User #{user.id}",
        new_data={
            "id": user.id,
            "username": user.username,
            "email": user.email,
            "role": user.role,
            "profile": user.profile,
        },
    )

    return redirect(url_for('admin_bp.user'))

@admin_bp.get('/user/edit/<int:user_id>')
@login_required
@limiter.limit("10 per minute", key_func=get_user_id, scope="admin_panel") # test_5_F
def edit_user(user_id):
    module = 'user'
    sql = text("SELECT * FROM user WHERE id = :user_id")
    result = db.session.execute(sql, {"user_id": user_id}).fetchone()
    user = None
    if result:
        user = dict(result._mapping)
    else:
        return redirect(url_for('admin_bp.user'))
    return render_template('admin/user/edit.html', module=module, user=user)

@admin_bp.post('/user/edit')
@login_required
def do_edit_user():
    module = 'user'
    form = request.form
    user = User.query.get(form.get('user_id'))
    if not user:
        return redirect(url_for('admin_bp.user'))

    old_data = {
        "id": user.id,
        "username": user.username,
        "email": user.email,
        "role": user.role,
        "profile": user.profile,
    }

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

    # Keep navbar image in sync if editing the logged-in user
    if session.get('user_id') == user.id:
        session['profile'] = user.profile
        session['username'] = user.username
        session['email'] = user.email

    # activity log — UPDATE user
    log_activity(
        action="UPDATE",
        module="User",
        table_name="user",
        record_id=user.id,
        description=f"Updated User #{user.id}",
        old_data=old_data,
        new_data={
            "id": user.id,
            "username": user.username,
            "email": user.email,
            "role": user.role,
            "profile": user.profile,
        },
    )

    return redirect(url_for('admin_bp.user'))

@admin_bp.get('/user/confirm-delete/<int:user_id>')
@login_required
@limiter.limit("10 per minute", key_func=get_user_id, scope="admin_panel") # test_5_F
def confirm_delete(user_id):
    module = 'user'
    sql = text("SELECT * FROM user WHERE id = :user_id")
    result = db.session.execute(sql, {"user_id": user_id}).fetchone()
    user = None
    if result:
        user = dict(result._mapping)
    else:
        return redirect(url_for('admin_bp.user'))
    return render_template('admin/user/confirm_delete.html', module=module, user=user)

@admin_bp.post('/user/delete')
@login_required
def delete_user():
    module = 'user'
    form = request.form
    user_id = form.get('user_id')
    # assert False, user_id
    user = User.query.get(user_id)
    if not user:
        return redirect(url_for('admin_bp.user'))

    old_data = {
        "id": user.id,
        "username": user.username,
        "email": user.email,
        "role": user.role,
        "profile": user.profile,
    }
    deleted_id = user.id

    db.session.delete(user)
    db.session.commit()

    # activity log — DELETE user
    log_activity(
        action="DELETE",
        module="User",
        table_name="user",
        record_id=deleted_id,
        description=f"Deleted User #{deleted_id}",
        old_data=old_data,
    )

    # assert False, form
    return redirect(url_for('admin_bp.user'))

@admin_bp.post("/remove_item")
@login_required
def remove_item():
    cart_list = request.cookies.get('cart_list')
    cart_list = json.loads(cart_list) if cart_list else []

    item_id = int(request.form["id"])

    # remove item
    cart_list = [item for item in cart_list if item["id"] != item_id]

    response = jsonify({"success": True})
    response.set_cookie('cart_list', json.dumps(cart_list))

    return response

