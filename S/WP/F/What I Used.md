**KhmerStore — what it uses only**

### Architecture
- Monolith
- MVC (Model–View–Controller)
- Server-Side Rendering (Blade)
- Service layer (Controllers + Services)
- Multi-vendor marketplace
- Role-Based Access Control (RBAC)
- Escrow-style order workflow

### Auth & security
- Session-based authentication
- Cookie sessions
- `Auth::attempt()` / `Auth::login()` / logout
- Remember me (`remember_token`)
- bcrypt password hashing (`BCRYPT_ROUNDS=12`)
- CSRF protection (`@csrf`)
- Middleware: `auth`, `guest`, `role`
- Roles: `ADMIN`, `SELLER`, `CUSTOMER`
- Account enabled/disabled check

### Backend
- PHP 8.3+
- Laravel 13
- Eloquent ORM
- MySQL (`final_wp_mmt`)
- Blade templates (~64 views)
- Routes: `web.php` only
- Custom middleware: `RoleMiddleware`
- Services: `OrderService`, `CartService`, `AdminCountryService`, `ProductVariantOptions`
- Support: `CountryResolver`
- Custom cast: `MysqlBit`
- Helpers: `media_url()`, `default_product_image()`, `product_gallery_urls()`
- DB transactions (`DB::transaction`)
- Pagination (`paginate`)
- Eloquent relations (HasOne, HasMany, BelongsTo)
- Local file storage / uploads
- File session driver
- File cache driver
- Sync queue driver
- Log mailer
- Health check route `/up`
- Laravel encryption key (`APP_KEY`)

### Models
- User, Seller, Product, Category, Brand
- Order, OrderItem, CartItem, WishlistItem
- Address, Review, Notification, Commission, SellerFollow

### Controllers / areas
- Auth (login, register, logout)
- Storefront (home, products, brands, categories, sellers, deals, become-seller)
- Cart / checkout / wishlist
- Customer account (orders, addresses, settings, notifications)
- Seller dashboard (products, orders, earnings, reviews, settings)
- Admin (users, sellers, products, categories, brands, orders, commissions, countries, reviews)

### Frontend
- HTML + custom CSS
- Vanilla JavaScript
- Blade layouts / partials / components
- Vite 8
- Tailwind CSS 4 (build tooling)
- laravel-vite-plugin
- Chart.js 4.4.1 (CDN — admin charts)
- Google Fonts (Plus Jakarta Sans, Sora)
- CSS: `storefront.css`, `dashboard.css`, `form.css`, `table.css`, `template.css`, `khmerstore.css`
- JS: `storefront.js`, `product-form.js`

### Payments (UI / DB labels only)
- `CREDIT_CARD`, `VISA`, `STRIPE`, `QR_PAYMENT`, `CASH_ON_DELIVERY`

### Dev / tooling
- Composer
- npm
- Artisan
- Laravel Tinker
- PHPUnit
- Laravel Pint
- Laravel Pail
- Laravel Pao
- Faker
- Mockery
- Collision
- Concurrently
- `.env` configuration
- XAMPP / local PHP server

### Order / marketplace features used
- Cart, wishlist, checkout
- Product variants / options / gallery
- Reviews, seller follow
- Notifications
- Addresses
- Seller applications / approve / ban
- Admin hold / deliver / payout / refund
- Commissions
- Country analytics

That’s the full **“used only”** list — nothing architectural left out.
