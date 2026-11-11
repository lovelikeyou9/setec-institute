"""Port of App\\Services\\ProductVariantOptions + per-choice prices in option_groups."""
import json
import re


def _split(value):
    if value is None or str(value).strip() == "":
        return []
    return [p.strip() for p in str(value).split(",") if p.strip()]


def _normalize(value):
    if value is None:
        return None
    value = str(value).strip()
    return None if value == "" else value


def _category_key(category):
    if not category:
        return ""
    if getattr(category, "slug", None):
        return category.slug.strip().lower()
    if getattr(category, "name", None):
        return category.name.strip().lower().replace(" ", "-")
    return ""


def for_category(category):
    key = _category_key(category)
    if key in ("laptop", "gaming-pc", "computers", "workstations"):
        return {"color": "Black,Silver", "storage": "512GB,1TB,2TB", "ram": "16GB,32GB,64GB"}
    if key in ("ssd", "hdd", "storage"):
        return {"color": None, "storage": "512GB,1TB,2TB,4TB", "ram": None}
    if key == "ram":
        return {"color": "Black", "storage": None, "ram": "8GB,16GB,32GB,64GB"}
    if key in ("monitor", "smart-tv"):
        return {"color": "Black", "storage": None, "ram": None}
    if key in ("mouse", "keyboard", "headset", "cooling", "pc-case", "case"):
        return {"color": "Black,White", "storage": None, "ram": None}
    return {"color": "Black", "storage": None, "ram": None}


def default_groups(category):
    d = for_category(category)
    groups = []
    if d["color"]:
        groups.append({"label": "Color", "choices": _split(d["color"])})
    if d["storage"]:
        groups.append({"label": "Storage", "choices": _split(d["storage"])})
    if d["ram"]:
        groups.append({"label": "RAM", "choices": _split(d["ram"])})
    return groups or [{"label": "Color", "choices": ["Black"]}]


def parse_capacity_gb(text):
    """Return size in GB units, or None if not a capacity label."""
    m = re.search(r"(\d+(?:\.\d+)?)\s*(TB|GB)\b", str(text or ""), re.I)
    if not m:
        return None
    n = float(m.group(1))
    if m.group(2).upper() == "TB":
        n *= 1024
    return n


def _money(value):
    try:
        return round(float(value), 2)
    except (TypeError, ValueError):
        return None


def _normalize_prices_map(raw, choices):
    """Accept {choice: price} and keep only known choices with numeric prices."""
    if not isinstance(raw, dict):
        return {}
    out = {}
    for choice in choices:
        if choice not in raw:
            continue
        price = _money(raw[choice])
        if price is not None and price >= 0:
            out[choice] = price
    return out


def decode_groups(raw):
    """Decode option_groups JSON.

    Supported shapes per group:
      {"label":"RAM","choices":["8GB","16GB"],"prices":{"8GB":39,"16GB":59},
       "original_prices":{"8GB":49,"16GB":75}}
    """
    if raw is None or str(raw).strip() == "":
        return []
    try:
        data = json.loads(raw) if isinstance(raw, str) else raw
    except ValueError:
        return []
    if not isinstance(data, list):
        return []
    out = []
    for row in data:
        if not isinstance(row, dict):
            continue
        label = str(row.get("label", "")).strip()
        choices_raw = row.get("choices", [])
        if isinstance(choices_raw, str):
            choices_raw = _split(choices_raw)
        if not isinstance(choices_raw, list):
            choices_raw = []
        choices = []
        prices = {}
        originals = {}
        for c in choices_raw:
            if isinstance(c, dict):
                name = str(c.get("value") or c.get("name") or c.get("label") or "").strip()
                if not name:
                    continue
                choices.append(name)
                p = _money(c.get("price"))
                o = _money(c.get("original_price") or c.get("original"))
                if p is not None:
                    prices[name] = p
                if o is not None:
                    originals[name] = o
            else:
                name = str(c).strip()
                if name:
                    choices.append(name)
        if not label or not choices:
            continue
        prices.update(_normalize_prices_map(row.get("prices"), choices))
        originals.update(_normalize_prices_map(row.get("original_prices"), choices))
        group = {"label": label, "choices": choices}
        if prices:
            group["prices"] = prices
        if originals:
            group["original_prices"] = originals
        out.append(group)
    return out


def _choices_for_label(groups, aliases):
    for g in groups:
        if g["label"].strip().lower() in aliases:
            return ",".join(g["choices"])
    return None


