
from admin import admin_bp
from flask import render_template, request, make_response
# from .auth import login_required
from admin.auth import login_required
from extensions import limiter, get_user_id


# After login: refresh admin dashboard more than 10 times / minute -> 429 ban (by user_id)
@admin_bp.get('/dashboard')
@login_required
@limiter.limit("10 per minute", key_func=get_user_id, scope="admin_panel") # test_5_F
def dashboard():
    module = 'dashboard'
    # assert False, session.get('is_login')
    # if not session.get('is_login'):
    #     return redirect(url_for('admin_login'))
    return render_template('admin/dashboard/dashboard.html', module=module)
