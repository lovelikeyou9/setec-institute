"""Port of KhmerStore app/helpers.php + the PHP/Blade helpers used by the templates."""
import fnmatch
import json
import math
import re
from datetime import datetime
from decimal import Decimal
from urllib.parse import quote, urlencode

from flask import flash, g, get_flashed_messages, redirect, render_template, request, session


# --------------------------------------------------------------------------
# helpers.php
# --------------------------------------------------------------------------
def default_product_image():
    """Placeholder used when a product has no uploaded image."""
    return "/uploads/No_Image_Available.jpg"


def media_url(path, fallback=True):
    """Encode a public media path so spaces, +, & etc. work in <img src>."""
    if path is None or str(path).strip() == "":
        return default_product_image() if fallback else ""
    path = str(path).strip().replace("\\", "/")
    if re.match(r"^https?://", path, re.I):
        return path
    absolute = path.startswith("/")
    segments = [s for s in path.split("/") if s != ""]
    encoded = "/".join(quote(s, safe="") for s in segments)
    return ("/" if absolute else "") + encoded


def product_gallery_urls(image_url, gallery_csv):
    """Unique gallery paths for a product (primary + gallery_urls), no empties."""
    items = []
    for url in [image_url or ""] + [s.strip() for s in (gallery_csv or "").split(",")]:
        url = (url or "").strip()
        if url == "" or url in items:
            continue
        items.append(url)
    return items


# --------------------------------------------------------------------------
# generic PHP-ish helpers (exposed to Jinja)
# --------------------------------------------------------------------------
def to_number(value, default=0.0):
    if value is None or value == "":
        return default
    if isinstance(value, Decimal):
        return float(value)
    try:
        return float(value)
    except (TypeError, ValueError):
        return default


def number_format(value, decimals=0, dec_point=".", thousands=","):
    num = to_number(value)
    decimals = int(decimals or 0)
    # PHP rounds half away from zero
    q = Decimal(str(num)).quantize(Decimal(1).scaleb(-decimals), rounding="ROUND_HALF_UP")
    s = f"{q:,.{decimals}f}"
    return s.replace(",", "\x00").replace(".", dec_point).replace("\x00", thousands)


def str_replace(search, replace, subject):
    return str(subject if subject is not None else "").replace(str(search), str(replace))


def implode(sep, items):
    return str(sep).join(str(i) for i in (items or []))


def count(value):
    try:
        return len(value)
    except TypeError:
        return 0


def mb_substr(s, start, length=None):
    s = "" if s is None else str(s)
    return s[start:] if length is None else s[start:start + length]


def mb_strtolower(s):
    return ("" if s is None else str(s)).lower()


def str_contains(haystack, needle):
    return needle in ("" if haystack is None else str(haystack))


def urlencode_(s):
    return quote(str(s if s is not None else ""), safe="").replace("%20", "+")


def rawurlencode(s):
    return quote(str(s if s is not None else ""), safe="")


def str_limit(value, limit=100, end="..."):
    value = "" if value is None else str(value)
    if len(value) <= limit:
        return value
    return value[:limit].rstrip() + end


def max_(*args):
    args = [to_number(a) for a in args]
    return max(args)


def min_(*args):
    args = [to_number(a) for a in args]
    return min(args)


def slugify(text):
    text = (text or "").lower()
    text = re.sub(r"[^a-z0-9]+", "-", text)
    return text.strip("-") or "item"


# PHP date() format -> python strftime
_PHP_FMT = {
    "d": "%d", "D": "%a", "j": "{day}", "l": "%A", "m": "%m", "M": "%b", "F": "%B",
    "n": "{month}", "Y": "%Y", "y": "%y", "H": "%H", "h": "%I", "G": "{hour}",
    "i": "%M", "s": "%S", "A": "%p", "a": "%p",
}


def php_date(dt, fmt):
    if dt is None:
        return None
    out = []
    i = 0
    while i < len(fmt):
        ch = fmt[i]
        if ch == "\\" and i + 1 < len(fmt):
            out.append(fmt[i + 1])
            i += 2
            continue
        if ch in _PHP_FMT:
            token = _PHP_FMT[ch]
            if token == "{day}":
                out.append(str(dt.day))
            elif token == "{month}":
                out.append(str(dt.month))
            elif token == "{hour}":
                out.append(str(dt.hour))
            else:
                out.append(dt.strftime(token))
        else:
            out.append(ch)
        i += 1
    return "".join(out)


def date_fmt(dt, fmt="d M Y"):
    """optional($dt)->format($fmt) -> '' when null."""
    if not dt:
        return ""
    if isinstance(dt, str):
        return dt
    return php_date(dt, fmt)


def fmt_dt(dt):
    """$fmt = fn($dt) => $dt ? $dt->format('d M Y H:i') : null"""
    return php_date(dt, "d M Y H:i") if dt else None


