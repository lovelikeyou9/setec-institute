"""Ensure order_items.line_status exists (per-item refuse).

Run:
  .venv\\Scripts\\python.exe ensure_order_item_status.py
"""
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from sqlalchemy import text  # noqa: E402

from app import app  # noqa: E402
from extensions import db  # noqa: E402

SQL = "ALTER TABLE order_items ADD COLUMN line_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'"


def ensure_line_status_column():
    try:
        db.session.execute(text(SQL))
        db.session.commit()
        print("OK:", SQL)
    except Exception as exc:
        db.session.rollback()
        msg = str(exc).lower()
        if "duplicate" in msg or "exists" in msg:
            print("skip (exists): line_status")
        else:
            print("WARN:", exc)


if __name__ == "__main__":
    with app.app_context():
        ensure_line_status_column()
