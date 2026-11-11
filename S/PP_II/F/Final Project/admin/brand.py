from admin import admin_bp
from admin.auth import login_required
from flask import render_template, request, redirect, url_for, flash
from extensions import db, limiter, get_user_id
from models.brand import Brand
from catalog import slugify
from activity_logger import log_activity


@admin_bp.get("/brand")
@login_required
@limiter.limit("10 per minute", key_func=get_user_id, scope="admin_panel")
def brand():
    module = "brand"
    brands = Brand.query.order_by(Brand.id.asc()).all()
    return render_template("admin/brand/list.html", module=module, brands=brands)


@admin_bp.get("/brand/add")
@login_required
@limiter.limit("10 per minute", key_func=get_user_id, scope="admin_panel")
def add_brand():
    return render_template("admin/brand/form.html", module="brand", brand=None)


@admin_bp.post("/brand/add")
@login_required
def do_add_brand():
    form = request.form
    name = (form.get("name") or "").strip()
    if not name:
        flash("Brand name is required.", "danger")
        return redirect(url_for("admin_bp.add_brand"))

    brand = Brand(
        name=name,
        slug=slugify(form.get("slug") or name),
        logo=(form.get("logo") or "").strip() or None,
        description=(form.get("description") or "").strip() or None,
    )
    db.session.add(brand)
    db.session.commit()
    log_activity(
        action="INSERT",
        module="Brand",
        table_name="brand",
        record_id=brand.id,
        description=f"Created Brand #{brand.id}",
        new_data=brand.to_dict(),
    )
    return redirect(url_for("admin_bp.brand"))


@admin_bp.get("/brand/edit/<int:brand_id>")
@login_required
@limiter.limit("10 per minute", key_func=get_user_id, scope="admin_panel")
def edit_brand(brand_id):
    brand = Brand.query.get_or_404(brand_id)
    return render_template("admin/brand/form.html", module="brand", brand=brand)


@admin_bp.post("/brand/edit")
@login_required
def do_edit_brand():
    brand = Brand.query.get(request.form.get("brand_id"))
    if not brand:
        return redirect(url_for("admin_bp.brand"))
    old = brand.to_dict()
    brand.name = (request.form.get("name") or "").strip()
    brand.slug = slugify(request.form.get("slug") or brand.name)
    brand.logo = (request.form.get("logo") or "").strip() or None
    brand.description = (request.form.get("description") or "").strip() or None
    db.session.commit()
    log_activity(
        action="UPDATE",
        module="Brand",
        table_name="brand",
        record_id=brand.id,
        description=f"Updated Brand #{brand.id}",
        old_data=old,
        new_data=brand.to_dict(),
    )
    return redirect(url_for("admin_bp.brand"))


@admin_bp.get("/brand/delete/<int:brand_id>")
@login_required
def delete_brand(brand_id):
    brand = Brand.query.get_or_404(brand_id)
    old = brand.to_dict()
    db.session.delete(brand)
    db.session.commit()
    log_activity(
        action="DELETE",
        module="Brand",
        table_name="brand",
        record_id=brand_id,
        description=f"Deleted Brand #{brand_id}",
        old_data=old,
    )
    return redirect(url_for("admin_bp.brand"))
