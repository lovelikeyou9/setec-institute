Here’s how to test JWT for your MMT Flask app in Postman.

Make sure Flask is running at `http://127.0.0.1:5000`.

---

### 1. Login — get the token

| | |
|---|---|
| **Method** | `POST` |
| **URL** | `http://127.0.0.1:5000/api/login` |
| **Headers** | `Content-Type: application/json` |
| **Body** | raw → JSON |

```json
{
  "email": "customer@mmt.local",
  "password": "Customer123!"
}
```

You should get **200** with something like:

```json
{
  "access_token": "eyJ...",
  "token_type": "Bearer",
  "expires_in": 86400,
  "user": { ... }
}
```

Copy `access_token`.

---

### 2. Call a protected route with the token

| | |
|---|---|
| **Method** | `GET` |
| **URL** | `http://127.0.0.1:5000/api/me` |
| **Headers** | `Authorization: Bearer eyJ...` (paste your token after `Bearer `) |

Or in Postman: **Authorization** tab → Type **Bearer Token** → paste the token (Postman adds `Bearer` for you).

Expect **200** with your user JSON.

---

### 3. Other useful requests

| Auth | Method | URL |
|------|--------|-----|
| None | `GET` | `/api/health` |
| None | `GET` | `/api/products` |
| None | `GET` | `/api/categories` |
| **JWT** | `GET` | `/api/me` |
| **JWT** | `GET` | `/api/cart` |
| **JWT** | `GET` | `/api/orders` |

---

### Quick checks

- **No token** on `/api/me` → **401**
- **Wrong password** on login → **401**
- **Bad/expired token** → **401**
- API is CSRF-exempt — you do **not** need the HTML `csrf_token` in Postman

Demo accounts: `customer@mmt.local` / `Customer123!`, `admin@mmt.local` / `Admin123!`, `seller@mmt.local` / `Seller123!`