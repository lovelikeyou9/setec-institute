
### Final Project MA_II - SV2.6

Group Member:
- Chou Yamata (did all job)




### 1

.\mvnw.cmd spring-boot:run



### 2

Seeded accounts (login with **email** + password):

| Role | Email | Username | Password |
|------|--------|----------|----------|
| **Admin** | `admin@mmt.store` | `mmt_admin` | `admin123` |
| **Customer** | `customer@mmt.store` | `mmt_customer` | `customer123` |

Use the **email** on the login screen (not the username).




### 3
-- =============================
CREATE DATABASE final_ma_ii_computer_store;

SHOW DATABASES;

SHOW TABLES;

use final_ma_ii_computer_store;

select * from audit_logs;
select * from brands;
select * from cart_items;
select * from categories;
select * from order_items;
select * from orders;
select * from products;
select * from users;
select * from wishlist_items;
-- =============================
