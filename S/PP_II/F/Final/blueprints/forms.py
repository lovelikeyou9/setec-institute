"""Shared form helpers for the seller + admin controllers
(normalizeDemoRequest, image collection, product create/update payload)."""
import math

from flask import flash, request, session

from extensions import db
from helpers import default_product_image, to_bool
from models import Brand, Category
from services import product_variants
from upload_service import IMAGE_EXT, _ext, is_image_file, save_upload

ALIASES = {
    "fullName": "full_name", "logoUrl": "logo_url", "bannerUrl": "banner_url", "coverUrl": "cover_url",
    "avatarUrl": "avatar_url", "imageUrl": "image_url", "categoryId": "category_id", "brandId": "brand_id",
    "originalPrice": "original_price", "storeName": "store_name", "ownerName": "owner_name",
    "colorOptions": "color_options", "storageOptions": "storage_options", "ramOptions": "ram_options",
    "optionGroupsJson": "option_groups",
}
FILE_ALIASES = {"logoFile": "logo", "imageFile": "image", "bannerFile": "banner",
                "avatarFile": "avatar", "coverFile": "cover"}


def normalized_form(flags=("active",)):
    """request.form as a plain dict with camelCase aliases + boolean flags normalised."""
    data = {k: v for k, v in request.form.items()}
    for src, dst in ALIASES.items():
        if src in data and not (data.get(dst) or "").strip():
            data[dst] = data[src]
    for flag in flags:
        if flag in data:
            data[flag] = to_bool(data[flag])
    return data


def get_file(name):
    """Uploaded file by name, honouring the FILE_ALIASES (logoFile -> logo ...)."""
    f = request.files.get(name)
    if f and f.filename:
        return f
    for src, dst in FILE_ALIASES.items():
        if dst == name:
            f = request.files.get(src)
            if f and f.filename:
                return f
    return None


def collect_images(max_files=12):
    files = []
    # HTML uses Laravel-style name="imageFiles[]"; also accept plain keys.
    for key in ("imageFiles[]", "imageFiles", "images", "image", "imageFile"):
        files.extend(request.files.getlist(key))
    if not files:
        for key in request.files:
            if key.lower().startswith("image"):
                files.extend(request.files.getlist(key))
    # De-dupe by filename+size while preserving order
    seen, out = set(), []
    for f in files:
        if not f or not f.filename:
            continue
        marker = (f.filename, getattr(f, "content_length", None) or 0)
        if marker in seen:
            continue
        seen.add(marker)
        out.append(f)
        if len(out) >= max_files:
            break
    return out


def store_images(files):
    urls = []
    for f in files:
        url = save_upload(f, "products")
        if url:
            urls.append(url)
    return urls


def parse_gallery_keep(csv):
    out = []
    for part in (csv or "").split(","):
        part = part.strip()
        if not part or part in out:
            continue
        if part.startswith("/uploads/") or part.startswith("uploads/"):
            out.append(part if part.startswith("/") else "/" + part)
    return out


PRODUCT_RULES = {
    "name": "required|string|max:250",
    "category_id": "required|integer|exists",
    "brand_id": "required|integer|exists",
    "description": "required|string",
    "specifications": "nullable|string",
    "option_groups": "nullable|string",
    "color_options": "nullable|string|max:2000",
    "storage_options": "nullable|string|max:2000",
    "ram_options": "nullable|string|max:2000",
    "price": "required|numeric|min:0",
    "original_price": "nullable|numeric|min:0",
    "stock": "required|integer|min:0",
    "sku": "nullable|string|max:80",
    "warranty": "nullable|string|max:80",
    "galleryKeep": "nullable|string",
    "active": "nullable|boolean",
}

PRODUCT_EXISTS = {
    "category_id": lambda v: db.session.get(Category, int(v)) is not None,
    "brand_id": lambda v: db.session.get(Brand, int(v)) is not None,
}

PRODUCT_MESSAGES = {
    "category_id.required": "Please select a category.",
    "brand_id.required": "Please select a brand.",
}


def validate_images(files):
    errors = []
    for f in files:
        if not is_image_file(f, 10):
            errors.append(f"{f.filename}: must be an image (jpg, png, gif, webp, avif, bmp) up to 10 MB.")
    return errors


def pricing(data):
    price = float(data["price"])
    original = float(data["original_price"]) if data.get("original_price") not in (None, "") else price
    discount = 0
    if original > price:
        discount = int(math.floor((original - price) * 100 / original + 0.5))
    return price, original, discount


def variant_fields(data):
    category = db.session.get(Category, int(data["category_id"]))
    return product_variants.normalize_posted(
        category, data.get("option_groups"), data.get("color_options"),
        data.get("storage_options"), data.get("ram_options"))


def build_gallery(kept, new_urls):
    gallery = []
    for url in list(kept) + list(new_urls):
        url = (url or "").strip()
        if url and url not in gallery:
            gallery.append(url)
    return gallery or [default_product_image()]


def open_modal(name):
    flash(name, "open_modal")
