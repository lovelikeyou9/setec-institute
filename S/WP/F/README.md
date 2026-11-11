### Final Project WP - SV2.6

Group Member:
- Chou Yamata (did all job)




### 1
Command run

cmd
|--------|
| php artisan serve |



### 2
Demo accounts from the seed data (same as the Java project):

### Admin
| Email | Password |
|--------|----------|
| `admin@computerhub.com` | `admin123` |

### Sellers
| Email | Password | Store |
|--------|----------|--------|
| `techzone@computerhub.com` | `seller123` | TechZone Cambodia |
| `pcmaster@computerhub.com` | `seller123` | PC Master |
| `digitech@computerhub.com` | `seller123` | DigiTech Store |

### Customers
| Email | Password |
|--------|----------|
| `customer@computerhub.com` | `customer123` |
| `rithy@computerhub.com` | `customer123` |
| `chenda@computerhub.com` | `customer123` |
| `piseth@computerhub.com` | `customer123` |
| `sothea@computerhub.com` | `customer123` |
| `bopha@computerhub.com` | `customer123` |

Login: **http://127.0.0.1:8000/login**




### 3
-- =============================
CREATE DATABASE final_wp_mmt;

SHOW DATABASES;

-- drop database final_wp_mmt;

SHOW TABLES;

use final_wp_mmt;

select * from addresses;
select * from brands;
select * from cart_items;
select * from categories;
select * from commissions;
select * from notifications;
select * from order_items;
select * from orders;
select * from products;
select * from reviews;
select * from seller_follows;
select * from sellers;
select * from users;
select * from wishlist_items;
-- =============================
