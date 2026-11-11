
import os

UPLOAD_DIR = os.path.join("static", "images", "user")
os.makedirs(UPLOAD_DIR, exist_ok=True)
ALLOWED_EXT = {"png", "jpg", "jpeg", "gif"}

