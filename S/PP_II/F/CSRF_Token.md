CSRF protects **website forms** (`POST /login`, `/cart/add`, …), **not** `/api/*` (API is CSRF-exempt — that’s JWT).

### Test A — Without CSRF (should fail)

| | |
|--|--|
| Method | `POST` |
| URL | `http://127.0.0.1:5000/login` |
| Body | `x-www-form-urlencoded` |

| Key | Value |
|-----|--------|
| email | `customer@mmt.local` |
| password | `Customer123!` |

**Do not** send `csrf_token`.

**Expected:** CSRF reject — redirect / flash, or **400** if you send `Accept: application/json`.

---

### Test B — With CSRF (should work)

**Step 1 — Get token + cookie**

| | |
|--|--|
| Method | `GET` |
| URL | `http://127.0.0.1:5000/login` |

In response:

1. **Cookies** → copy the `session` cookie (Postman can keep it in the same collection if “Send cookies” is on).
2. In **Body** HTML, find:

```html
<input type="hidden" name="csrf_token" value="....">
```

Copy that `value`.

**Step 2 — Login with token**

| | |
|--|--|
| Method | `POST` |
| URL | `http://127.0.0.1:5000/login` |
| Body | `x-www-form-urlencoded` |

| Key | Value |
|-----|--------|
| email | `customer@mmt.local` |
| password | `Customer123!` |
| csrf_token | *(paste from Step 1)* |

Cookie `session` from Step 1 must be sent too (same Postman tab/collection).

**Expected:** **302** → `/account` (login OK).

---

### Easy demo for teacher

1. Postman `POST /login` **without** `csrf_token` → blocked  
2. Same request **with** `csrf_token` + session cookie → success  
3. Optional: `POST /api/login` with JSON **without** CSRF → still works (API uses JWT, not CSRF)

---

### Tip

In Postman, turn on **Cookies** for `127.0.0.1`. CSRF token and session cookie must match the same session — copy a fresh token after each `GET /login`.