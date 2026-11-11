
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate

from flask_limiter import Limiter
from flask_limiter.util import get_remote_address
from flask import session

# test_4_F
from flask_wtf.csrf import CSRFProtect

# test_5_F
from flask_cors import CORS

db = SQLAlchemy()
migrate = Migrate()


# test_5_F homework
# Use logged-in user_id as the rate-limit key (ban per user, not only per IP).
# If not logged in, fall back to IP address.
def get_user_id():
    user_id = session.get("user_id")
    if user_id:
        return str(user_id)   # ban by user_id / id
    return get_remote_address()


# test_3_F
# [ Limiter ] used for rate limiting
# It controls how many requests a user/client can send to your server within a certain amount of time.
# Limit each client to 10,000 requests per day and 1,100 requests per minute.
# [ key_func ] tells the Limiter who should be counted separately.
# Use the client's IP address as the identifier.
# [ get_remote_address ] is a function from Flask-Limiter that gets the IP address of the client making the request.
# [ default_limits ] tells Flask-Limiter how many requests are allowed by default.
# Default = automatically applied to your routes, unless you give a route its own limit or exempt it.
limiter = Limiter(
    key_func = get_remote_address,
    default_limits=["10000 per day", "100 per minute"]
)

# test_4_F
csrf = CSRFProtect()

# test_5_F Core / Cors
core = CORS()