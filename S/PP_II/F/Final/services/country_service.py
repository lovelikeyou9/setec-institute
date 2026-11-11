"""Port of App\\Support\\CountryResolver + App\\Services\\AdminCountryService."""
from datetime import datetime
from urllib.parse import unquote

from sqlalchemy.orm import joinedload

from extensions import db
from models import Address, Commission, Order, OrderItem, Product, Seller, User, WishlistItem
from models.base import utcnow

OPTIONS = ["Cambodia", "United States", "United Kingdom", "Germany", "Canada",
           "Thailand", "Singapore", "Vietnam", "Japan", "Australia", "China", "Other"]


# ------------------------------------------------------------ CountryResolver
def _contains_any(text, needles):
    return any(n in text for n in needles)


def _normalize_known(country):
    for opt in OPTIONS:
        if opt.lower() == country.lower():
            return opt
    return country


def resolve(address, location=None, explicit=None):
    if explicit is not None and explicit.strip() != "":
        return _normalize_known(explicit.strip())
    blob = f"{address or ''} {location or ''}".strip().lower()
    if blob == "":
        return "Unknown"
    rules = [
        ("United States", ["united states", "usa", "u.s.a", "u.s.", " america"]),
        ("United Kingdom", ["united kingdom", "england", "london", " scotland", " wales"]),
        ("Germany", ["germany", "berlin", "munich"]),
        ("Canada", ["canada", "toronto", "vancouver"]),
        ("Thailand", ["thailand", "bangkok"]),
        ("Singapore", ["singapore"]),
        ("Vietnam", ["vietnam", "ho chi minh", "hanoi"]),
        ("Japan", ["japan", "tokyo", "osaka"]),
        ("Australia", ["australia", "sydney", "melbourne"]),
        ("China", ["china", "beijing", "shanghai"]),
        ("Cambodia", ["cambodia", "phnom penh", "siem reap", "battambang", "sihanoukville",
                      "kampong", "kandal", "takeo", "kampot", "banteay", "prey veng", "svay rieng"]),
    ]
    for name, needles in rules:
        if _contains_any(blob, needles):
            return name
    return "Other"


def resolve_seller(seller):
    if not seller:
        return "Unknown"
    return resolve(seller.address, seller.location, seller.country)


def resolve_buyer(order):
    if not order:
        return "Unknown"
    return resolve(order.shipping_address, None, order.shipping_country)


def resolve_address(addr):
    if not addr:
        return "Unknown"
    return resolve(addr.formatted(), None, addr.country)


def matches_seller(country, seller):
    return country != "" and country.lower() == resolve_seller(seller).lower()


def matches_buyer(country, order):
    return country != "" and country.lower() == resolve_buyer(order).lower()


def normalize_name(name):
    decoded = unquote(name or "").strip()
    for known in OPTIONS:
        if known.lower() == decoded.lower():
            return known
    return decoded or "Unknown"


# ------------------------------------------------------- AdminCountryService
def _valid_orders(orders):
    return [o for o in orders if o.status not in ("CANCELLED", "PENDING_PAYMENT")]


def _units(order):
    return sum(i.quantity for i in order.items)


def _resolve_user_countries(addresses, orders):
    mp = {}
    for a in addresses:
        if not a.user_id:
            continue
        if a.user_id in mp and not a.default_address:
            continue
        mp[a.user_id] = resolve_address(a)
    for o in orders:
        if not o.user_id or o.user_id in mp:
            continue
        mp[o.user_id] = resolve_buyer(o)
    return mp


def _ensure_agg(mp, name):
    if name not in mp:
        mp[name] = {"sellerIds": set(), "buyerIds": set(), "buyOrders": 0, "sellOrders": 0,
                    "buyMoney": 0.0, "sellMoney": 0.0, "buyUnits": 0, "sellUnits": 0,
                    "wishlistViews": 0, "wishlistOnSellers": 0}


def _load_orders(*extra):
    opts = [joinedload(Order.user), joinedload(Order.seller), joinedload(Order.items)] + list(extra)
    return Order.query.options(*opts).all()