def date_now(fmt="Y"):
    return php_date(datetime.utcnow(), fmt)


# --------------------------------------------------------------------------
# request / flash / old input (Blade: request(), old(), session('message'))
# --------------------------------------------------------------------------
def url(path="/"):
    path = path or "/"
    if re.match(r"^https?://", path):
        return path
    return path if path.startswith("/") else "/" + path


def asset(path):
    return "/static/" + str(path).lstrip("/")


def old(key, default=""):
    data = getattr(g, "old_input", None) or {}
    if key in data and data[key] is not None:
        return data[key]
    return default if default is not None else ""


def flash_msg(category):
    """session('message') / session('error') / session('open_modal')."""
    msgs = get_flashed_messages(category_filter=[category])
    return msgs[0] if msgs else None


def request_is(pattern):
    path = request.path or "/"
    path = "/" if path == "/" else path.strip("/")
    pat = pattern if pattern == "/" else pattern.strip("/")
    return fnmatch.fnmatchcase(path, pat)


def req_input(key, default=None):
    v = request.values.get(key)
    return v if v not in (None, "") else default


def req_int(key, default=None):
    """Request value as int (fixes '12' == 12 filter/select mismatches)."""
    v = request.values.get(key)
    if v in (None, ""):
        return default
    try:
        return int(v)
    except (TypeError, ValueError):
        return default


def url_current():
    return request.base_url


def url_full():
    return request.url


def status_tone(status, kind="order"):
    status = status or ""
    if kind == "payout":
        if status in ("RELEASED", "PAID"):
            return "green"
        if status in ("REFUNDED", "CANCELLED"):
            return "red"
        if status in ("HELD", "IN_PROTECTION"):
            return "amber"
        return "blue"
    if status in ("DELIVERED", "COMPLETED", "PAID_OUT", "APPROVED"):
        return "green"
    if status in ("CANCELLED", "REFUNDED", "DISPUTED", "REJECTED"):
        return "red"
    if status in ("PENDING_PAYMENT", "PAYMENT_CONFIRMED", "FUNDS_HELD", "SELLER_PROCESSING",
                  "SHIPPED", "OUT_FOR_DELIVERY", "PENDING"):
        return "amber"
    return "blue"


_STEP_INDEX = {
    "PENDING_PAYMENT": 0, "PAYMENT_CONFIRMED": 1, "FUNDS_HELD": 2, "SELLER_PROCESSING": 3,
    "SHIPPED": 4, "OUT_FOR_DELIVERY": 5, "DELIVERED": 6, "DISPUTED": 6, "COMPLETED": 7,
}


def order_step_index(status):
    return _STEP_INDEX.get(status, -1)


def spec_rows(text):
    rows = []
    for line in re.split(r"\r\n|\n|\r", text or ""):
        line = line.strip()
        if not line:
            continue
        parts = [p.strip() for p in line.split(":", 1)]
        if len(parts) == 2 and parts[0] != "":
            rows.append({"key": parts[0], "value": parts[1]})
        else:
            rows.append({"key": "Spec", "value": line})
    return rows


_SIDEBAR_MAPS = {
    "account": [
        ("account", "overview"), ("dashboard", "overview"),
        ("account/orders", "orders"), ("orders/*", "orders"),
        ("account/wishlist", "wishlist"), ("account/cart", "cart"),
        ("account/notifications", "notifications"), ("account/addresses", "addresses"),
        ("account/settings", "settings"),
    ],
    "seller": [
        ("seller-dashboard", "dashboard"), ("seller-dashboard/products*", "products"),
        ("seller-dashboard/categories", "categories"), ("seller-dashboard/brands", "brands"),
        ("seller-dashboard/orders*", "orders"), ("seller-dashboard/reviews", "reviews"),
        ("seller-dashboard/earnings", "earnings"), ("seller-dashboard/settings", "settings"),
        ("account/orders", "my-orders"), ("orders/*", "my-orders"),
        ("account/wishlist", "wishlist"), ("account/cart", "cart"),
        ("account/notifications", "notifications"), ("account/addresses", "addresses"),
        ("account/settings", "account-settings"),
    ],
    "admin": [
        ("admin", "dashboard"), ("admin/users*", "users"), ("admin/seller-applications*", "applications"),
        ("admin/sellers*", "sellers"), ("admin/products*", "products"),
        ("admin/categories*", "categories"), ("admin/brands*", "brands"),
        ("admin/orders*", "orders"), ("admin/commissions*", "commissions"),
        ("admin/countries*", "countries"), ("admin/reviews*", "reviews"),
        ("admin/settings*", "settings"),
        ("account/orders", "my-orders"), ("orders/*", "my-orders"),
        ("account/wishlist", "wishlist"), ("account/cart", "cart"),
        ("account/notifications", "notifications"), ("account/addresses", "addresses"),
        ("account/settings", "account-settings"),
    ],
}


