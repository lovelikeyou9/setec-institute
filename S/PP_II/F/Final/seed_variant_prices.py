"""Backfill products.option_groups with per-choice prices + cart_items columns.

Run:
  .venv\\Scripts\\python.exe seed_variant_prices.py
"""
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from sqlalchemy import text  # noqa: E402

from app import app  # noqa: E402
from extensions import db  # noqa: E402
from models import Product  # noqa: E402
from services.product_variants import ensure_priced_groups  # noqa: E402


DDL = [
    "ALTER TABLE cart_items ADD COLUMN selected_options VARCHAR(255) NULL",
    "ALTER TABLE cart_items ADD COLUMN unit_price DECIMAL(12,2) NULL",
]


def ensure_cart_columns():
    for sql in DDL:
        try:
            db.session.execute(text(sql))
            db.session.commit()
            print("OK:", sql)
        except Exception as exc:
            db.session.rollback()
            msg = str(exc).lower()
            if "duplicate" in msg or "exists" in msg:
                print("skip (exists):", sql.split("ADD COLUMN")[-1].strip())
            else:
                print("WARN:", sql, "->", exc)


def backfill_products():
    updated = 0
    for product in Product.query.all():
        groups = ensure_priced_groups(product)
        has_prices = any(g.get("prices") for g in groups)
        if not has_prices:
            continue
        payload = json.dumps(groups, ensure_ascii=False)
        if (product.option_groups or "").strip() == payload:
            continue
        product.option_groups = payload
        updated += 1
        print(f"  #{product.id} {product.name[:50]} -> priced options")
    db.session.commit()
    print(f"Updated {updated} products")


if __name__ == "__main__":
    with app.app_context():
        ensure_cart_columns()
        backfill_products()
