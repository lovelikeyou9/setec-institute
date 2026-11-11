
# [ request ] used to get data from the URL (GET request)
# example: http://127.0.0.1:5000/cart?product_id=1

# make_response() is used to create and control the HTTP response before sending it back to the browser.
# render_template() is used to show an HTML file (page) in Flask and send data to it.
from flask import Flask

from config import Config

# json is converts STRING → Python object
import json

from extensions import db, migrate

from front import front_bp
from admin import admin_bp
from api import api_bp

app = Flask(__name__) # when it is running. it start from [ if __name__ = '__main__' ]
app.config.from_object(Config)

db.init_app(app)
migrate.init_app(app, db)

# load model
import models

app.register_blueprint(front_bp, url_prefix="/")
app.register_blueprint(admin_bp, url_prefix="/admin")
app.register_blueprint(api_bp, url_prefix="/api")



if __name__ == '__main__':
    app.run()
