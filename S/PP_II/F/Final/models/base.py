from datetime import datetime

from sqlalchemy import Integer
from sqlalchemy.types import TypeDecorator


def utcnow():
    """Laravel config/app.php timezone is UTC, so keep UTC like `now()`."""
    return datetime.utcnow()


class MysqlBit(TypeDecorator):
    """MySQL bit(1) <-> Python bool (port of App\\Casts\\MysqlBit)."""

    impl = Integer
    cache_ok = True

    def process_bind_param(self, value, dialect):
        if value is None:
            return None
        if isinstance(value, str):
            return 1 if value.strip().lower() in ("1", "true", "on", "yes") else 0
        return 1 if value else 0

    def process_result_value(self, value, dialect):
        if value is None:
            return False
        if isinstance(value, (bytes, bytearray)):
            return len(value) > 0 and value[0] == 1 if len(value) == 1 else int.from_bytes(value, "big") == 1
        if isinstance(value, str):
            if value in ("", "0", "\x00"):
                return False
            return value in ("1", "\x01") or ord(value[0]) == 1
        return bool(value)
