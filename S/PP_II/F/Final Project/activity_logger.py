
import json
from flask import request, session
from extensions import db
from models.activity_log import ActivityLog


def log_activity(
    action,
    module,
    description=None,
    table_name=None,
    record_id=None,
    old_data=None,
    new_data=None,
    user_id=None,
    username=None,
):
    """Save one row into activity_logs (for report / audit)."""
    try:
        if user_id is None:
            user_id = session.get("user_id")
        if username is None:
            username = session.get("username")

        def _dump(data):
            if data is None:
                return None
            if isinstance(data, str):
                return data
            return json.dumps(data, default=str, ensure_ascii=False, indent=2)

        log = ActivityLog(
            user_id=user_id,
            username=username or "unknown",
            action=action,
            module=module,
            table_name=table_name,
            record_id=str(record_id) if record_id is not None else None,
            description=description,
            old_data=_dump(old_data),
            new_data=_dump(new_data),
            ip_address=request.remote_addr if request else None,
            user_agent=(request.headers.get("User-Agent")[:255] if request else None),
        )
        db.session.add(log)
        db.session.commit()
    except Exception:
        db.session.rollback()
