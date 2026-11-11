from routes.auth import auth_bp
from routes.main import main_bp
from routes.product import product_bp
from routes.cart import cart_bp
from routes.checkout import checkout_bp
from routes.order import order_bp
from routes.admin import admin_bp

__all__ = [
    "auth_bp",
    "main_bp",
    "product_bp",
    "cart_bp",
    "checkout_bp",
    "order_bp",
    "admin_bp",
]
