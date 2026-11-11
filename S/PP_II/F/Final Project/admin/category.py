from admin import admin_bp
from admin.auth import login_required
from flask import render_template, request, redirect, url_for, flash
from extensions import db, limiter, get_user_id
from models.category import Category
from catalog import slugify
from activity_logger import log_activity


@admin_bp.get("/category")
@login_required
@limiter.limit("10 per minute", key_func=get_user_id, scope="admin_panel")
def category():
    module = "category"
    categories = Category.query.order_by(Category.id.asc()).all()
    return render_template("admin/category/list.html", module=module, categories=categories)


@admin_bp.get("/category/add")
@login_required
@limiter.limit("10 per minute", key_func=get_user_id, scope="admin_panel")
def add_category():
    return render_template("admin/category/form.html", module="category", category=None)


@admin_bp.post("/category/add")
@login_required
def do_add_category():
    form = request.form
    name = (form.get("name") or "").strip()
    if not name:
        flash("Category name is required.", "danger")
        return redirect(url_for("admin_bp.add_category"))

    category = Category(
        name=name,
        slug=slugify(form.get("slug") or name),
        description=(form.get("description") or "").strip() or None,
    )
    db.session.add(category)
    db.session.commit()
    log_activity(
        action="INSERT",
        module="Category",
        table_name="category",
        record_id=category.id,
        description=f"Created Category #{category.id}",
        new_data=category.to_dict(),
    )
    return redirect(url_for("admin_bp.category"))


@admin_bp.get("/category/edit/<int:category_id>")
@login_required
@limiter.limit("10 per minute", key_func=get_user_id, scope="admin_panel")
def edit_category(category_id):
    category = Category.query.get_or_404(category_id)
    return render_template("admin/category/form.html", module="category", category=category)


@admin_bp.post("/category/edit")
@login_required
def do_edit_category():
    category = Category.query.get(request.form.get("category_id"))
    if not category:
        return redirect(url_for("admin_bp.category"))
    old = category.to_dict()
    category.name = (request.form.get("name") or "").strip()
    category.slug = slugify(request.form.get("slug") or category.name)
    category.description = (request.form.get("description") or "").strip() or None
    db.session.commit()
    log_activity(
        action="UPDATE",
        module="Category",
        table_name="category",
        record_id=category.id,
        description=f"Updated Category #{category.id}",
        old_data=old,
        new_data=category.to_dict(),
    )
    return redirect(url_for("admin_bp.category"))


@admin_bp.get("/category/delete/<int:category_id>")
@login_required
def delete_category(category_id):
    category = Category.query.get_or_404(category_id)
    old = category.to_dict()
    db.session.delete(category)
    db.session.commit()
    log_activity(
        action="DELETE",
        module="Category",
        table_name="category",
        record_id=category_id,
        description=f"Deleted Category #{category_id}",
        old_data=old,
    )
    return redirect(url_for("admin_bp.category"))
