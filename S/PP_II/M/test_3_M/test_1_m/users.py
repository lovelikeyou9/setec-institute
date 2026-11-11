import json
import os
from datetime import datetime, timezone

from werkzeug.security import check_password_hash, generate_password_hash

DATA_DIR = os.path.join(os.path.dirname(__file__), 'data')
USERS_FILE = os.path.join(DATA_DIR, 'users.json')

DEFAULT_ADMIN = {
    'id': 1,
    'first_name': 'Admin',
    'last_name': 'User',
    'email': 'admin@voltedge.com',
    'password': 'admin123',
    'role': 'admin',
    'status': 'active',
}


def _ensure_data_dir():
    os.makedirs(DATA_DIR, exist_ok=True)


def _load_users():
    _ensure_data_dir()
    if not os.path.exists(USERS_FILE):
        _seed_default_admin()
    with open(USERS_FILE, 'r', encoding='utf-8') as f:
        return json.load(f)


def _save_users(users):
    _ensure_data_dir()
    with open(USERS_FILE, 'w', encoding='utf-8') as f:
        json.dump(users, f, indent=2)


def _seed_default_admin():
    users = [{
        'id': DEFAULT_ADMIN['id'],
        'first_name': DEFAULT_ADMIN['first_name'],
        'last_name': DEFAULT_ADMIN['last_name'],
        'email': DEFAULT_ADMIN['email'],
        'password_hash': generate_password_hash(DEFAULT_ADMIN['password']),
        'role': DEFAULT_ADMIN['role'],
        'status': DEFAULT_ADMIN['status'],
        'created_at': datetime.now(timezone.utc).isoformat(),
    }]
    _save_users(users)


def get_all_users():
    return _load_users()


def get_user_by_id(user_id):
    for user in _load_users():
        if user['id'] == int(user_id):
            return user
    return None


def get_user_by_email(email):
    email = email.strip().lower()
    for user in _load_users():
        if user['email'].lower() == email:
            return user
    return None


def create_user(first_name, last_name, email, password, role='customer'):
    users = _load_users()
    email = email.strip().lower()

    if get_user_by_email(email):
        return None, 'Email is already registered.'

    if len(password) < 6:
        return None, 'Password must be at least 6 characters.'

    next_id = max((u['id'] for u in users), default=0) + 1
    user = {
        'id': next_id,
        'first_name': first_name.strip(),
        'last_name': last_name.strip(),
        'email': email,
        'password_hash': generate_password_hash(password),
        'role': role,
        'status': 'active',
        'created_at': datetime.now(timezone.utc).isoformat(),
    }
    users.append(user)
    _save_users(users)
    return user, None


def verify_user(email, password):
    user = get_user_by_email(email)
    if not user:
        return None, 'Invalid email or password.'
    if user.get('status') != 'active':
        return None, 'Your account is inactive. Contact support.'
    if not check_password_hash(user['password_hash'], password):
        return None, 'Invalid email or password.'
    return user, None


def update_user_status(user_id, status):
    users = _load_users()
    for user in users:
        if user['id'] == int(user_id):
            if user['role'] == 'admin':
                return False, 'Cannot change admin account status.'
            user['status'] = status
            _save_users(users)
            return True, None
    return False, 'User not found.'


def delete_user(user_id):
    users = _load_users()
    target = get_user_by_id(user_id)
    if not target:
        return False, 'User not found.'
    if target['role'] == 'admin':
        return False, 'Cannot delete admin account.'
    users = [u for u in users if u['id'] != int(user_id)]
    _save_users(users)
    return True, None


def user_initials(user):
    return (user.get('first_name', '?')[0] + user.get('last_name', '?')[0]).upper()


def user_full_name(user):
    return f"{user.get('first_name', '')} {user.get('last_name', '')}".strip()
