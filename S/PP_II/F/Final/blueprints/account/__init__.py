from flask import Blueprint

from extensions import refresh_limit

account_bp = Blueprint("account", __name__)
refresh_limit(account_bp)

from blueprints.account import routes  # noqa: E402,F401
