

from api import api_bp
from extensions import db
from sqlalchemy import text

@api_bp.get('/user')
def user():
    module = 'user'
    sql = text("SELECT * FROM user")
    result = db.session.execute(sql)
    rows = [dict(row._mapping) for row in result]
    return rows