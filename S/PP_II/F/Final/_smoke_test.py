"""Quick smoke test against running Flask app."""
import re
import sys

import requests

BASE = "http://127.0.0.1:5000"


def csrf(session):
    r = session.get(f"{BASE}/login")
    m = re.search(r'name="csrf_token" value="([^"]+)"', r.text)
    return m.group(1) if m else ""


def main():
    ok = True

    # Admin login
    s = requests.Session()
    token = csrf(s)
    r = s.post(
        f"{BASE}/login",
        data={"email": "admin@mmt.local", "password": "Admin123!", "csrf_token": token},
        allow_redirects=False,
    )
    print("admin_login", r.status_code, r.headers.get("Location"))
    ok &= r.status_code in (302, 303)
    r = s.get(f"{BASE}/admin")
    print("admin_dash", r.status_code, "Admin" in r.text or "Dashboard" in r.text)
    ok &= r.status_code == 200

    # Seller login
    s2 = requests.Session()
    token = csrf(s2)
    r = s2.post(
        f"{BASE}/login",
        data={"email": "seller@mmt.local", "password": "Seller123!", "csrf_token": token},
        allow_redirects=False,
    )
    print("seller_login", r.status_code, r.headers.get("Location"))
    ok &= r.status_code in (302, 303)
    r = s2.get(f"{BASE}/seller-dashboard")
    print("seller_dash", r.status_code, len(r.text))
    ok &= r.status_code == 200

    # JWT API
    r = requests.post(
        f"{BASE}/api/login",
        json={"email": "customer@mmt.local", "password": "Customer123!"},
    )
    print("jwt_login", r.status_code, list(r.json().keys()) if r.ok else r.text[:200])
    ok &= r.status_code == 200
    tok = r.json().get("access_token") if r.ok else None
    if tok:
        r = requests.get(f"{BASE}/api/me", headers={"Authorization": f"Bearer {tok}"})
        print("api_me", r.status_code, r.json())
        ok &= r.status_code == 200

    # 404 real page
    r = requests.get(
        f"{BASE}/sadsamdas",
        headers={
            "Sec-Fetch-Dest": "document",
            "Sec-Fetch-Mode": "navigate",
            "Accept": "text/html",
        },
    )
    print("404", r.status_code)
    ok &= r.status_code == 404

    # Home / products
    print("home", requests.get(BASE + "/").status_code)
    print("products", requests.get(BASE + "/products").status_code)

    print("RESULT", "PASS" if ok else "FAIL")
    return 0 if ok else 1


if __name__ == "__main__":
    sys.exit(main())
