import os
import uuid

from flask import (
    Blueprint,
    current_app,
    flash,
    redirect,
    render_template,
    request,
    url_for,
)
from sqlalchemy import func
from werkzeug.utils import secure_filename

from extensions import db
from forms import ProductForm, CategoryForm, OrderStatusForm
from models import User, Product, Category, Order
from utils import flash_form_errors

admin_bp = Blueprint("admin", __name__, url_prefix="/admin")


@admin_bp.before_request
def protect_admin():
    from flask_login import current_user
    from flask import abort, redirect, url_for

    if not current_user.is_authenticated:
        return redirect(url_for("auth.login", next=request.path))
    if not current_user.is_admin:
        abort(403)


@admin_bp.route("/")
def dashboard():
    total_products = Product.query.count()
    total_customers = User.query.filter_by(role="customer").count()
    total_orders = Order.query.count()
    pending_orders = Order.query.filter_by(status="Pending").count()
    paid_orders = Order.query.filter(Order.payment_status == "Paid").count()
    total_sales = (
        db.session.query(func.coalesce(func.sum(Order.total_amount), 0))
        .filter(Order.payment_status == "Paid")
        .scalar()
    )
    recent_orders = Order.query.order_by(Order.created_at.desc()).limit(8).all()

    return render_template(
        "admin/dashboard.html",
        total_products=total_products,
        total_customers=total_customers,
        total_orders=total_orders,
        pending_orders=pending_orders,
        paid_orders=paid_orders,
        total_sales=float(total_sales or 0),
        recent_orders=recent_orders,
    )


@admin_bp.route("/products")
def products():
    products_list = Product.query.order_by(Product.created_at.desc()).all()
    return render_template("admin/products.html", products=products_list)


@admin_bp.route("/products/create", methods=["GET", "POST"])
def product_create():
    form = ProductForm()
    form.category_id.choices = [(c.id, c.name) for c in Category.query.order_by(Category.name)]
    if form.validate_on_submit():
        product = Product(
            name=form.name.data.strip(),
            brand=form.brand.data.strip(),
            description=form.description.data,
            specifications=form.specifications.data,
            price=form.price.data,
            discount_price=form.discount_price.data,
            stock_quantity=form.stock_quantity.data,
            category_id=form.category_id.data,
            rating=float(form.rating.data or 4.5),
            is_featured=form.is_featured.data,
        )
        if form.image.data:
            product.image = _save_image(form.image.data)
        db.session.add(product)
        db.session.commit()
        flash("Product created.", "success")
        return redirect(url_for("admin.products"))
    if form.is_submitted():
        flash_form_errors(form)
    return render_template("admin/product_form.html", form=form, title="Add Product")


@admin_bp.route("/products/update/<int:product_id>", methods=["GET", "POST"])
def product_update(product_id):
    product = Product.query.get_or_404(product_id)
    form = ProductForm(obj=product)
    form.category_id.choices = [(c.id, c.name) for c in Category.query.order_by(Category.name)]
    if request.method == "GET":
        form.category_id.data = product.category_id
        form.is_featured.data = product.is_featured

    if form.validate_on_submit():
        product.name = form.name.data.strip()
        product.brand = form.brand.data.strip()
        product.description = form.description.data
        product.specifications = form.specifications.data
        product.price = form.price.data
        product.discount_price = form.discount_price.data
        product.stock_quantity = form.stock_quantity.data
        product.category_id = form.category_id.data
        product.rating = float(form.rating.data or product.rating or 4.5)
        product.is_featured = form.is_featured.data
        if form.image.data:
            product.image = _save_image(form.image.data)
        db.session.commit()
        flash("Product updated.", "success")
        return redirect(url_for("admin.products"))
    if form.is_submitted():
        flash_form_errors(form)
    return render_template(
        "admin/product_form.html", form=form, title="Edit Product", product=product
    )


