import os
from urllib.parse import quote_plus

from dotenv import load_dotenv

load_dotenv()


class Config:
    SECRET_KEY = os.getenv("FLASK_SECRET_KEY", "dev-secret-key")
    _user = os.getenv("MYSQL_USER", os.getenv("DB_USER", "root"))
    _password = quote_plus(os.getenv("MYSQL_PASSWORD", os.getenv("DB_PASSWORD", "")))
    _host = os.getenv("MYSQL_HOST", os.getenv("DB_HOST", "127.0.0.1"))
    _port = os.getenv("MYSQL_PORT", os.getenv("DB_PORT", "3306"))
    _database = os.getenv("MYSQL_DATABASE", os.getenv("DB_NAME", "ec_test_1_f"))
    SQLALCHEMY_DATABASE_URI = (
        f"mysql+pymysql://{_user}:{_password}@{_host}:{_port}/{_database}"
    )
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    SQLALCHEMY_ENGINE_OPTIONS = {"pool_pre_ping": True, "pool_recycle": 280}

    STRIPE_PUBLISHABLE_KEY = os.getenv("STRIPE_PUBLISHABLE_KEY", "")
    STRIPE_SECRET_KEY = os.getenv("STRIPE_SECRET_KEY", "")
    STRIPE_WEBHOOK_SECRET = os.getenv("STRIPE_WEBHOOK_SECRET", "")

    # Visa Developer — X-Pay Token (Credentials → X-Pay Token)
    VISA_API_KEY = os.getenv("VISA_API_KEY", "")
    VISA_SHARED_SECRET = os.getenv("VISA_SHARED_SECRET", "")
    VISA_API_BASE_URL = os.getenv("VISA_API_BASE_URL", "https://sandbox.api.visa.com")
    # demo = Hello World credential check then mark paid; cybersource = live auth API
    VISA_PAYMENT_MODE = os.getenv("VISA_PAYMENT_MODE", "demo")
    VISA_SHARED_SECRET_ENCRYPTED = os.getenv("VISA_SHARED_SECRET_ENCRYPTED", "false").lower() in (
        "1",
        "true",
        "yes",
    )
    VISA_PRIVATE_KEY_PATH = os.getenv(
        "VISA_PRIVATE_KEY_PATH",
        os.path.join(os.path.dirname(__file__), "certs", "private_key.pem"),
    )
    _secret_path = os.getenv(
        "VISA_SHARED_SECRET_PATH",
        os.path.join(os.path.dirname(__file__), "certs", "decrypted_shared_secret.bin"),
    )
    if _secret_path and not os.path.isabs(_secret_path):
        _secret_path = os.path.join(os.path.dirname(__file__), _secret_path)
    VISA_SHARED_SECRET_PATH = _secret_path

    SHIPPING_COST = float(os.getenv("SHIPPING_COST", "5.00"))
    UPLOAD_FOLDER = os.path.join(os.path.dirname(__file__), "static", "uploads")
    MAX_CONTENT_LENGTH = 5 * 1024 * 1024
    WTF_CSRF_ENABLED = True
    WTF_CSRF_TIME_LIMIT = None
