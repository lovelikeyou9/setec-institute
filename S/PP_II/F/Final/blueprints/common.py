from urllib.parse import urlparse

from flask import request


def safe_redirect_target(target, default):
    """Accept relative paths and absolute URLs on this host only (no open redirect)."""
    if not target:
        return default
    p = urlparse(target)
    if p.netloc and p.netloc != request.host:
        return default
    if p.scheme and p.scheme not in ("http", "https"):
        return default
    path = p.path or "/"
    if not path.startswith("/") or path.startswith("//"):
        return default
    return path + (("?" + p.query) if p.query else "")


def to_int(value, default=0):
    try:
        return int(str(value).strip())
    except (TypeError, ValueError):
        return default
