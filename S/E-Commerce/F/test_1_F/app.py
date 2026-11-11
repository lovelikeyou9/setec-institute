import os

from flask import Flask

from config import Config
from extensions import db, login_manager, csrf


def create_app(config_class=Config):
    app = Flask(__name__)
    app.config.from_object(config_class)

    os.makedirs(app.config["UPLOAD_FOLDER"], exist_ok=True)
    os.makedirs(os.path.join(app.root_path, "static", "images", "products"), exist_ok=True)

    db.init_app(app)
    login_manager.init_app(app)
    csrf.init_app(app)

    from models import User  # noqa: F401 — registers user_loader
    from routes import (
        auth_bp,
        main_bp,
        product_bp,
        cart_bp,
        checkout_bp,
        order_bp,
        admin_bp,
    )

    app.register_blueprint(main_bp)
    app.register_blueprint(auth_bp)
    app.register_blueprint(product_bp)
    app.register_blueprint(cart_bp)
    app.register_blueprint(checkout_bp)
    app.register_blueprint(order_bp)
    app.register_blueprint(admin_bp)

    @app.context_processor
    def inject_globals():
        from flask_login import current_user
        from models import Category
        from utils import get_or_create_cart

        cart_count = 0
        if current_user.is_authenticated:
            cart = get_or_create_cart(current_user)
            cart_count = cart.item_count

        categories = Category.query.order_by(Category.name).all()
        return {
            "nav_categories": categories,
            "cart_count": cart_count,
            "stripe_publishable_key": app.config.get("STRIPE_PUBLISHABLE_KEY", ""),
        }

    @app.errorhandler(403)
    def forbidden(e):
        return render_template_safe("errors/403.html"), 403

    @app.errorhandler(404)
    def not_found(e):
        return render_template_safe("errors/404.html"), 404

    @app.cli.command("init-db")
    def init_db_command():
        """Create tables and seed sample data."""
        from seed import seed_database

        db.create_all()
        ensure_order_payment_columns()
        db.session.commit()
        seed_database()
        print("Database initialized and seeded.")

    return app


def ensure_order_payment_columns():
    """Add payment_method / visa_transaction_id if the orders table predates Visa support."""
    from sqlalchemy import inspect, text

    try:
        inspector = inspect(db.engine)
        if "orders" not in inspector.get_table_names():
            return
        cols = {c["name"] for c in inspector.get_columns("orders")}
        statements = []
        if "payment_method" not in cols:
            statements.append(
                "ALTER TABLE orders ADD COLUMN payment_method VARCHAR(30) NOT NULL DEFAULT 'stripe'"
            )
        if "visa_transaction_id" not in cols:
            statements.append(
                "ALTER TABLE orders ADD COLUMN visa_transaction_id VARCHAR(255) NULL"
            )
        if not statements:
            return
        with db.engine.begin() as conn:
            for sql in statements:
                conn.execute(text(sql))
    except Exception as exc:
        # Non-fatal on first boot before MySQL is ready
        print(f"Schema check skipped: {exc}")


def render_template_safe(template_name):
    from flask import render_template

    try:
        return render_template(template_name)
    except Exception:
        return template_name


app = create_app()


if __name__ == "__main__":
    with app.app_context():
        db.create_all()
        ensure_order_payment_columns()
        db.session.commit()
        from seed import seed_database

        seed_database()
    app.run(debug=True, host="127.0.0.1", port=5000)
