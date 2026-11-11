# Database Setup for Premium Car Dealer Project

This project **already has database code**.

It supports two database modes:

1. **MySQL database** — real database connection using `mysql2`.
2. **Local JSON fallback** — if MySQL is not running or the credentials are wrong, the app automatically uses `database/local_db.json`.

## Files related to database

- `.env` — database configuration
- `db.js` — database connection and all database functions
- `database/schema.sql` — MySQL table structure
- `database/local_db.json` — fallback local database
- `server.js` — starts the database before starting the server

## Step 1: Create the MySQL database

Open MySQL / XAMPP / phpMyAdmin and create this database:

```sql
CREATE DATABASE car_dealer_db;
```

You can also import this file:

```text
database/schema.sql
```

The project can also create the database and tables automatically when it starts, if your MySQL user has permission.

## Step 2: Check `.env`

Open `.env` and make sure it matches your MySQL setup:

```env
PORT=3000
DB_HOST=localhost
DB_USER=root
DB_PASSWORD=
DB_NAME=car_dealer_db
JWT_SECRET=premium-car-dealer-token-secret-key-2026
```

For XAMPP default MySQL, usually:

```env
DB_USER=root
DB_PASSWORD=
```

If your MySQL has a password, put it here:

```env
DB_PASSWORD=your_mysql_password
```

## Step 3: Install packages

In the project folder, run:

```bash
npm install
```

## Step 4: Start MySQL

Start MySQL from XAMPP, Laragon, WAMP, or MySQL Server.

## Step 5: Start the project

```bash
npm start
```

or

```bash
node server.js
```

## Step 6: Check if database is connected

Open this URL in your browser:

```text
http://localhost:3000/api/db-status
```

If MySQL is connected, you will see:

```json
{
  "connected": true,
  "mode": "MySQL",
  "message": "Connected to MySQL database."
}
```

If MySQL is not connected, you will see:

```json
{
  "connected": true,
  "mode": "Local JSON File",
  "message": "Using local JSON database fallback. Start MySQL and check .env to use real MySQL."
}
```

## Default login accounts

The project seeds these default accounts:

| Role | Email | Password |
|---|---|---|
| Admin | admin@dealer.com | admin123 |
| Staff | staff@dealer.com | staff123 |
| Support | support@dealer.com | support123 |
| Seller | seller@dealer.com | seller123 |
| Buyer | buyer@dealer.com | buyer123 |

## Important note

If the website works even when MySQL is off, that means it is using the JSON fallback database, not MySQL.

To force real database usage, make sure MySQL is running and `.env` is correct.
