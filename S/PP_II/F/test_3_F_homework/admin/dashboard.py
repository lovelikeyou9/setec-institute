
from admin import admin_bp
from flask import render_template, request, make_response
# from .auth import login_required
from admin.auth import login_required

@admin_bp.get('/dashboard')
@login_required
def dashboard():
    module = 'dashboard'
    # assert False, session.get('is_login')
    # if not session.get('is_login'):
    #     return redirect(url_for('admin_login'))
    return render_template('admin/dashboard/dashboard.html', module=module)
