from flask import Blueprint

from auth_utils import role_required

admin_bp = Blueprint("admin", __name__, url_prefix="/admin")


@admin_bp.before_request
def admin_guard():
    """auth + role:ADMIN (admins are not subject to the browsing refresh limit)."""
    return role_required("ADMIN")(lambda: None)()


from blueprints.admin import routes  # noqa: E402,F401