def _fit_legacy(value, max_len=500):
    value = _normalize(value)
    if value is None or len(value) <= max_len:
        return value
    parts, length = [], 0
    for part in _split(value):
        add = (0 if not parts else 1) + len(part)
        if length + add > max_len:
            break
        parts.append(part)
        length += add
    return ",".join(parts) if parts else value[:max_len]


def apply_defaults(category, color, storage, ram):
    d = for_category(category)
    return {
        "color_options": _normalize(color) or d["color"],
        "storage_options": _normalize(storage) or d["storage"],
        "ram_options": _normalize(ram) or d["ram"],
    }


def normalize_posted(category, option_groups_json, color, storage, ram):
    groups = decode_groups(option_groups_json)
    if not groups:
        legacy = apply_defaults(category, color, storage, ram)
        if legacy["color_options"]:
            groups.append({"label": "Color", "choices": _split(legacy["color_options"])})
        if legacy["storage_options"]:
            groups.append({"label": "Storage", "choices": _split(legacy["storage_options"])})
        if legacy["ram_options"]:
            groups.append({"label": "RAM", "choices": _split(legacy["ram_options"])})
    groups = [g for g in groups if g.get("label") and g.get("choices")]
    # Keep prices maps when present
    cleaned = []
    for g in groups:
        row = {"label": g["label"], "choices": g["choices"]}
        if g.get("prices"):
            row["prices"] = g["prices"]
        if g.get("original_prices"):
            row["original_prices"] = g["original_prices"]
        cleaned.append(row)
    return {
        "option_groups": json.dumps(cleaned, ensure_ascii=False) if cleaned else None,
        "color_options": _fit_legacy(_choices_for_label(cleaned, ["color", "colour"])),
        "storage_options": _fit_legacy(_choices_for_label(cleaned, ["storage", "ssd", "hdd", "tb", "capacity"])),
        "ram_options": _fit_legacy(_choices_for_label(cleaned, ["ram", "memory", "gb"])),
    }


def groups_from_product(product):
    groups = decode_groups(getattr(product, "option_groups", None))
    if groups:
        return [g for g in groups if g["choices"]]
    out = []
    if getattr(product, "color_options", None):
        out.append({"label": "Color", "choices": _split(product.color_options)})
    if getattr(product, "storage_options", None):
        out.append({"label": "Storage", "choices": _split(product.storage_options)})
    if getattr(product, "ram_options", None):
        out.append({"label": "RAM", "choices": _split(product.ram_options)})
    return out


def scale_capacity_prices(choices, base_choice, base_price, base_original=None):
    """Build price maps by scaling from a base capacity (e.g. 16GB @ $59)."""
    base_gb = parse_capacity_gb(base_choice)
    base_price = _money(base_price)
    base_original = _money(base_original)
    if not base_gb or base_price is None:
        return {}, {}
    prices, originals = {}, {}
    for choice in choices:
        gb = parse_capacity_gb(choice)
        if not gb:
            continue
        factor = gb / base_gb
        prices[choice] = round(base_price * factor, 2)
        if base_original is not None:
            originals[choice] = round(base_original * factor, 2)
    return prices, originals


def ensure_priced_groups(product):
    """Return option groups with prices filled for capacity choices (RAM/Storage).

    Does not write to DB — caller may persist json.dumps(groups).
    """
    groups = groups_from_product(product)
    if not groups:
        return groups
    name = getattr(product, "name", "") or ""
    base_price = _money(getattr(product, "price", None)) or 0.0
    base_original = _money(getattr(product, "original_price", None))
    out = []
    for g in groups:
        label = (g.get("label") or "").lower()
        choices = list(g.get("choices") or [])
        prices = dict(g.get("prices") or {})
        originals = dict(g.get("original_prices") or {})
        is_capacity = label in ("ram", "memory", "storage", "ssd", "hdd", "capacity", "gb", "tb")
        has_capacity_choice = any(parse_capacity_gb(c) for c in choices)
        if (is_capacity or has_capacity_choice) and base_price > 0:
            # Prefer title match as base size; else first choice with capacity
            base_choice = None
            for c in sorted(choices, key=lambda x: len(str(x)), reverse=True):
                if str(c).upper() in name.upper() and parse_capacity_gb(c):
                    base_choice = c
                    break
            if base_choice is None:
                for c in choices:
                    if parse_capacity_gb(c):
                        base_choice = c
                        break
            if base_choice and (len(prices) < len([c for c in choices if parse_capacity_gb(c)])):
                scaled, scaled_o = scale_capacity_prices(
                    choices, base_choice, base_price, base_original)
                for k, v in scaled.items():
                    prices.setdefault(k, v)
                for k, v in scaled_o.items():
                    originals.setdefault(k, v)
        row = {"label": g["label"], "choices": choices}
        if prices:
            row["prices"] = prices
        if originals:
            row["original_prices"] = originals
        out.append(row)
    return out


