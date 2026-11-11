from extensions import db
from models.base import utcnow


class ErrorLog(db.Model):
    __tablename__ = "error_log"

    id = db.Column(db.BigInteger, primary_key=True)
    user_id = db.Column(db.BigInteger)
    status_code = db.Column(db.Integer, nullable=False)
    path = db.Column(db.String(500), nullable=False)
    method = db.Column(db.String(10), nullable=False)
    ip = db.Column(db.String(64), nullable=False)
    message = db.Column(db.Text)
    created_at = db.Column(db.DateTime, default=utcnow)


class ActivityLog(db.Model):
    __tablename__ = "activity_logs"

    id = db.Column(db.BigInteger, primary_key=True)
    user_id = db.Column(db.BigInteger)
    action = db.Column(db.String(50), nullable=False)
    module = db.Column(db.String(80))
    table_name = db.Column(db.String(80))
    record_id = db.Column(db.BigInteger)
    description = db.Column(db.Text)
    old_data = db.Column(db.JSON)
    new_data = db.Column(db.JSON)
    ip = db.Column(db.String(64))
    created_at = db.Column(db.DateTime, default=utcnow)
