
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate

from flask_limiter import Limiter
from flask_limiter.util import get_remote_address

db = SQLAlchemy()
migrate = Migrate()

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
    default_limits=["10000 per day", "1100 per minute"]
)
