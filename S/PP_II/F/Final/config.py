"""MMT - Modern Multiple Technology (Flask port of Laravel KhmerStore)."""
import os
from datetime import timedelta

from dotenv import load_dotenv

BASE_DIR = os.path.abspath(os.path.dirname(__file__))
load_dotenv(os.path.join(BASE_DIR, ".env"))


def _bool(name, default="0"):
    return os.getenv(name, default).strip().lower() in ("1", "true", "yes", "on")


class Config:
    BASE_DIR = BASE_DIR

    SECRET_KEY = os.getenv("SECRET_KEY", "mmt-final-pp-ii-secret-change-me")
    TEMPLATES_AUTO_RELOAD = True  # pick up HTML edits without a full restart
    SQLALCHEMY_DATABASE_URI = os.getenv(
        "SQLALCHEMY_DATABASE_URI",
        "mysql+pymysql://root:@127.0.0.1/final_pp_ii_mmt",
    )
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    SQLALCHEMY_ENGINE_OPTIONS = {"pool_pre_ping": True, "pool_recycle": 280}

    PERMANENT_SESSION_LIFETIME = timedelta(minutes=1440)
    MAX_CONTENT_LENGTH = 64 * 1024 * 1024
    WTF_CSRF_TIME_LIMIT = None  # token lives as long as the session

    # Static / uploads
    STATIC_DIR = os.path.join(BASE_DIR, "static")
    UPLOAD_DIR = os.path.join(BASE_DIR, "static", "uploads")

    # Telegram (test_3_F style)
    TELEGRAM_BOT_TOKEN = os.getenv("TELEGRAM_BOT_TOKEN", "")
    TELEGRAM_CHAT_ID = os.getenv("TELEGRAM_CHAT_ID", "")
    TELEGRAM_ENABLED = _bool("TELEGRAM_ENABLED", "1")

    # Stripe
    STRIPE_SECRET_KEY = os.getenv("STRIPE_SECRET_KEY", "")
    STRIPE_PUBLISHABLE_KEY = os.getenv("STRIPE_PUBLISHABLE_KEY", "")
    STRIPE_WEBHOOK_SECRET = os.getenv("STRIPE_WEBHOOK_SECRET", "")

    # Visa (sandbox stub)
    VISA_API_KEY = os.getenv("VISA_API_KEY", "")
    VISA_KEYS_DIR = os.getenv("VISA_KEYS_DIR", r"C:\visa-keys")
    VISA_TEST_MODE = _bool("VISA_TEST_MODE", "1")

    # JWT
    JWT_SECRET_KEY = os.getenv("JWT_SECRET_KEY", "mmt-jwt-secret-pp-ii")
    JWT_EXPIRE_HOURS = int(os.getenv("JWT_EXPIRE_HOURS", "24"))

    # Rate limits
    REFRESH_LIMIT = os.getenv("REFRESH_LIMIT", "10 per minute")
    LOGIN_LIMIT = os.getenv("LOGIN_LIMIT", "5 per minute")

    # Business rules (OrderService.php)
    COMMISSION_PERCENT = 3.0
    BUYER_PROTECTION_DAYS = 3
