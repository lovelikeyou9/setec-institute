"""Create / reset demo accounts with known passwords.

    .venv\\Scripts\\python.exe seed_demo_users.py
    .venv\\Scripts\\python.exe seed_demo_users.py --all   # reset EVERY user password by role

Passwords by role (site login):
    ADMIN    → Admin123!
    CUSTOMER → Customer123!
    SELLER   → Seller123!
"""
import sys

from app import app
from auth_utils import hash_password
from extensions import db
from models import Seller, User
from models.base import utcnow

ROLE_PASSWORDS = {
    "ADMIN": "Admin123!",
    "CUSTOMER": "Customer123!",
    "SELLER": "Seller123!",
}

DEMO = [
    ("admin@mmt.local", "Admin123!", "ADMIN", "MMT Admin", "+855 10 000 001"),
    ("customer@mmt.local", "Customer123!", "CUSTOMER", "Demo Customer", "+855 10 000 002"),
    ("seller@mmt.local", "Seller123!", "SELLER", "Demo Seller", "+855 10 000 003"),
]

# Laravel sample accounts — always reset with the main seed
LARAVEL = [
    ("admin@computerhub.com", "Admin123!", "ADMIN"),
    ("customer@computerhub.com", "Customer123!", "CUSTOMER"),
    ("techzone@computerhub.com", "Seller123!", "SELLER"),
]


def upsert_user(email, password, role, name, phone):
    user = User.query.filter(db.func.lower(User.email) == email.lower()).first()
    now = utcnow()
    if user is None:
        user = User(email=email, created_at=now)
        db.session.add(user)
    user.full_name, user.phone, user.role = name, phone, role
    user.password = hash_password(password)
    user.enabled = True
    user.updated_at = now
    db.session.flush()
    return user


def ensure_demo_seller(user, name, phone):
    seller = Seller.query.filter_by(user_id=user.id).first()
    now = utcnow()
    if seller is None:
        seller = Seller(
            user_id=user.id, created_at=now, joined_at=now, rating=0, followers=0,
            response_rate=100, total_products=0, total_sales=0,
        )
        db.session.add(seller)
    seller.store_name = seller.store_name or "MMT Demo Store"
    if seller.store_name == "MMT Demo Store" or user.email == "seller@mmt.local":
        seller.store_name = "MMT Demo Store"
        seller.owner_name = name
        seller.phone = phone
        seller.location = seller.location or "Phnom Penh"
        seller.country = seller.country or "Cambodia"
        seller.address = seller.address or "Street 123, Phnom Penh"
        seller.description = seller.description or "Demo seller store created by seed_demo_users.py"
        seller.level = seller.level or "VERIFIED"
    seller.status = "APPROVED"
    seller.owner_name = seller.owner_name or name
    seller.phone = seller.phone or phone


def main():
    reset_all = "--all" in sys.argv or "--reset-laravel" in sys.argv or True  # always fix site logins
    with app.app_context():
        print("=== Core demo accounts ===")
        for email, password, role, name, phone in DEMO:
            user = upsert_user(email, password, role, name, phone)
            if role == "SELLER":
                ensure_demo_seller(user, name, phone)
            print(f"  ok  {email:<32} {password:<14} {role}")

        print("=== Laravel sample accounts ===")
        for email, password, role in LARAVEL:
            user = User.query.filter(db.func.lower(User.email) == email.lower()).first()
            if user:
                user.password = hash_password(password)
                user.enabled = True
                user.role = role or user.role
                user.updated_at = utcnow()
                if (user.role or "").upper() == "SELLER" and user.seller:
                    user.seller.status = "APPROVED"
                print(f"  ok  {email:<32} {password:<14} {user.role}")
            else:
                print(f"  (missing) {email}")

        if reset_all:
            print("=== All other users (password by role) ===")
            known = {e.lower() for e, *_ in DEMO} | {e.lower() for e, *_ in LARAVEL}
            for user in User.query.order_by(User.id).all():
                if (user.email or "").lower() in known:
                    continue
                role = (user.role or "CUSTOMER").upper()
                password = ROLE_PASSWORDS.get(role, "Customer123!")
                user.role = role
                user.password = hash_password(password)
                user.enabled = True
                user.updated_at = utcnow()
                if role == "SELLER" and user.seller and user.seller.status in ("SUSPENDED", "BANNED", "REJECTED", "PENDING"):
                    # leave status as-is for suspended; only ensure approved sellers stay usable
                    pass
                elif role == "SELLER" and user.seller and not user.seller.status:
                    user.seller.status = "APPROVED"
                print(f"  ok  {user.email:<32} {password:<14} {role}")

        db.session.commit()
        print()
        print("Done. Login with:")
        print("  ADMIN    → Admin123!")
        print("  CUSTOMER → Customer123!")
        print("  SELLER   → Seller123!")


if __name__ == "__main__":
    main()
