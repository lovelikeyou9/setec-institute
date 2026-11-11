
from front import front_bp
from flask import render_template, request, make_response, jsonify, redirect, url_for
from catalog import get_product_by_id
import json


def _load_cart():
    cart_list = request.cookies.get("cart_list")
    return json.loads(cart_list) if cart_list else []


def _enrich(cart_list):
    enriched = []
    for item in cart_list:
        product = get_product_by_id(item.get("id"))
        if not product:
            continue
        row = dict(item)
        row["image"] = product.get("image")
        row["title"] = product.get("title")
        row["price"] = float(product.get("price") or 0)
        row["category"] = product.get("category")
        row["brand"] = product.get("brand") or ""
        row["description"] = product.get("description")
        row["qty"] = int(row.get("qty") or 1)
        enriched.append(row)
    return enriched


@front_bp.get("/cart")
def cart():
    product_id = request.args.get("product_id", type=int)
    go_checkout = request.args.get("checkout") == "1"
    cart_list = _load_cart()
    updated = False

    if product_id:
        existing_ids = [int(item["id"]) for item in cart_list]
        if product_id in existing_ids:
            for item in cart_list:
                if int(item["id"]) == product_id:
                    item["qty"] = int(item.get("qty") or 1) + 1
                    break
        else:
            cart_list.append({"id": product_id, "qty": 1})
        updated = True

    cart_list = _enrich(cart_list)
    total = sum(float(i["price"]) * int(i["qty"]) for i in cart_list)

    if go_checkout and product_id:
        response = make_response(redirect(url_for("front_bp.checkout")))
        if updated:
            # store only id/qty in cookie
            slim = [{"id": int(i["id"]), "qty": int(i["qty"])} for i in cart_list]
            response.set_cookie("cart_list", json.dumps(slim))
        return response

    response = make_response(
        render_template("front/cart.html", cart_list=cart_list, total=total)
    )
    if updated:
        slim = [{"id": int(i["id"]), "qty": int(i["qty"])} for i in cart_list]
        response.set_cookie("cart_list", json.dumps(slim))
    return response


@front_bp.post("/update_qty")
def update_qty():
    cart_list = _load_cart()
    item_id = int(request.form["id"])
    action = request.form["action"]
    updated_qty = 0

    for item in cart_list:
        if int(item["id"]) == item_id:
            if action == "increase":
                item["qty"] = int(item.get("qty") or 1) + 1
            elif action == "decrease" and int(item.get("qty") or 1) > 1:
                item["qty"] = int(item.get("qty") or 1) - 1
            elif action == "remove":
                cart_list = [x for x in cart_list if int(x["id"]) != item_id]
                updated_qty = 0
                break
            updated_qty = int(item.get("qty") or 0)
            break

    response = jsonify({"qty": updated_qty, "action": action})
    response.set_cookie("cart_list", json.dumps(cart_list))
    return response


@front_bp.post("/remove_item")
def remove_item():
    cart_list = _load_cart()
    item_id = int(request.form["id"])
    cart_list = [x for x in cart_list if int(x["id"]) != item_id]
    response = jsonify({"success": True})
    response.set_cookie("cart_list", json.dumps(cart_list))
    return response
