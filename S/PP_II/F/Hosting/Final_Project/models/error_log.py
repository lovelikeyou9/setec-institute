
# test_5_F
from extensions import db
from datetime import datetime


class ErrorLog(db.Model):
    __tablename__ = "error_log"

    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, nullable=True)       # who got banned (session user_id)
    status_code = db.Column(db.Integer, nullable=False)  # e.g. 429
    path = db.Column(db.String(255), nullable=True)      # URL path
    method = db.Column(db.String(10), nullable=True)     # GET / POST
    ip = db.Column(db.String(50), nullable=True)
    message = db.Column(db.Text, nullable=True)
    created_at = db.Column(db.DateTime, default=datetime.utcnow, nullable=False)
