**`demo` (Java) — what it uses only**

Same marketplace idea as KhmerStore, but **Spring Boot + JWT for API**.

---

### Architecture
- Monolith
- MVC
- Layered architecture (Controller → Service → Repository → Model)
- Server-Side Rendering (Thymeleaf)
- Dual auth style: **Session (web)** + **JWT (API)**
- REST API (partial: `/api/auth`, `/api/brands`)
- DTO pattern
- Repository pattern (Spring Data JPA)
- Multi-vendor marketplace
- RBAC (`ADMIN` / `SELLER` / `CUSTOMER`)
- Escrow-style order workflow
- Scheduled jobs (`@EnableScheduling`)

---

### Auth & security
- Spring Security
- Form login (session) for website
- JWT (jjwt 0.12.6) for `/api/**`
- Stateless API sessions (`SessionCreationPolicy.STATELESS`)
- `JwtAuthenticationFilter`, `JwtService`, `JwtUtil`
- BCrypt password hashing
- CSRF for web forms
- Method security (`@EnableMethodSecurity`, `@PreAuthorize`)
- `CustomUserDetailsService`
- Role-based login redirect (`RoleBasedAuthSuccessHandler`)
- Login failure handler
- Dual `SecurityFilterChain` (API + Web)

---

### Backend stack
- Java 21
- Spring Boot 4.1.1
- Maven
- Spring Web MVC
- Spring Data JPA / Hibernate
- Spring JDBC / Data JDBC (in deps)
- MySQL (`comupter_store_final_java_iii`)
- Hibernate `ddl-auto=update`
- Thymeleaf + thymeleaf-extras-springsecurity6
- Lombok
- Multipart file uploads (10MB / 30MB)
- Local uploads folder
- `@Transactional`
- `@Scheduled` escrow auto-release
- Data seeder / catalog sync / metrics sync runners
- Jackson (via JWT + Spring)

---

### API
- `POST /api/auth/login` → JWT
- `/api/brands` CRUD (GET public, write = ADMIN)
- DTOs: `AuthRequest`, `AuthResponse`, `BrandRequest`, `BrandResponse`, …

---

### Models / enums
- Entities: User, Seller, Product, Category, Brand, Order, OrderItem, CartItem, WishlistItem, Address, Review, Notification, Commission, SellerFollow  
- Enums: Role, SellerStatus, SellerLevel, OrderStatus, PaymentMethod, DeliveryType, DisputeReason, PayoutStatus, ProductCondition

---

### Services (used)
- User, Seller, Product, Brand, Category, Cart, Wishlist, Order  
- Review, Notification, SellerFollow, FileStorage  
- Dashboard, AdminProfile, PublicProfile, CatalogNav  
- CountryResolver, ProductVariantOptions

---

### Controllers / areas
- Auth, Home, Account, Cart/Checkout, Profile  
- Seller dashboard, Admin  
- API: Auth + Brands  
- GlobalControllerAdvice

---

### Frontend
- Thymeleaf HTML (~64 templates)
- Custom CSS (`main.css`)
- Vanilla JS (`main.js` + `fetch`)
- Chart.js 4.4.1 (CDN)
- Google Fonts (Plus Jakarta Sans, Sora)
- Layouts / fragments

---

### Payments (labels only)
- `CREDIT_CARD`, `VISA`, `STRIPE`, `QR_PAYMENT`, `CASH_ON_DELIVERY`

---

### App features
- Storefront, cart, wishlist, checkout  
- Seller center, admin panel  
- Escrow hold / buyer protection / auto payout release  
- Commissions (default 3%)  
- Reviews, follows, notifications, addresses  
- Product variants / gallery / uploads  
- Country analytics  
- Search suggest endpoint

---

### Tooling
- Maven Wrapper (`mvnw`)
- Spring Boot DevTools
- Spring Boot test starters
- IntelliJ (`.idea`)
- Server port **8080**
- App name **MME**

---

**One line:** Spring Boot monolith MVC marketplace · Thymeleaf SSR · session form login for pages · JWT for REST API · BCrypt · Spring Security RBAC · JPA + MySQL · scheduled escrow.