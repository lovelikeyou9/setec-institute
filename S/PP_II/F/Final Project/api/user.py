
from flask import jsonify
from api import api_bp
from extensions import db
from sqlalchemy import text
from jwt_auth import jwt_required


@api_bp.get("/user")
@jwt_required
def user():
    sql = text("SELECT id, username, email, profile, role FROM user")
    result = db.session.execute(sql)
    rows = [dict(row._mapping) for row in result]
    return jsonify(data=rows)
