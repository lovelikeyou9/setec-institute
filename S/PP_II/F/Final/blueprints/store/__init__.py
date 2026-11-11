from flask import Blueprint

from extensions import refresh_limit

store_bp = Blueprint("store", __name__)
refresh_limit(store_bp)   # logged-in customers/sellers: 10 page loads per minute

from blueprints.store import storefront, cart, checkout  # noqa: E402,F401