def nav_for(kind, active=""):
    """$nav = $activeNav ?? guess from the URL (sidebar partials)."""
    if active:
        return active
    for pattern, nav in _SIDEBAR_MAPS.get(kind, []):
        if request_is(pattern):
            return nav
    return ""


# --------------------------------------------------------------------------
# Validation (Laravel $request->validate) + old input
# --------------------------------------------------------------------------
class FormErrors:
    def __init__(self, errors=None):
        self._errors = errors or {}

    def any(self):
        return bool(self._errors)

    def all(self):
        return [m for msgs in self._errors.values() for m in msgs]

    def has(self, key):
        return key in self._errors

    def first(self, key):
        msgs = self._errors.get(key)
        return msgs[0] if msgs else ""

    def __bool__(self):
        return bool(self._errors)


def _label(field):
    return field.replace("_", " ")


def validate(rules, source=None, unique=None, exists=None, messages=None):
    """Tiny Laravel-style validator.

    rules: {'email': 'required|email|max:100', ...}
    unique: {'email': callable(value)->bool (True means already taken)}
    exists: {'category_id': callable(value)->bool}
    Returns (data, errors) - data only holds the declared fields.
    """
    source = request.form if source is None else source
    messages = messages or {}
    unique = unique or {}
    exists = exists or {}
    data, errors = {}, {}
    for field, rule_str in rules.items():
        rule_list = rule_str.split("|")
        raw = source.get(field)
        value = raw.strip() if isinstance(raw, str) else raw
        nullable = "nullable" in rule_list
        required = "required" in rule_list
        empty = value is None or value == ""
        label = _label(field)

        def err(msg, rule):
            errors.setdefault(field, []).append(messages.get(f"{field}.{rule}", msg))

        if empty:
            if required:
                err(f"The {label} field is required.", "required")
            data[field] = None
            continue
        before = len(errors.get(field, []))
        is_num = "numeric" in rule_list or "integer" in rule_list
        for rule in rule_list:
            name, _, arg = rule.partition(":")
            if name == "email":
                if not re.match(r"^[^@\s]+@[^@\s]+\.[^@\s]+$", str(value)):
                    err(f"The {label} field must be a valid email address.", "email")
            elif name == "integer":
                try:
                    int(str(value))
                except ValueError:
                    err(f"The {label} field must be an integer.", "integer")
            elif name == "numeric":
                try:
                    float(str(value))
                except ValueError:
                    err(f"The {label} field must be a number.", "numeric")
            elif name == "max":
                if is_num:
                    try:
                        if float(value) > float(arg):
                            err(f"The {label} field must not be greater than {arg}.", "max")
                    except ValueError:
                        pass
                elif len(str(value)) > int(arg):
                    err(f"The {label} field must not be greater than {arg} characters.", "max")
            elif name == "min":
                if is_num:
                    try:
                        if float(value) < float(arg):
                            err(f"The {label} field must be at least {arg}.", "min")
                    except ValueError:
                        pass
                elif len(str(value)) < int(arg):
                    err(f"The {label} field must be at least {arg} characters.", "min")
            elif name == "in":
                if str(value) not in arg.split(","):
                    err(f"The selected {label} is invalid.", "in")
            elif name == "confirmed":
                if str(value) != str(source.get(field + "_confirmation") or ""):
                    err(f"The {label} field confirmation does not match.", "confirmed")
            elif name == "unique" and field in unique:
                if unique[field](value):
                    err(f"The {label} has already been taken.", "unique")
            elif name == "exists" and field in exists:
                if not exists[field](value):
                    err(f"The selected {label} is invalid.", "exists")
            elif name == "boolean":
                pass
        data[field] = value
    return data, errors


def back_with_errors(errors, fallback="/", extra_flash=None):
    """redirect()->back()->withErrors()->withInput()"""
    session["_errors"] = errors
    session["_old"] = {k: v for k, v in request.form.items() if "password" not in k}
    for cat, msg in (extra_flash or {}).items():
        flash(msg, cat)
    return redirect(request.referrer or fallback)


def back_with_input(fallback="/"):
    session["_old"] = {k: v for k, v in request.form.items() if "password" not in k}
    return redirect(request.referrer or fallback)


def back(message=None, category="message", fallback="/"):
    if message:
        flash(message, category)
    return redirect(request.referrer or fallback)


def to_bool(value):
    if isinstance(value, bool):
        return value
    return str(value).strip().lower() in ("1", "true", "on", "yes")