def choice_price(group, choice, fallback=None):
    prices = (group or {}).get("prices") or {}
    if choice in prices:
        return prices[choice]
    return fallback


_CAPACITY_LABEL_RE = re.compile(
    r"^(ram|memory|storage|ssd|hdd|capacity|size|gb|tb)$|"
    r"(ram|memory|storage|ssd|hdd|capacity)",
    re.I,
)


def is_capacity_group(label):
    """True for RAM / Storage / GB-style groups (drive the product price)."""
    text = (label or "").strip()
    if not text:
        return False
    return bool(_CAPACITY_LABEL_RE.search(text))


def _parse_selected_map(selected_options):
    """Normalize selected options to {label_lower: choice}."""
    if not selected_options:
        return {}
    if isinstance(selected_options, dict):
        return {str(k).strip().lower(): str(v).strip() for k, v in selected_options.items()}
    if isinstance(selected_options, (list, tuple)):
        return {"": str(v).strip() for v in selected_options}
    if isinstance(selected_options, str):
        text = selected_options.strip()
        if text.startswith("{"):
            try:
                return {str(k).strip().lower(): str(v).strip()
                        for k, v in json.loads(text).items()}
            except ValueError:
                return {}
        selected = {}
        for part in text.split("|"):
            if ":" in part:
                k, v = part.split(":", 1)
                selected[k.strip().lower()] = v.strip()
            elif part.strip():
                selected[""] = part.strip()
        return selected
    return {}


def _match_selected_choice(group, selected):
    label = (group.get("label") or "").strip().lower()
    choice = selected.get(label)
    prices = group.get("prices") or {}
    originals = group.get("original_prices") or {}
    if choice:
        return choice
    # Fallback: match by choice value alone (legacy selected payloads)
    for v in selected.values():
        if v in prices or v in originals or v in (group.get("choices") or []):
            return v
    return None


def sum_option_addons(groups, selected):
    """Sum Price$/Was$ add-ons for the currently selected choice in each group.

    Price $ = amount added to the product base price (0 = no change).
    Was $  = compare-at add-on for that choice (defaults to Price $ when empty).
    Returns (addon_sale, addon_original).
    """
    addon_sale = 0.0
    addon_original = 0.0
    for g in groups or []:
        choice = _match_selected_choice(g, selected or {})
        if not choice:
            continue
        prices = g.get("prices") or {}
        originals = g.get("original_prices") or {}
        sale_add = _money(prices.get(choice))
        if sale_add is None:
            sale_add = 0.0
        orig_add = _money(originals.get(choice))
        if orig_add is None:
            orig_add = sale_add
        addon_sale += sale_add
        addon_original += orig_add
    return addon_sale, addon_original


def resolve_display_price(product, option_groups=None, selected_options=None):
    """(sale, original) = base product price + sum of selected option add-ons.

    Example: base 1950, Black +0, 52GB +0 → 1950
             base 1950, White +5, 76GB +600 → 2555
    """
    # Use stored groups as-is (do not invent absolute scaled prices).
    if option_groups is not None:
        groups = option_groups
    else:
        groups = groups_from_product(product)

    base_sale = _money(getattr(product, "price", None)) or 0.0
    base_original = _money(getattr(product, "original_price", None))
    if base_original is None:
        base_original = base_sale

    selected = _parse_selected_map(selected_options)
    if not selected:
        from helpers import default_option_index
        selected = {}
        for g in groups or []:
            label = (g.get("label") or "").strip().lower()
            choices = list(g.get("choices") or [])
            if not label or not choices:
                continue
            idx = default_option_index(choices, getattr(product, "name", "") or "")
            selected[label] = str(choices[idx])

    addon_sale, addon_original = sum_option_addons(groups, selected)
    return base_sale + addon_sale, base_original + addon_original


def resolve_unit_price(product, selected_options=None):
    """Checkout unit price = product price + selected option add-ons."""
    sale, _original = resolve_display_price(product, selected_options=selected_options)
    return sale
