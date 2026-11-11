from flask import request, session

from extensions import db
from models.logs import ActivityLog


def log_activity(
    action,
    module,
    description=None,
    table_name=None,
    record_id=None,
    old_data=None,
    new_data=None,
    user_id=None,
):
    """Save one row into activity_logs (audit trail). Never raises."""
    try:
        if user_id is None:
            user_id = session.get("user_id")

        def _json(data):
            if data is None:
                return None
            if isinstance(data, (dict, list)):
                return data
            return {"value": str(data)}

        rid = None
        if record_id is not None:
            try:
                rid = int(record_id)
            except (TypeError, ValueError):
                rid = None

        log = ActivityLog(
            user_id=user_id,
            action=action,
            module=module,
            table_name=table_name,
            record_id=rid,
            description=description,
            old_data=_json(old_data),
            new_data=_json(new_data),
            ip=(request.remote_addr if request else None),
        )
        db.session.add(log)
        db.session.commit()
    except Exception:
        db.session.rollback()
