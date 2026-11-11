**`MA_II\F\Final` — what it uses only**

This is a **2-part client–server app**: Flutter mobile + Spring Boot JWT API (MMT computer store). Not the multi-seller escrow marketplace.

---

## Overall architecture
- Client–server
- Mobile app + REST backend
- Monolith backend
- API-first (JSON REST)
- Layered backend (Controller → Service → Repository → Entity)
- DTO + Mapper pattern
- RBAC (`ADMIN` / `CUSTOMER`)

---

## Backend — Spring Boot (`spring boot/demo`)

### Stack
- Java 21
- Spring Boot 4.1.1
- Maven
- Spring Web MVC (`@RestController`)
- Spring Data JPA / Hibernate
- Spring Security
- Spring Validation (`@Valid`)
- JWT (jjwt 0.12.6)
- MySQL (`final_ma_ii_computer_store`)
- Lombok
- Multipart uploads (10MB)
- Local `uploads/` folder
- CORS (`*` origins)
- Bean Validation
- JPA `Specification` (product filters)
- Data seeder
- Global exception handler
- DevTools
- H2 dependency present (config uses MySQL)

### Auth & security
- JWT only (stateless API)
- Bearer token
- BCrypt passwords
- CSRF disabled (API)
- `JwtAuthFilter` / `JwtUtil`
- Method security (`@EnableMethodSecurity`)
- Role rules on `/api/admin/**`, product/brand/category write = ADMIN

### API areas
- `/api/auth` — register, login, me  
- `/api/products`, `/api/categories`, `/api/brands`  
- `/api/cart`, `/api/wishlist`, `/api/orders`  
- `/api/admin` — stats, orders status, stock, audit logs  
- `/api/admin/customers` — CRUD, ban/unban, activity  
- `/api/admin/uploads` — image upload  

### Entities
- User, Product, Category, Brand  
- CartItem, WishlistItem  
- OrderEntity, OrderItem  
- AuditLog  
- Enums: Role, OrderStatus (`PENDING` → `PAID` → `PROCESSING` → `SHIPPED` → `DELIVERED` / `CANCELLED`), AuditLevel  

### Features
- Catalog with filters  
- Cart / wishlist / place order  
- Admin CRUD (products, brands, categories, customers)  
- Stock update, order status update  
- Customer ban / audit logs  
- File upload for product images  

Server: **port 8080** · app name **mmt**

*(Thymeleaf is in `pom.xml` but templates are empty — UI is Flutter, not SSR.)*

---

## Frontend — Flutter (`flutter`)

### Stack
- Flutter / Dart SDK ^3.12
- Material Design
- Provider (state management)
- http (REST client)
- shared_preferences (save JWT)
- google_fonts
- cached_network_image
- image_picker
- intl (formatting)
- cupertino_icons
- flutter_lints / flutter_test

### App structure
- Screens: store home, catalog, product detail, cart, wishlist, profile, login/register, admin dashboard/tabs  
- Providers: Auth, Product, Cart, Wishlist  
- Services: ApiClient, Auth, Product, Cart, Wishlist, Order, Admin, Upload  
- Models + widgets + theme  

### Client behavior
- Calls Spring API at `http://localhost:8080/api` (Android emulator: `10.0.2.2`)
- Stores JWT in SharedPreferences (`mmt_jwt`)
- Sends `Authorization: Bearer <token>`
- Multipart image upload for admin
- Cross-platform targets: Android, iOS, web, Windows, Linux, macOS

---

## What this project is (short)
**Flutter mobile store + Spring Boot JWT REST API + MySQL**  
Admin/customer roles · cart/wishlist/orders · admin CRUD · **no seller marketplace, no Stripe, no Telegram, no session web UI**.