def countries_list(sort="money_desc"):
    orders = _valid_orders(_load_orders())
    sellers = Seller.query.all()
    addresses = Address.query.all()
    wishlists = WishlistItem.query.options(joinedload(WishlistItem.user),
                                           joinedload(WishlistItem.product).joinedload(Product.seller)).all()
    user_country = _resolve_user_countries(addresses, orders)
    mp = {}

    for s in sellers:
        if s.status == "PENDING":
            continue
        c = resolve_seller(s)
        if c == "Unknown":
            continue
        _ensure_agg(mp, c)
        mp[c]["sellerIds"].add(s.id)

    for uid, c in user_country.items():
        if c == "Unknown":
            continue
        _ensure_agg(mp, c)
        mp[c]["buyerIds"].add(uid)

    for o in orders:
        buy = resolve_buyer(o)
        if buy != "Unknown":
            _ensure_agg(mp, buy)
            mp[buy]["buyOrders"] += 1
            mp[buy]["buyMoney"] += float(o.total)
            mp[buy]["buyUnits"] += _units(o)
            if o.user_id:
                mp[buy]["buyerIds"].add(o.user_id)
        if o.seller:
            sell = resolve_seller(o.seller)
            if sell == "Unknown":
                continue
            _ensure_agg(mp, sell)
            mp[sell]["sellOrders"] += 1
            mp[sell]["sellMoney"] += float(o.total)
            mp[sell]["sellUnits"] += _units(o)
            mp[sell]["sellerIds"].add(o.seller_id)

    for w in wishlists:
        if w.user_id and user_country.get(w.user_id, "Unknown") != "Unknown":
            c = user_country[w.user_id]
            _ensure_agg(mp, c)
            mp[c]["wishlistViews"] += 1
        seller = w.product.seller if w.product else None
        if seller:
            c = resolve_seller(seller)
            if c != "Unknown":
                _ensure_agg(mp, c)
                mp[c]["wishlistOnSellers"] += 1

    rows = [{
        "name": name,
        "sellerCount": len(a["sellerIds"]),
        "buyerCount": len(a["buyerIds"]),
        "buyOrders": a["buyOrders"],
        "sellOrders": a["sellOrders"],
        "buyMoney": round(a["buyMoney"], 2),
        "sellMoney": round(a["sellMoney"], 2),
        "buyUnits": a["buyUnits"],
        "sellUnits": a["sellUnits"],
        "wishlistViews": a["wishlistViews"],
    } for name, a in mp.items()]
    return _sort_country_rows(rows, sort)


def _sort_country_rows(rows, sort):
    keymap = {
        "name_asc": (lambda r: r["name"].lower(), False),
        "name_desc": (lambda r: r["name"].lower(), True),
        "sellers_asc": (lambda r: r["sellerCount"], False),
        "sellers_desc": (lambda r: r["sellerCount"], True),
        "buyers_asc": (lambda r: r["buyerCount"], False),
        "buyers_desc": (lambda r: r["buyerCount"], True),
        "buy_money_desc": (lambda r: r["buyMoney"], True),
        "sell_money_desc": (lambda r: r["sellMoney"], True),
    }
    key, rev = keymap.get(sort, (lambda r: r["buyMoney"] + r["sellMoney"], True))
    return sorted(rows, key=key, reverse=rev)


def _top(orders, limit, by_category):
    mp = {}
    for o in orders:
        for item in o.items:
            if by_category:
                label = item.product.category.name if item.product and item.product.category else "Other"
            else:
                label = item.product_name or "Product"
            r = mp.setdefault(label, {"label": label, "qty": 0, "amount": 0.0})
            r["qty"] += int(item.quantity)
            r["amount"] += float(item.line_total)
    rows = sorted(mp.values(), key=lambda r: r["amount"], reverse=True)[:limit]
    return [{"label": r["label"], "qty": r["qty"], "amount": round(r["amount"], 2)} for r in rows]


