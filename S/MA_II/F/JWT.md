Here’s how to test your **JWT** in Postman (your app’s own token, not Keycloak).

### 1. Start Spring Boot
API base: `http://localhost:8080`

---

### 2. Get a token (login)

**POST** `http://localhost:8080/api/auth/login`  
Headers: `Content-Type: application/json`  
Body → raw → JSON:

```json
{
  "email": "admin@mmt.store",
  "password": "admin123"
}
```

(or `customer@mmt.store` / `customer123`)

Response includes something like:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "user": { ... }
}
```

Copy the `token` value.

---

### 3. Call a protected API with the token

Example: **GET** `http://localhost:8080/api/auth/me`

In Postman → **Authorization** tab:
- Type: **Bearer Token**
- Token: paste the JWT (no `Bearer ` prefix — Postman adds it)

Or in **Headers**:

| Key | Value |
|-----|--------|
| Authorization | `Bearer eyJhbGciOiJIUzI1NiIs...` |

Send → you should get your user profile (**200**).

---

### 4. Quick checks

| Test | Request | Expected |
|------|---------|----------|
| No token | GET `/api/auth/me` | **401** |
| With customer token | GET `/api/admin/stats` | **403** |
| With admin token | GET `/api/admin/stats` | **200** |
| Public (no token) | GET `/api/products` | **200** |
| With any token | GET `/api/cart` | **200** |

---

### 5. Optional: save token in Postman

After login, in **Tests** tab:

```javascript
const data = pm.response.json();
pm.environment.set("jwt", data.token);
```

Then use Authorization → Bearer Token → `{{jwt}}`.

---

**Tip:** Token expires (your config uses ~24h). If you get 401 again, login again and use the new token.