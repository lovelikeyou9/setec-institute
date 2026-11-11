
from front import front_bp
from flask import render_template
from catalog import products_as_dicts


@front_bp.get("/")
@front_bp.get("/home")
def home():
    return render_template("front/index.html", products=products_as_dicts())