def country_profile(country_name, seller_sort="revenue_desc", buyer_sort="spent_desc"):
    country = normalize_name(country_name)
    all_orders = _valid_orders(_load_orders(
        joinedload(Order.items).joinedload(OrderItem.product).joinedload(Product.category),
        joinedload(Order.commission)))
    all_sellers = Seller.query.all()
    addresses = Address.query.all()
    wishlists = WishlistItem.query.options(joinedload(WishlistItem.user),
                                           joinedload(WishlistItem.product).joinedload(Product.seller)).all()
    user_country = _resolve_user_countries(addresses, all_orders)

    sellers = [s for s in all_sellers if matches_seller(country, s)]
    seller_ids = [s.id for s in sellers]
    buy_orders = [o for o in all_orders if matches_buyer(country, o)]
    sell_orders = [o for o in all_orders if o.seller_id and o.seller_id in seller_ids]

    buyer_ids = {uid for uid, c in user_country.items() if c.lower() == country.lower()}
    buyer_ids |= {o.user_id for o in buy_orders if o.user_id}
    buyer_id_list = list(buyer_ids)

    buy_money = round(sum(float(o.total) for o in buy_orders), 2)
    sell_money = round(sum(float(o.total) for o in sell_orders), 2)
    buy_units = sum(_units(o) for o in buy_orders)
    sell_units = sum(_units(o) for o in sell_orders)

    wishlist_views = len([w for w in wishlists if w.user_id and w.user_id in buyer_ids])
    wishlist_on_sellers = len([w for w in wishlists
                               if w.product and w.product.seller_id and w.product.seller_id in seller_ids])

    today = utcnow()
    year = today.year
    month_start = datetime(today.year, today.month, 1)
    year_start = datetime(today.year, 1, 1)

    seller_rows = []
    for s in sellers:
        so = [o for o in sell_orders if o.seller_id == s.id]
        seller_rows.append({
            "id": s.id, "storeName": s.store_name, "location": s.location or "\u2014",
            "units": sum(_units(o) for o in so),
            "revenue": round(sum(float(o.total) for o in so), 2),
        })
    sm = {
        "name_asc": ("storeName", False), "name_desc": ("storeName", True),
        "units_desc": ("units", True), "units_asc": ("units", False),
        "revenue_asc": ("revenue", False),
    }
    k, rev = sm.get(seller_sort, ("revenue", True))
    seller_rows.sort(key=lambda r: (r[k].lower() if isinstance(r[k], str) else r[k]), reverse=rev)

    users = {u.id: u for u in User.query.filter(User.id.in_(buyer_id_list)).all()} if buyer_id_list else {}
    buyer_rows = []
    for uid in buyer_id_list:
        u = users.get(uid)
        if not u:
            continue
        uo = [o for o in buy_orders if o.user_id == uid]
        buyer_rows.append({
            "id": u.id, "fullName": u.full_name, "email": u.email, "orders": len(uo),
            "spent": round(sum(float(o.total) for o in uo), 2),
            "wishlist": len([w for w in wishlists if w.user_id == uid]),
        })
    bm = {
        "name_asc": ("fullName", False), "name_desc": ("fullName", True),
        "orders_desc": ("orders", True), "orders_asc": ("orders", False),
        "spent_asc": ("spent", False),
    }
    k, rev = bm.get(buyer_sort, ("spent", True))
    buyer_rows.sort(key=lambda r: (r[k].lower() if isinstance(r[k], str) else r[k]), reverse=rev)
    buyer_rows = buyer_rows[:50]

    sell_order_ids = [o.id for o in sell_orders]
    commissions = Commission.query.filter(Commission.order_id.in_(sell_order_ids)).all() if sell_order_ids else []
    fees_earned = round(sum(float(c.commission_amount) for c in commissions if c.payout_status == "RELEASED"), 2)
    fees_in_escrow = round(sum(float(c.commission_amount) for c in commissions
                               if c.payout_status in ("HELD", "IN_PROTECTION")), 2)

    def in_year_month(o, m):
        return o.placed_at and o.placed_at.year == year and o.placed_at.month == m

    buy_trend = [round(sum(float(o.total) for o in buy_orders if in_year_month(o, m)), 2) for m in range(1, 13)]
    sell_trend = [round(sum(float(o.total) for o in sell_orders if in_year_month(o, m)), 2) for m in range(1, 13)]
    orders_trend = [len([o for o in buy_orders if in_year_month(o, m)]) for m in range(1, 13)]

    def since(orders, start):
        return [o for o in orders if o.placed_at and o.placed_at >= start]

    recent = sorted(buy_orders, key=lambda o: o.placed_at or datetime.min, reverse=True)[:12]

    return {
        "countryName": country,
        "sellerCount": len(sellers),
        "buyerCount": len(buyer_id_list),
        "buyOrders": len(buy_orders),
        "sellOrders": len(sell_orders),
        "buyMoney": buy_money,
        "sellMoney": sell_money,
        "buyUnits": buy_units,
        "sellUnits": sell_units,
        "wishlistViews": wishlist_views,
        "wishlistOnSellers": wishlist_on_sellers,
        "feesEarned": fees_earned,
        "feesInEscrow": fees_in_escrow,
        "feesTotal": round(fees_earned + fees_in_escrow, 2),
        "commissionRate": "3%",
        "feeOrders": len(commissions),
        "buyMonth": round(sum(float(o.total) for o in since(buy_orders, month_start)), 2),
        "sellMonth": round(sum(float(o.total) for o in since(sell_orders, month_start)), 2),
        "buyYear": round(sum(float(o.total) for o in since(buy_orders, year_start)), 2),
        "sellYear": round(sum(float(o.total) for o in since(sell_orders, year_start)), 2),
        "ordersMonth": len(since(buy_orders, month_start)),
        "ordersYear": len(since(buy_orders, year_start)),
        "topBuyCategories": _top(buy_orders, 5, True),
        "topSellCategories": _top(sell_orders, 5, True),
        "topBuyProducts": _top(buy_orders, 5, False),
        "topSellProducts": _top(sell_orders, 5, False),
        "sellers": seller_rows,
        "buyers": buyer_rows,
        "recentOrders": recent,
        "months": ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
        "buyTrend": buy_trend,
        "sellTrend": sell_trend,
        "ordersTrend": orders_trend,
        "year": year,
        "sellerSort": seller_sort,
        "buyerSort": buyer_sort,
    }
