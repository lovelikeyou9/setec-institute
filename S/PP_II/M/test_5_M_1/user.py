import json
import os

from werkzeug.security import generate_password_hash

DATA_DIR = os.path.join(os.path.dirname(__file__), 'data')
USERS_FILE = os.path.join(DATA_DIR, 'users.json')

DEFAULT_USERS = [
    {
        'id': 1,
        'name': 'dara',
        'email': 'dara@example.com',
        'password': generate_password_hash('123456'),
        'role': 'user',
        'status': 'active',
        'profile': 'admin/avatar.png',
    }
]


def _ensure_data_file():
    os.makedirs(DATA_DIR, exist_ok=True)
    if not os.path.exists(USERS_FILE):
        with open(USERS_FILE, 'w', encoding='utf-8') as f:
            json.dump(DEFAULT_USERS, f, indent=2)


def _load_users():
    _ensure_data_file()
    with open(USERS_FILE, 'r', encoding='utf-8') as f:
        return json.load(f)


def _save_users(users):
    _ensure_data_file()
    with open(USERS_FILE, 'w', encoding='utf-8') as f:
        json.dump(users, f, indent=2)


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


def add_user(name, email, password, role='user', status='active'):
    users = _load_users()
    email = email.strip().lower()

    if not name.strip():
        return None, 'Name is required.'
    if not email:
        return None, 'Email is required.'
    if get_user_by_email(email):
        return None, 'Email already exists.'
    if len(password) < 6:
        return None, 'Password must be at least 6 characters.'

    next_id = max((user['id'] for user in users), default=0) + 1
    user = {
        'id': next_id,
        'name': name.strip(),
        'email': email,
        'password': generate_password_hash(password),
        'role': role,
        'status': status,
        'profile': 'admin/avatar.png',
    }
    users.append(user)
    _save_users(users)
    return user, None


def update_user(user_id, name, email, password=None, role=None, status=None):
    users = _load_users()
    email = email.strip().lower()
    target = None

    for user in users:
        if user['id'] == int(user_id):
            target = user
            break

    if not target:
        return None, 'User not found.'

    if not name.strip():
        return None, 'Name is required.'
    if not email:
        return None, 'Email is required.'

    existing = get_user_by_email(email)
    if existing and existing['id'] != int(user_id):
        return None, 'Email already exists.'

    target['name'] = name.strip()
    target['email'] = email
    if password:
        if len(password) < 6:
            return None, 'Password must be at least 6 characters.'
        target['password'] = generate_password_hash(password)
    if role:
        target['role'] = role
    if status:
        target['status'] = status

    _save_users(users)
    return target, None


def delete_user(user_id):
    users = _load_users()
    target = get_user_by_id(user_id)

    if not target:
        return False, 'User not found.'

    users = [user for user in users if user['id'] != int(user_id)]
    _save_users(users)
    return True, None