# --------------------------------------------------------------------------
# Pagination (Laravel LengthAwarePaginator + vendor/pagination/mmt.blade.php)
# --------------------------------------------------------------------------
class Paginator:
    def __init__(self, items, page, per_page, total):
        self.items = list(items)
        self.page = page
        self.per_page = per_page
        self._total = total
        self.last_page = max(1, int(math.ceil(total / per_page))) if per_page else 1

    def __iter__(self):
        return iter(self.items)

    def __len__(self):
        return len(self.items)

    def __bool__(self):
        return bool(self.items)

    def total(self):
        return self._total

    def count(self):
        return len(self.items)

    def hasPages(self):
        return self.last_page > 1

    def currentPage(self):
        return self.page

    def lastPage(self):
        return self.last_page

    def onFirstPage(self):
        return self.page <= 1

    def hasMorePages(self):
        return self.page < self.last_page

    def url(self, page):
        args = request.args.to_dict(flat=True)
        args["page"] = page
        return f"{request.path}?{urlencode(args)}"

    def previousPageUrl(self):
        return self.url(self.page - 1) if self.page > 1 else None

    def nextPageUrl(self):
        return self.url(self.page + 1) if self.hasMorePages() else None

    def elements(self):
        last, cur, side = self.last_page, self.page, 3
        if last <= side * 2 + 6:
            return [{p: self.url(p) for p in range(1, last + 1)}]
        out = []
        first_block = {p: self.url(p) for p in range(1, 3)}
        lo, hi = max(3, cur - side), min(last - 2, cur + side)
        out.append(first_block)
        if lo > 3:
            out.append("...")
        out.append({p: self.url(p) for p in range(lo, hi + 1)})
        if hi < last - 2:
            out.append("...")
        out.append({p: self.url(p) for p in range(last - 1, last + 1)})
        return out

    def links(self):
        return render_template("vendor/pagination/mmt.html", paginator=self, elements=self.elements())


def paginate(query, per_page=12):
    try:
        page = max(1, int(request.args.get("page", 1)))
    except ValueError:
        page = 1
    total = query.order_by(None).count()
    items = query.limit(per_page).offset((page - 1) * per_page).all()
    return Paginator(items, page, per_page, total)


def _kv(value):
    """foreach ($x as $k => $v): dict -> items, list -> enumerate."""
    if value is None:
        return []
    if isinstance(value, dict):
        return list(value.items())
    return list(enumerate(value))


def default_option_index(choices, product_name=""):
    """Pick the pill that matches the product title (e.g. 16GB on a 16GB RAM listing).

    Without this, the first choice (often 8GB) stays active even when the product is 16GB.
    """
    choices = list(choices or [])
    if not choices:
        return 0
    name = str(product_name or "").upper()
    # Prefer longer matches first (64GB before 4GB, 16GB before 8GB substring issues)
    ranked = sorted(enumerate(choices), key=lambda iv: len(str(iv[1])), reverse=True)
    for i, opt in ranked:
        token = str(opt or "").strip().upper()
        if token and token in name:
            return i
    return 0


def json_encode(v):
    return json.dumps(v, ensure_ascii=False, default=str)


def register_template_helpers(app):
    """Expose the PHP/Blade helper layer to Jinja."""
    from jinja2 import ChainableUndefined
    from services.product_variants import is_capacity_group, resolve_display_price

    app.jinja_env.undefined = ChainableUndefined
    app.jinja_env.add_extension("jinja2.ext.do")
    app.jinja_env.finalize = lambda v: "" if v is None else v   # PHP echo null -> ""
    g_ = app.jinja_env.globals
    g_.update(
        url=url, asset=asset, media_url=media_url, number_format=number_format,
        str_replace=str_replace, implode=implode, count=count, mb_substr=mb_substr,
        mb_strtolower=mb_strtolower, str_contains=str_contains, urlencode=urlencode_,
        rawurlencode=rawurlencode, str_limit=str_limit, max=max_, min=min_,
        date_fmt=date_fmt, fmt_dt=fmt_dt, date=date_now, old=old, flash_msg=flash_msg,
        request_is=request_is, req_input=req_input, req_int=req_int,
        url_current=url_current, url_full=url_full,
        status_tone=status_tone, order_step_index=order_step_index, spec_rows=spec_rows,
        nav_for=nav_for, default_product_image=default_product_image, json_encode=json_encode,
        default_option_index=default_option_index,
        resolve_display_price=resolve_display_price,
        is_capacity_group=is_capacity_group,
        empty=lambda v: not v, strtolower=lambda s: str(s or '').lower(),
        strtoupper=lambda s: str(s or '').upper(), ucfirst=lambda s: str(s or '')[:1].upper() + str(s or '')[1:],
    )
    app.jinja_env.filters.update(
        take=lambda seq, n: list(seq or [])[: int(n)],
        bool=lambda v: bool(v),
        urlencode=urlencode_,
        media_url=media_url,
        number_format=number_format,
        kv=_kv,
        json_encode=json_encode,
    )
