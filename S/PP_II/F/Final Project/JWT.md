# How to test JWT with Postman (Final Project)

Base URL examples:
- Local: `http://127.0.0.1:5000`
- Live: `https://chouyamta.store`

Use an admin/user that already exists in your DB (same account as `/admin/login`).

---

## Step 1 — Public check (no token)

| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/health` |

Expect **200** JSON: `{ "status": "ok", "app": "VoltEdge", ... }`

Also public (no token):
- `GET /api/products`
- `GET /api/products/1`

---

## Step 2 — Login → get JWT

| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/login` |
| **Headers** | `Content-Type: application/json` |
| **Body** | raw → JSON |

```json
{
  "email": "your@email.com",
  "password": "your-password"
}
```

Or with username:

```json
{
  "username": "admin",
  "password": "your-password"
}
```

Expect **200**:

```json
{
  "access_token": "eyJ...",
  "token_type": "Bearer",
  "expires_in": 86400,
  "user": { "id": 1, "username": "...", "email": "...", "role": "..." }
}
```

Copy `access_token`.

---

## Step 3 — Protected route with Bearer token

| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/me` |
| **Auth** | Authorization → **Bearer Token** → paste `access_token` |

Or header:

```text
Authorization: Bearer eyJ...
```

Expect **200** with your user.

Same token for:
- `GET /api/user` (JWT required — user list)

---

## Step 4 — Teacher demo

| Request | Expect |
|---------|--------|
| `/api/me` **with** token | **200** |
| `/api/me` **without** token | **401** |
| `/api/me` with fake token | **401** |
| `/api/products` without token | **200** (public) |

---

## Optional — save token in Postman

On login request → **Tests**:

```js
const data = pm.response.json();
pm.environment.set("token", data.access_token);
```

Other requests: Bearer Token = `{{token}}`
