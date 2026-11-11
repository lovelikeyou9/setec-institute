"""Uploads -> static/uploads/<folder>/<random>.<ext>  (served at /uploads/...)."""
import os
import secrets

from flask import current_app
from PIL import Image, UnidentifiedImageError

IMAGE_EXT = {"jpg", "jpeg", "png", "gif", "webp", "avif", "bmp"}


def _ext(filename):
    return os.path.splitext(filename or "")[1].lower().lstrip(".")


def is_image_file(file_storage, max_mb=None):
    """True when the upload looks like a real image (Pillow check; avif trusted by extension)."""
    if not file_storage or not getattr(file_storage, "filename", ""):
        return False
    ext = _ext(file_storage.filename)
    if ext not in IMAGE_EXT:
        return False
    if max_mb:
        file_storage.stream.seek(0, os.SEEK_END)
        size = file_storage.stream.tell()
        file_storage.stream.seek(0)
        if size > max_mb * 1024 * 1024:
            return False
    if ext == "avif":
        return True
    try:
        pos = file_storage.stream.tell()
        with Image.open(file_storage.stream) as im:
            im.verify()
        file_storage.stream.seek(pos)
        return True
    except (UnidentifiedImageError, OSError, ValueError):
        file_storage.stream.seek(0)
        return False


def save_upload(file_storage, folder, default_ext="jpg"):
    """Save an uploaded file; returns the public path like /uploads/products/abc.jpg or None."""
    if not file_storage or not getattr(file_storage, "filename", ""):
        return None
    ext = _ext(file_storage.filename)
    if ext not in IMAGE_EXT:
        ext = default_ext
    root = current_app.config["UPLOAD_DIR"]
    target_dir = os.path.join(root, *folder.split("/"))
    os.makedirs(target_dir, exist_ok=True)
    name = f"{secrets.token_hex(20)}.{ext}"
    file_storage.stream.seek(0)
    file_storage.save(os.path.join(target_dir, name))
    return f"/uploads/{folder}/{name}"
