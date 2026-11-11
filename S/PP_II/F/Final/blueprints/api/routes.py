"""JWT-protected JSON API (mounted at /api, CSRF-exempt).

POST /api/login            -> access_token
GET  /api/health           -> public
GET  /api/me               -> JWT
GET  /api/products         -> public (JWT optional), ?q=&category=&brand=&page=&per_page=
GET  /api/products/<id>    -> public
GET  /api/categories       -> public
GET  /api/cart             -> JWT
GET  /api/orders           -> JWT (own orders; admin sees all)
"""
from flask import current_app, g, jsonify, request
from sqlalchemy.orm import joinedload

from activity_logger import log_activity
from auth_utils import create_access_token, jwt_optional, jwt_required, verify_password
from blueprints.api import api_bp
from extensions import db, limiter
from models import Category, Order, Product, User
from services import cart_service


def _num(v):
    return float(v) if v is not None else None


def user_json(u):
    return {"id": u.id, "email": u.email, "full_name": u.full_name, "phone": u.phone, "role": u.role,
            "enabled": bool(u.enabled), "avatar_url": u.avatar_url,
            "seller": ({"id": u.seller.id, "store_name": u.seller.store_name, "status": u.seller.status}
                       if u.seller else None)}


def product_json(p):
    return {"id": p.id, "name": p.name, "slug": p.slug, "price": _num(p.price),
            "original_price": _num(p.original_price), "discount_percent": p.discount_percent,
            "stock": p.stock, "rating": p.rating, "review_count": p.review_count, "sold_count": p.sold_count,
            "image_url": p.image_url, "brand": p.brand.name if p.brand else None,
            "category": p.category.name if p.category else None,
            "seller": p.seller.store_name if p.seller else None}


def order_json(o):
    return {"id": o.id, "order_number": o.order_number, "status": o.status, "payment_method": o.payment_method,
            "total": _num(o.total), "subtotal": _num(o.subtotal), "shipping_fee": _num(o.shipping_fee),
            "placed_at": o.placed_at.isoformat() if o.placed_at else None,
            "items": [{"product_id": i.product_id, "name": i.product_name, "quantity": i.quantity,
                       "unit_price": _num(i.unit_price)} for i in o.items]}


@api_bp.get("/health")
def health():
    try:
        db.session.execute(db.text("SELECT 1"))
        db_ok = True
    except Exception:
        db_ok = False
    return jsonify(status="ok" if db_ok else "degraded", app="MMT", database=db_ok)


@api_bp.post("/login")
@limiter.limit(lambda: current_app.config.get("LOGIN_LIMIT", "5 per minute"))
def login():
    body = request.get_json(silent=True) or request.form
    email = (body.get("email") or "").strip().lower()
    password = body.get("password") or ""
    if not email or not password:
        return jsonify(error="validation", message="email and password are required"), 422
    user = User.query.filter(db.func.lower(User.email) == email).first()
    if not user or not verify_password(user.password, password):
        log_activity("LOGIN_FAILED", "API", f"API login failed for {email}")
        return jsonify(error="invalid_credentials", message="Invalid email or password"), 401
    if not user.enabled:
        return jsonify(error="forbidden", message="Account is disabled"), 403
    token = create_access_token(user)
    log_activity("LOGIN", "API", f"API login {user.email}", "users", user.id, user_id=user.id)
    return jsonify(access_token=token, token_type="Bearer",
                   expires_in=current_app.config["JWT_EXPIRE_HOURS"] * 3600, user=user_json(user))


@api_bp.get("/me")
@jwt_required
def me():
    return jsonify(user=user_json(g.api_user))


@api_bp.get("/products")
@jwt_optional
def products():
    a = request.args
    q = (Product.query.options(joinedload(Product.brand), joinedload(Product.category), joinedload(Product.seller))
         .filter(Product.active == 1))
    if a.get("q"):
        q = q.filter(Product.name.like(f"%{a.get('q')}%"))
    if a.get("category"):
        q = q.filter(Product.category_id == a.get("category", type=int))
    if a.get("brand"):
        q = q.filter(Product.brand_id == a.get("brand", type=int))
    page = max(1, a.get("page", 1, type=int))
    per_page = min(100, max(1, a.get("per_page", 20, type=int)))
    total = q.order_by(None).count()
    rows = q.order_by(Product.id.desc()).limit(per_page).offset((page - 1) * per_page).all()
    return jsonify(page=page, per_page=per_page, total=total, authenticated=g.api_user is not None,
                   data=[product_json(p) for p in rows])


@api_bp.get("/products/<int:id>")
@jwt_optional
def product(id):
    p = Product.query.options(joinedload(Product.brand), joinedload(Product.category),
                              joinedload(Product.seller)).filter_by(id=id, active=1).first()
    if not p:
        return jsonify(error="not_found", message="Product not found"), 404
    return jsonify(data=product_json(p))


@api_bp.get("/categories")
def categories():
    rows = Category.query.filter(Category.active == 1).order_by(Category.sort_order).all()
    return jsonify(data=[{"id": c.id, "name": c.name, "slug": c.slug} for c in rows])


@api_bp.get("/cart")
@jwt_required
def cart():
    items = cart_service.get_cart(g.api_user.id)
    return jsonify(subtotal=float(cart_service.selected_subtotal(g.api_user.id)),
                   data=[{"id": i.id, "product_id": i.product_id, "name": i.product.name,
                          "quantity": i.quantity, "selected": bool(i.selected),
                          "price": _num(i.product.price)} for i in items])


@api_bp.get("/orders")
@jwt_required
def orders():
    q = Order.query.options(joinedload(Order.items)).order_by(Order.placed_at.desc())
    if g.api_user.role != "ADMIN":
        q = q.filter(Order.user_id == g.api_user.id)
    return jsonify(data=[order_json(o) for o in q.limit(100).all()])


@api_bp.errorhandler(404)
def api_404(_e):
    return jsonify(error="not_found", message="Endpoint not found"), 404
