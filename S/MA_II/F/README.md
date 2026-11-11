# MMT — Modern Multiple Technology (MA II Final)

Mobile tech store for teacher **MA**. Brand: **Modern Multiple Technology (MMT)**.

| Layer | Stack | Path |
|--------|--------|------|
| Frontend | Flutter + Dart | `flutter/` |
| Backend | Spring Boot + Java 21 | `spring boot/demo/` |
| Database | MySQL 8 | `final_ma_ii_computer_store` |
| Images | Local files | `spring boot/demo/uploads/` |

## Demo accounts

| Role | Email | Password |
|------|--------|----------|
| Admin | `admin@mmt.store` | `admin123` |
| Customer | `customer@mmt.store` | `customer123` |

Discount code: **`CYBER2025`** (−$50). Free shipping when subtotal ≥ $500. Tax 7%.

## Categories

Laptops & PCs · Components · Monitors & TVs · Networking · Drones & Cameras · Audio & Streaming · Gaming & Consoles · Storage & NAS · Accessories · Gift Cards

Product photos are served from `uploads/products/` at `http://localhost:8080/uploads/...`.

## 1. Start MySQL

Database `final_ma_ii_computer_store` must exist. Credentials are in `spring boot/demo/src/main/resources/application.yml`.

## 2. Start Spring Boot

```powershell
cd "C:\Users\Admin\OneDrive\Desktop\S\MA_II\F\Final\spring boot\demo"
.\mvnw.cmd spring-boot:run
```

API: `http://localhost:8080/api`  
Images: `http://localhost:8080/uploads/products/...`

Run the backend from the `demo` folder so the relative `uploads/` path resolves correctly.

## 3. Run Flutter

```powershell
cd "C:\Users\Admin\OneDrive\Desktop\S\MA_II\F\Final\flutter"
flutter pub get
flutter run
```

- **Android emulator** → `http://10.0.2.2:8080/api`
- **Windows / Chrome** → `http://localhost:8080/api`
- **Physical phone** → set PC IP in `lib/config/api_config.dart`:

```dart
ApiConfig.overrideBaseUrl = 'http://192.168.x.x:8080/api';
```
