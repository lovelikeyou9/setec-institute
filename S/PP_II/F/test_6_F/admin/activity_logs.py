
from admin import admin_bp
from flask import render_template, request
from admin.auth import login_required
from extensions import db, limiter, get_user_id
from models.activity_log import ActivityLog
from sqlalchemy import or_


@admin_bp.get("/activity-logs")
@login_required
@limiter.limit("10 per minute", key_func=get_user_id, scope="admin_panel")
def activity_logs():
    module = "activity_logs"

    q = request.args.get("q", "").strip()
    filter_module = request.args.get("module", "").strip()
    filter_action = request.args.get("action", "").strip()
    per_page = request.args.get("per_page", "50")
    try:
        per_page = int(per_page)
    except ValueError:
        per_page = 50
    if per_page not in (25, 50, 100, 200):
        per_page = 50

    page = request.args.get("page", 1, type=int)
    if page < 1:
        page = 1

    query = ActivityLog.query.order_by(ActivityLog.id.desc())

    if filter_module:
        query = query.filter(ActivityLog.module == filter_module)
    if filter_action:
        query = query.filter(ActivityLog.action == filter_action)
    if q:
        like = f"%{q}%"
        query = query.filter(
            or_(
                ActivityLog.description.ilike(like),
                ActivityLog.username.ilike(like),
                ActivityLog.module.ilike(like),
                ActivityLog.action.ilike(like),
                ActivityLog.record_id.ilike(like),
            )
        )

    pagination = query.paginate(page=page, per_page=per_page, error_out=False)
    logs = pagination.items

    modules = [
        row[0]
        for row in db.session.query(ActivityLog.module)
        .distinct()
        .order_by(ActivityLog.module)
        .all()
        if row[0]
    ]
    actions = [
        row[0]
        for row in db.session.query(ActivityLog.action)
        .distinct()
        .order_by(ActivityLog.action)
        .all()
        if row[0]
    ]

    return render_template(
        "admin/activity_logs/activity_logs.html",
        module=module,
        logs=logs,
        pagination=pagination,
        modules=modules,
        actions=actions,
        q=q,
        filter_module=filter_module,
        filter_action=filter_action,
        per_page=per_page,
    )
