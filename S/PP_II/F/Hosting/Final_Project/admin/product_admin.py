from admin import admin_bp
from admin.auth import login_required
from flask import render_template, request, redirect, url_for, flash
from extensions import db, limiter, get_user_id
from models.brand import Brand
from models.category import Category
from models.product_model import Product
from activity_logger import log_activity


@admin_bp.get("/product")
@login_required
@limiter.limit("10 per minute", key_func=get_user_id, scope="admin_panel")
def product_list():
    module = "product"
    products = Product.query.order_by(Product.id.desc()).all()
    return render_template("admin/product/list.html", module=module, products=products)


@admin_bp.get("/product/add")
@login_required
@limiter.limit("10 per minute", key_func=get_user_id, scope="admin_panel")
def add_product():
    return render_template(
        "admin/product/form.html",
        module="product",
        product=None,
        brands=Brand.query.order_by(Brand.name).all(),
        categories=Category.query.order_by(Category.name).all(),
    )


@admin_bp.post("/product/add")
@login_required
def do_add_product():
    form = request.form
    title = (form.get("title") or "").strip()
    if not title:
        flash("Product title is required.", "danger")
        return redirect(url_for("admin_bp.add_product"))

    product = Product(
        title=title,
        price=float(form.get("price") or 0),
        description=(form.get("description") or "").strip(),
        image=(form.get("image") or "").strip(),
        rating_rate=float(form.get("rating_rate") or 0),
        rating_count=int(form.get("rating_count") or 0),
        is_active=form.get("is_active") == "1",
        brand_id=int(form["brand_id"]) if form.get("brand_id") else None,
        category_id=int(form["category_id"]) if form.get("category_id") else None,
    )
    db.session.add(product)
    db.session.commit()
    log_activity(
        action="INSERT",
        module="Product",
        table_name="product",
        record_id=product.id,
        description=f"Created Product #{product.id}",
        new_data=product.to_dict(),
    )
    return redirect(url_for("admin_bp.product_list"))


@admin_bp.get("/product/edit/<int:product_id>")
@login_required
@limiter.limit("10 per minute", key_func=get_user_id, scope="admin_panel")
def edit_product(product_id):
    product = Product.query.get_or_404(product_id)
    return render_template(
        "admin/product/form.html",
        module="product",
        product=product,
        brands=Brand.query.order_by(Brand.name).all(),
        categories=Category.query.order_by(Category.name).all(),
    )


@admin_bp.post("/product/edit")
@login_required
def do_edit_product():
    product = Product.query.get(request.form.get("product_id"))
    if not product:
        return redirect(url_for("admin_bp.product_list"))
    old = product.to_dict()
    form = request.form
    product.title = (form.get("title") or "").strip()
    product.price = float(form.get("price") or 0)
    product.description = (form.get("description") or "").strip()
    product.image = (form.get("image") or "").strip()
    product.rating_rate = float(form.get("rating_rate") or 0)
    product.rating_count = int(form.get("rating_count") or 0)
    product.is_active = form.get("is_active") == "1"
    product.brand_id = int(form["brand_id"]) if form.get("brand_id") else None
    product.category_id = int(form["category_id"]) if form.get("category_id") else None
    db.session.commit()
    log_activity(
        action="UPDATE",
        module="Product",
        table_name="product",
        record_id=product.id,
        description=f"Updated Product #{product.id}",
        old_data=old,
        new_data=product.to_dict(),
    )
    return redirect(url_for("admin_bp.product_list"))


@admin_bp.get("/product/delete/<int:product_id>")
@login_required
def delete_product(product_id):
    product = Product.query.get_or_404(product_id)
    old = product.to_dict()
    db.session.delete(product)
    db.session.commit()
    log_activity(
        action="DELETE",
        module="Product",
        table_name="product",
        record_id=product_id,
        description=f"Deleted Product #{product_id}",
        old_data=old,
    )
    return redirect(url_for("admin_bp.product_list"))
