from flask import Blueprint, flash, g, redirect

from auth_utils import load_current_user, role_required
from extensions import refresh_limit

seller_bp = Blueprint("seller", __name__, url_prefix="/seller-dashboard")
refresh_limit(seller_bp)


@seller_bp.before_request
def seller_guard():
    """auth + role:SELLER,ADMIN, then SellerDashboardController::requireSeller()."""
    denied = role_required("SELLER", "ADMIN")(lambda: None)()
    if denied is not None:
        return denied
    user = load_current_user()
    seller = user.seller
    if not seller or seller.status != "APPROVED":
        if seller and (seller.status or "").upper() in ("SUSPENDED", "BANNED"):
            flash(
                "Your seller store has been BANNED by an administrator. "
                "You can shop as a customer, but selling is disabled.",
                "error",
            )
            return redirect("/account")
        flash("Seller account required. Your store may be pending or banned.", "error")
        return redirect("/become-seller")
    g.seller = seller
    return None


from blueprints.seller import routes  # noqa: E402,F401