@admin_bp.route("/products/delete/<int:product_id>", methods=["POST"])
def product_delete(product_id):
    product = Product.query.get_or_404(product_id)
    db.session.delete(product)
    db.session.commit()
    flash("Product deleted.", "info")
    return redirect(url_for("admin.products"))


@admin_bp.route("/categories", methods=["GET", "POST"])
def categories():
    form = CategoryForm()
    if form.validate_on_submit():
        name = form.name.data.strip()
        slug = name.lower().replace(" ", "-")
        if Category.query.filter_by(name=name).first():
            flash("Category already exists.", "danger")
        else:
            db.session.add(
                Category(
                    name=name,
                    description=form.description.data,
                    slug=slug,
                    icon=form.icon.data or "bi-cpu",
                )
            )
            db.session.commit()
            flash("Category created.", "success")
            return redirect(url_for("admin.categories"))
    cats = Category.query.order_by(Category.name).all()
    return render_template("admin/categories.html", form=form, categories=cats)


@admin_bp.route("/categories/update/<int:category_id>", methods=["GET", "POST"])
def category_update(category_id):
    category = Category.query.get_or_404(category_id)
    form = CategoryForm(obj=category)
    if form.validate_on_submit():
        category.name = form.name.data.strip()
        category.description = form.description.data
        category.icon = form.icon.data or category.icon
        category.slug = category.name.lower().replace(" ", "-")
        db.session.commit()
        flash("Category updated.", "success")
        return redirect(url_for("admin.categories"))
    return render_template("admin/category_form.html", form=form, category=category)


@admin_bp.route("/categories/delete/<int:category_id>", methods=["POST"])
def category_delete(category_id):
    category = Category.query.get_or_404(category_id)
    if category.products.count() > 0:
        flash("Cannot delete a category that still has products.", "danger")
        return redirect(url_for("admin.categories"))
    db.session.delete(category)
    db.session.commit()
    flash("Category deleted.", "info")
    return redirect(url_for("admin.categories"))


@admin_bp.route("/orders")
def orders():
    status = request.args.get("status")
    query = Order.query
    if status:
        query = query.filter_by(status=status)
    orders_list = query.order_by(Order.created_at.desc()).all()
    return render_template("admin/orders.html", orders=orders_list, status=status)


@admin_bp.route("/orders/<int:order_id>", methods=["GET", "POST"])
def order_detail(order_id):
    order = Order.query.get_or_404(order_id)
    form = OrderStatusForm(status=order.status)
    if form.validate_on_submit():
        order.status = form.status.data
        db.session.commit()
        flash("Order status updated.", "success")
        return redirect(url_for("admin.order_detail", order_id=order.id))
    return render_template("admin/order_detail.html", order=order, form=form)


@admin_bp.route("/customers")
def customers():
    customers_list = User.query.filter_by(role="customer").order_by(User.created_at.desc()).all()
    return render_template("admin/customers.html", customers=customers_list)


@admin_bp.route("/customers/<int:user_id>/toggle", methods=["POST"])
def customer_toggle(user_id):
    user = User.query.get_or_404(user_id)
    if user.role == "admin":
        flash("Cannot disable an admin account here.", "danger")
        return redirect(url_for("admin.customers"))
    user.is_active = not user.is_active
    db.session.commit()
    state = "enabled" if user.is_active else "disabled"
    flash(f"Account {user.email} {state}.", "success")
    return redirect(url_for("admin.customers"))


def _save_image(file_storage):
    filename = secure_filename(file_storage.filename)
    ext = filename.rsplit(".", 1)[-1].lower() if "." in filename else "png"
    new_name = f"{uuid.uuid4().hex}.{ext}"
    upload_dir = current_app.config["UPLOAD_FOLDER"]
    os.makedirs(upload_dir, exist_ok=True)
    path = os.path.join(upload_dir, new_name)
    file_storage.save(path)
    # Store relative path under static so templates can serve uploads
    # Product.image_url expects products/ folder OR we put uploads path
    return f"../uploads/{new_name}"
