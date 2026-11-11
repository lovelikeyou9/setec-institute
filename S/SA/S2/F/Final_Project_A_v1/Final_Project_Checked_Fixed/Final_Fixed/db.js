// Hybrid Database Adapter - MySQL with seamless JSON file fallback
const mysql = require('mysql2/promise');
const fs = require('fs');
const path = require('path');
const bcrypt = require('bcryptjs');
require('dotenv').config();
const dbFolder = path.join(__dirname, 'database');
if (!fs.existsSync(dbFolder)) {
    fs.mkdirSync(dbFolder, { recursive: true });
}
const jsonDbPath = path.join(dbFolder, 'local_db.json');
let isMySQL = false;
let pool = null;
// Mock database format
const initialJsonSchema = {
    users: [],
    cars: [],
    cart: [],
    bookings: [],
    orders: [],
    tickets: [],
    ticket_messages: []
};
// Simple synchronous helpers for JSON DB
function readJsonDb() {
    if (!fs.existsSync(jsonDbPath)) {
        fs.writeFileSync(jsonDbPath, JSON.stringify(initialJsonSchema, null, 2), 'utf8');
        return initialJsonSchema;
    }
    try {
        const data = fs.readFileSync(jsonDbPath, 'utf8');
        return JSON.parse(data);
    } catch (err) {
        console.error('Error reading JSON DB, resetting...', err);
        return initialJsonSchema;
    }
}
function writeJsonDb(data) {
    fs.writeFileSync(jsonDbPath, JSON.stringify(data, null, 2), 'utf8');
}
// -------------------------------------------------------------
// Database Connection & Initialization
// -------------------------------------------------------------
async function initializeDatabase() {
    // Try to connect to MySQL
    try {
        console.log('Attempting to connect to MySQL at', process.env.DB_HOST || 'localhost');

        // First, connect without DB to create database if it doesn't exist
        const connection = await mysql.createConnection({
            host: process.env.DB_HOST || 'localhost',
            user: process.env.DB_USER || 'root',
            password: process.env.DB_PASSWORD !== undefined ? process.env.DB_PASSWORD : ''
        });
        const dbName = process.env.DB_NAME || 'car_dealer_db';
        await connection.query(`CREATE DATABASE IF NOT EXISTS \`${dbName}\``);
        await connection.end();
        // Now connect with connection pool
        pool = mysql.createPool({
            host: process.env.DB_HOST || 'localhost',
            user: process.env.DB_USER || 'root',
            password: process.env.DB_PASSWORD !== undefined ? process.env.DB_PASSWORD : '',
            database: dbName,
            waitForConnections: true,
            connectionLimit: 10,
            queueLimit: 0
        });
        // Create Tables
        await createMySQLTables();
        isMySQL = true;
        console.log(`\x1b[32m[DATABASE] Successfully connected to MySQL database: "${dbName}"\x1b[0m`);
    } catch (err) {
        console.warn(`\x1b[33m[DATABASE] MySQL connection failed. Error: ${err.message}\x1b[0m`);
        console.log(`\x1b[36m[DATABASE] Falling back to zero-config local JSON Database at: ${jsonDbPath}\x1b[0m`);
        isMySQL = false;
        // Initialise JSON db
        readJsonDb();
    }
    // Seed default data if database is empty
    await seedDefaultData();
}
async function createMySQLTables() {
    const queries = [
        `CREATE TABLE IF NOT EXISTS users (
      id INT AUTO_INCREMENT PRIMARY KEY,
      name VARCHAR(100) NOT NULL,
      email VARCHAR(100) UNIQUE NOT NULL,
      password VARCHAR(255) NOT NULL,
      role ENUM('admin', 'staff', 'support', 'seller', 'buyer') DEFAULT 'buyer',
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4`,
        `CREATE TABLE IF NOT EXISTS cars (
      id INT AUTO_INCREMENT PRIMARY KEY,
      make VARCHAR(50) NOT NULL,
      model VARCHAR(50) NOT NULL,
      year INT NOT NULL,
      price DECIMAL(12, 2) NOT NULL,
      mileage INT NOT NULL,
      transmission VARCHAR(30) NOT NULL,
      fuel_type VARCHAR(30) NOT NULL,
      body_type VARCHAR(30) NOT NULL,
      \`condition\` ENUM('New', 'Used') DEFAULT 'Used',
      description TEXT,
      image_url VARCHAR(255) DEFAULT NULL,
      seller_id INT NOT NULL,
      status ENUM('Available', 'Reserved', 'Sold') DEFAULT 'Available',
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4`,
        `CREATE TABLE IF NOT EXISTS cart (
      id INT AUTO_INCREMENT PRIMARY KEY,
      user_id INT NOT NULL,
      car_id INT NOT NULL,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
      FOREIGN KEY (car_id) REFERENCES cars(id) ON DELETE CASCADE,
      UNIQUE KEY unique_user_car (user_id, car_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4`,
        `CREATE TABLE IF NOT EXISTS bookings (
      id INT AUTO_INCREMENT PRIMARY KEY,
      car_id INT NOT NULL,
      buyer_id INT NOT NULL,
      booking_date DATETIME NOT NULL,
      type ENUM('Test Drive', 'Reservation') NOT NULL DEFAULT 'Test Drive',
      status ENUM('Pending', 'Approved', 'Completed', 'Cancelled') DEFAULT 'Pending',
      notes TEXT,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      FOREIGN KEY (car_id) REFERENCES cars(id) ON DELETE CASCADE,
      FOREIGN KEY (buyer_id) REFERENCES users(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4`,
        `CREATE TABLE IF NOT EXISTS orders (
      id INT AUTO_INCREMENT PRIMARY KEY,
      car_id INT NOT NULL,
      buyer_id INT NOT NULL,
      seller_id INT NOT NULL,
      price_paid DECIMAL(12, 2) NOT NULL,
      payment_method VARCHAR(50) NOT NULL,
      status ENUM('Paid', 'Completed') DEFAULT 'Paid',
      shipping_address TEXT NOT NULL,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      FOREIGN KEY (car_id) REFERENCES cars(id) ON DELETE CASCADE,
      FOREIGN KEY (buyer_id) REFERENCES users(id) ON DELETE CASCADE,
      FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4`,
        `CREATE TABLE IF NOT EXISTS tickets (
      id INT AUTO_INCREMENT PRIMARY KEY,
      buyer_id INT NOT NULL,
      subject VARCHAR(255) NOT NULL,
      status ENUM('Open', 'In Progress', 'Resolved') DEFAULT 'Open',
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      FOREIGN KEY (buyer_id) REFERENCES users(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4`,
        `CREATE TABLE IF NOT EXISTS ticket_messages (
      id INT AUTO_INCREMENT PRIMARY KEY,
      ticket_id INT NOT NULL,
      sender_id INT NOT NULL,
      message TEXT NOT NULL,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE,
      FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4`
    ];
    for (const query of queries) {
        await pool.query(query);
    }
}
// -------------------------------------------------------------
// Seeding Default Accounts and Premium Cars
// -------------------------------------------------------------
async function seedDefaultData() {
    const defaultUsers = [
        { name: 'System Admin', email: 'admin@dealer.com', role: 'admin', password: 'admin123' },
        { name: 'Showroom Staff', email: 'staff@dealer.com', role: 'staff', password: 'staff123' },
        { name: 'Help Support', email: 'support@dealer.com', role: 'support', password: 'support123' },
        { name: 'Elite Seller', email: 'seller@dealer.com', role: 'seller', password: 'seller123' },
        { name: 'Alex Buyer', email: 'buyer@dealer.com', role: 'buyer', password: 'buyer123' }
    ];
    const defaultCars = [
        { make: 'Tesla', model: 'Model S Plaid', year: 2024, price: 89990.00, mileage: 1200, transmission: 'Automatic', fuel_type: 'Electric', body_type: 'Sedan', condition: 'New', description: 'Triple motor AWD, 1020 horsepower. Ultimate electric performance sedan. Carbon fiber spoiler, yolk steering, premium audio.', image_url: '/uploads/tesla_s.svg' },
        { make: 'Porsche', model: '911 Carrera S', year: 2023, price: 131000.00, mileage: 4500, transmission: 'Automatic', fuel_type: 'Petrol', body_type: 'Coupe', condition: 'Used', description: 'Twin-turbo flat-six, 443 hp, beautiful chalk grey finish, Sport Chrono package, active suspension, leather-lined sports seats.', image_url: '/uploads/porsche_911.svg' },
        { make: 'Ford', model: 'Mustang Shelby GT500', year: 2022, price: 79500.00, mileage: 8200, transmission: 'Automatic', fuel_type: 'Petrol', body_type: 'Coupe', condition: 'Used', description: 'Supercharged 5.2L V8 producing 760 hp. Rapid Red metallic, Recaro racing seats, exposed carbon fiber wheels, absolute muscle beast.', image_url: '/uploads/mustang.svg' },
        { make: 'Audi', model: 'e-tron GT RS', year: 2024, price: 147500.00, mileage: 500, transmission: 'Automatic', fuel_type: 'Electric', body_type: 'Sedan', condition: 'New', description: 'Premium electric grand tourer. Kemora Gray paint, Matrix LED headlights, Quattro AWD, Bang & Olufsen high-fidelity acoustic surround.', image_url: '/uploads/audi_etron.svg' },
        { make: 'Land Rover', model: 'Range Rover Sport', year: 2023, price: 95000.00, mileage: 15000, transmission: 'Automatic', fuel_type: 'Hybrid', body_type: 'SUV', condition: 'Used', description: 'Mild-hybrid inline-6. Santorini Black with ebony luxury leather, air suspension, panoramic roof, Meridian 3D sound system.', image_url: '/uploads/range_rover.svg' }
    ];
    // 1. Seed/repair default users
    // Old bug: this only seeded accounts when the users table was completely empty.
    // If one buyer/seller already existed in MySQL, the admin account was never created.
    console.log('[DATABASE] Checking default system accounts...');
    for (const u of defaultUsers) {
        const existingUser = await db.users.getByEmail(u.email);
        if (!existingUser) {
            const hashedPassword = await bcrypt.hash(u.password, 10);
            await db.users.create(u.name, u.email, hashedPassword, u.role);
            console.log(`[DATABASE] Created default ${u.role}: ${u.email}`);
        }
    }
    // Get a seller ID for seeding cars
    let sellerId = 4; // Default to 4 in JSON
    if (isMySQL) {
        const [rows] = await pool.query("SELECT id FROM users WHERE role = 'seller' LIMIT 1");
        if (rows.length > 0) sellerId = rows[0].id;
    }
    // 2. Seed Cars
    let carsCount = 0;
    if (isMySQL) {
        const [rows] = await pool.query('SELECT COUNT(*) as count FROM cars');
        carsCount = rows[0].count;
    } else {
        const db = readJsonDb();
        carsCount = db.cars.length;
    }
    if (carsCount === 0) {
        console.log('[DATABASE] Seeding premium showroom cars...');
        if (isMySQL) {
            for (const c of defaultCars) {
                await pool.query(
                    'INSERT INTO cars (make, model, year, price, mileage, transmission, fuel_type, body_type, `condition`, description, image_url, seller_id, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)',
                    [c.make, c.model, c.year, c.price, c.mileage, c.transmission, c.fuel_type, c.body_type, c.condition, c.description, c.image_url, sellerId, 'Available']
                );
            }
        } else {
            const db = readJsonDb();
            defaultCars.forEach((c, i) => {
                db.cars.push({
                    id: i + 1,
                    ...c,
                    seller_id: sellerId,
                    status: 'Available',
                    created_at: new Date().toISOString()
                });
            });
            writeJsonDb(db);
        }
        console.log('[DATABASE] Seeded premium cars successfully!');
    }
}
// -------------------------------------------------------------
// Unified Database Access Layer (API)
// -------------------------------------------------------------
const db = {
    getMode: () => (isMySQL ? 'MySQL' : 'Local JSON File'),
    users: {
        getByEmail: async (email) => {
            if (isMySQL) {
                const [rows] = await pool.query('SELECT * FROM users WHERE email = ?', [email]);
                return rows[0] || null;
            } else {
                const data = readJsonDb();
                return data.users.find(u => u.email.toLowerCase() === email.toLowerCase()) || null;
            }
        },
        getById: async (id) => {
            const numId = parseInt(id, 10);
            if (isMySQL) {
                const [rows] = await pool.query('SELECT id, name, email, role, created_at FROM users WHERE id = ?', [numId]);
                return rows[0] || null;
            } else {
                const data = readJsonDb();
                const user = data.users.find(u => u.id === numId);
                if (!user) return null;
                // eslint-disable-next-line no-unused-vars
                const { password, ...safeUser } = user;
                return safeUser;
            }
        },
        create: async (name, email, hashedPassword, role = 'buyer') => {
            if (isMySQL) {
                const [result] = await pool.query(
                    'INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)',
                    [name, email, hashedPassword, role]
                );
                return result.insertId;
            } else {
                const data = readJsonDb();
                const newId = data.users.length > 0 ? Math.max(...data.users.map(u => u.id)) + 1 : 1;
                const newUser = {
                    id: newId,
                    name,
                    email,
                    password: hashedPassword,
                    role,
                    created_at: new Date().toISOString()
                };
                data.users.push(newUser);
                writeJsonDb(data);
                return newId;
            }
        },
        getAll: async () => {
            if (isMySQL) {
                const [rows] = await pool.query('SELECT id, name, email, role, created_at FROM users ORDER BY id DESC');
                return rows;
            } else {
                const data = readJsonDb();
                return data.users.map(({ password, ...u }) => u);
            }
        },
        update: async (id, { name, email, role }) => {
            const numId = parseInt(id, 10);
            if (isMySQL) {
                await pool.query('UPDATE users SET name = ?, email = ?, role = ? WHERE id = ?', [name, email, role, numId]);
            } else {
                const data = readJsonDb();
                const idx = data.users.findIndex(u => u.id === numId);
                if (idx !== -1) {
                    data.users[idx].name = name;
                    data.users[idx].email = email;
                    data.users[idx].role = role;
                    writeJsonDb(data);
                }
            }
        },
        delete: async (id) => {
            const numId = parseInt(id, 10);
            if (isMySQL) {
                await pool.query('DELETE FROM users WHERE id = ?', [numId]);
            } else {
                const data = readJsonDb();
                data.users = data.users.filter(u => u.id !== numId);
                writeJsonDb(data);
            }
        }
    },
    cars: {
        getAll: async (filters = {}) => {
            if (isMySQL) {
                let sql = 'SELECT * FROM cars WHERE 1=1';
                const params = [];
                if (filters.make) { sql += ' AND make = ?'; params.push(filters.make); }
                if (filters.condition) { sql += ' AND `condition` = ?'; params.push(filters.condition); }
                if (filters.transmission) { sql += ' AND transmission = ?'; params.push(filters.transmission); }
                if (filters.fuel_type) { sql += ' AND fuel_type = ?'; params.push(filters.fuel_type); }
                if (filters.body_type) { sql += ' AND body_type = ?'; params.push(filters.body_type); }
                if (filters.seller_id) { sql += ' AND seller_id = ?'; params.push(parseInt(filters.seller_id, 10)); }
                if (filters.status) { sql += ' AND status = ?'; params.push(filters.status); }
                if (filters.min_price) { sql += ' AND price >= ?'; params.push(parseFloat(filters.min_price)); }
                if (filters.max_price) { sql += ' AND price <= ?'; params.push(parseFloat(filters.max_price)); }
                if (filters.search) {
                    sql += ' AND (make LIKE ? OR model LIKE ? OR description LIKE ?)';
                    const searchVal = `%${filters.search}%`;
                    params.push(searchVal, searchVal, searchVal);
                }
                sql += ' ORDER BY id DESC';
                const [rows] = await pool.query(sql, params);
                return rows;
            } else {
                const data = readJsonDb();
                let list = [...data.cars];
                if (filters.make) list = list.filter(c => c.make.toLowerCase() === filters.make.toLowerCase());
                if (filters.condition) list = list.filter(c => c.condition === filters.condition);
                if (filters.transmission) list = list.filter(c => c.transmission === filters.transmission);
                if (filters.fuel_type) list = list.filter(c => c.fuel_type === filters.fuel_type);
                if (filters.body_type) list = list.filter(c => c.body_type === filters.body_type);
                if (filters.seller_id) list = list.filter(c => c.seller_id === parseInt(filters.seller_id, 10));
                if (filters.status) list = list.filter(c => c.status === filters.status);
                if (filters.min_price) list = list.filter(c => c.price >= parseFloat(filters.min_price));
                if (filters.max_price) list = list.filter(c => c.price <= parseFloat(filters.max_price));
                if (filters.search) {
                    const s = filters.search.toLowerCase();
                    list = list.filter(c =>
                        c.make.toLowerCase().includes(s) ||
                        c.model.toLowerCase().includes(s) ||
                        (c.description && c.description.toLowerCase().includes(s))
                    );
                }
                return list.reverse();
            }
        },
        getById: async (id) => {
            const numId = parseInt(id, 10);
            if (isMySQL) {
                const [rows] = await pool.query('SELECT c.*, u.name as seller_name, u.email as seller_email FROM cars c JOIN users u ON c.seller_id = u.id WHERE c.id = ?', [numId]);
                return rows[0] || null;
            } else {
                const data = readJsonDb();
                const car = data.cars.find(c => c.id === numId);
                if (!car) return null;
                const seller = data.users.find(u => u.id === car.seller_id) || {};
                return {
                    ...car,
                    seller_name: seller.name || 'Elite Seller',
                    seller_email: seller.email || 'seller@dealer.com'
                };
            }
        },
        create: async (carData) => {
            const { make, model, year, price, mileage, transmission, fuel_type, body_type, condition, description, image_url, seller_id } = carData;
            if (isMySQL) {
                const [result] = await pool.query(
                    'INSERT INTO cars (make, model, year, price, mileage, transmission, fuel_type, body_type, `condition`, description, image_url, seller_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)',
                    [make, model, year, price, mileage, transmission, fuel_type, body_type, condition, description, image_url, seller_id]
                );
                return result.insertId;
            } else {
                const data = readJsonDb();
                const newId = data.cars.length > 0 ? Math.max(...data.cars.map(c => c.id)) + 1 : 1;
                const newCar = {
                    id: newId,
                    make,
                    model,
                    year: parseInt(year, 10),
                    price: parseFloat(price),
                    mileage: parseInt(mileage, 10),
                    transmission,
                    fuel_type,
                    body_type,
                    condition,
                    description,
                    image_url,
                    seller_id: parseInt(seller_id, 10),
                    status: 'Available',
                    created_at: new Date().toISOString()
                };
                data.cars.push(newCar);
                writeJsonDb(data);
                return newId;
            }
        },
        update: async (id, carData) => {
            const numId = parseInt(id, 10);
            const { make, model, year, price, mileage, transmission, fuel_type, body_type, condition, description, image_url, status } = carData;
            if (isMySQL) {
                let query = 'UPDATE cars SET make=?, model=?, year=?, price=?, mileage=?, transmission=?, fuel_type=?, body_type=?, `condition`=?, description=?, status=?';
                const params = [make, model, year, price, mileage, transmission, fuel_type, body_type, condition, description, status];
                if (image_url) {
                    query += ', image_url=?';
                    params.push(image_url);
                }
                query += ' WHERE id=?';
                params.push(numId);
                await pool.query(query, params);
            } else {
                const data = readJsonDb();
                const idx = data.cars.findIndex(c => c.id === numId);
                if (idx !== -1) {
                    data.cars[idx] = {
                        ...data.cars[idx],
                        make,
                        model,
                        year: parseInt(year, 10),
                        price: parseFloat(price),
                        mileage: parseInt(mileage, 10),
                        transmission,
                        fuel_type,
                        body_type,
                        condition,
                        description,
                        status,
                        image_url: image_url ? image_url : data.cars[idx].image_url
                    };
                    writeJsonDb(data);
                }
            }
        },
        delete: async (id) => {
            const numId = parseInt(id, 10);
            if (isMySQL) {
                await pool.query('DELETE FROM cars WHERE id = ?', [numId]);
            } else {
                const data = readJsonDb();
                data.cars = data.cars.filter(c => c.id !== numId);
                writeJsonDb(data);
            }
        }
    },
    cart: {
        get: async (userId) => {
            const numUserId = parseInt(userId, 10);
            if (isMySQL) {
                const [rows] = await pool.query(
                    'SELECT c.id as cart_id, car.* FROM cart c JOIN cars car ON c.car_id = car.id WHERE c.user_id = ?',
                    [numUserId]
                );
                return rows;
            } else {
                const data = readJsonDb();
                const cartItems = data.cart.filter(item => item.user_id === numUserId);
                return cartItems.map(item => {
                    const car = data.cars.find(c => c.id === item.car_id);
                    return car ? { cart_id: item.id, ...car } : null;
                }).filter(Boolean);
            }
        },
        add: async (userId, carId) => {
            const numUserId = parseInt(userId, 10);
            const numCarId = parseInt(carId, 10);
            if (isMySQL) {
                await pool.query('INSERT IGNORE INTO cart (user_id, car_id) VALUES (?, ?)', [numUserId, numCarId]);
            } else {
                const data = readJsonDb();
                const exists = data.cart.some(item => item.user_id === numUserId && item.car_id === numCarId);
                if (!exists) {
                    const newId = data.cart.length > 0 ? Math.max(...data.cart.map(c => c.id)) + 1 : 1;
                    data.cart.push({ id: newId, user_id: numUserId, car_id: numCarId, created_at: new Date().toISOString() });
                    writeJsonDb(data);
                }
            }
        },
        remove: async (userId, carId) => {
            const numUserId = parseInt(userId, 10);
            const numCarId = parseInt(carId, 10);
            if (isMySQL) {
                await pool.query('DELETE FROM cart WHERE user_id = ? AND car_id = ?', [numUserId, numCarId]);
            } else {
                const data = readJsonDb();
                data.cart = data.cart.filter(item => !(item.user_id === numUserId && item.car_id === numCarId));
                writeJsonDb(data);
            }
        },
        removeFromAll: async (carId) => {
            const numCarId = parseInt(carId, 10);
            if (isMySQL) {
                await pool.query('DELETE FROM cart WHERE car_id = ?', [numCarId]);
            } else {
                const data = readJsonDb();
                data.cart = data.cart.filter(item => item.car_id !== numCarId);
                writeJsonDb(data);
            }
        },
        clear: async (userId) => {
            const numUserId = parseInt(userId, 10);
            if (isMySQL) {
                await pool.query('DELETE FROM cart WHERE user_id = ?', [numUserId]);
            } else {
                const data = readJsonDb();
                data.cart = data.cart.filter(item => item.user_id !== numUserId);
                writeJsonDb(data);
            }
        }
    },
    bookings: {
        getAll: async (filters = {}) => {
            if (isMySQL) {
                let sql = `
          SELECT b.*, c.make, c.model, c.year, c.price, c.image_url, 
                 buyer.name as buyer_name, buyer.email as buyer_email,
                 seller.id as seller_id, seller.name as seller_name
          FROM bookings b
          JOIN cars c ON b.car_id = c.id
          JOIN users buyer ON b.buyer_id = buyer.id
          JOIN users seller ON c.seller_id = seller.id
          WHERE 1=1
        `;
                const params = [];
                if (filters.buyer_id) { sql += ' AND b.buyer_id = ?'; params.push(parseInt(filters.buyer_id, 10)); }
                if (filters.seller_id) { sql += ' AND c.seller_id = ?'; params.push(parseInt(filters.seller_id, 10)); }
                if (filters.status) { sql += ' AND b.status = ?'; params.push(filters.status); }
                sql += ' ORDER BY b.id DESC';
                const [rows] = await pool.query(sql, params);
                return rows;
            } else {
                const data = readJsonDb();
                let list = [...data.bookings];
                const mapped = list.map(b => {
                    const car = data.cars.find(c => c.id === b.car_id) || {};
                    const buyer = data.users.find(u => u.id === b.buyer_id) || {};
                    const seller = data.users.find(u => u.id === car.seller_id) || {};
                    return {
                        ...b,
                        make: car.make,
                        model: car.model,
                        year: car.year,
                        price: car.price,
                        image_url: car.image_url,
                        buyer_name: buyer.name,
                        buyer_email: buyer.email,
                        seller_id: seller.id,
                        seller_name: seller.name
                    };
                });
                let filtered = mapped;
                if (filters.buyer_id) filtered = filtered.filter(b => b.buyer_id === parseInt(filters.buyer_id, 10));
                if (filters.seller_id) filtered = filtered.filter(b => b.seller_id === parseInt(filters.seller_id, 10));
                if (filters.status) filtered = filtered.filter(b => b.status === filters.status);
                return filtered.reverse();
            }
        },
        create: async (bookingData) => {
            const { car_id, buyer_id, booking_date, type, notes } = bookingData;
            if (isMySQL) {
                const [result] = await pool.query(
                    'INSERT INTO bookings (car_id, buyer_id, booking_date, type, notes) VALUES (?, ?, ?, ?, ?)',
                    [car_id, buyer_id, booking_date, type, notes]
                );
                // Also update car status if this is a reservation
                if (type === 'Reservation') {
                    await pool.query("UPDATE cars SET status = 'Reserved' WHERE id = ?", [car_id]);
                }
                return result.insertId;
            } else {
                const data = readJsonDb();
                const newId = data.bookings.length > 0 ? Math.max(...data.bookings.map(b => b.id)) + 1 : 1;
                const newBooking = {
                    id: newId,
                    car_id: parseInt(car_id, 10),
                    buyer_id: parseInt(buyer_id, 10),
                    booking_date,
                    type,
                    status: 'Pending',
                    notes,
                    created_at: new Date().toISOString()
                };
                data.bookings.push(newBooking);

                // Update car status if reservation
                if (type === 'Reservation') {
                    const carIdx = data.cars.findIndex(c => c.id === parseInt(car_id, 10));
                    if (carIdx !== -1) {
                        data.cars[carIdx].status = 'Reserved';
                    }
                }

                writeJsonDb(data);
                return newId;
            }
        },
        updateStatus: async (id, status) => {
            const numId = parseInt(id, 10);
            if (isMySQL) {
                await pool.query('UPDATE bookings SET status = ? WHERE id = ?', [status, numId]);

                // If booking cancelled, release the car status back to Available
                if (status === 'Cancelled') {
                    const [bookingRows] = await pool.query('SELECT car_id, type FROM bookings WHERE id = ?', [numId]);
                    if (bookingRows.length > 0 && bookingRows[0].type === 'Reservation') {
                        await pool.query("UPDATE cars SET status = 'Available' WHERE id = ?", [bookingRows[0].car_id]);
                    }
                }
            } else {
                const data = readJsonDb();
                const idx = data.bookings.findIndex(b => b.id === numId);
                if (idx !== -1) {
                    data.bookings[idx].status = status;

                    if (status === 'Cancelled' && data.bookings[idx].type === 'Reservation') {
                        const carIdx = data.cars.findIndex(c => c.id === data.bookings[idx].car_id);
                        if (carIdx !== -1) {
                            data.cars[carIdx].status = 'Available';
                        }
                    }
                    writeJsonDb(data);
                }
            }
        }
    },
    orders: {
        getAll: async (filters = {}) => {
            if (isMySQL) {
                let sql = `
          SELECT o.*, c.make, c.model, c.year, c.image_url,
                 buyer.name as buyer_name, buyer.email as buyer_email,
                 seller.name as seller_name
          FROM orders o
          JOIN cars c ON o.car_id = c.id
          JOIN users buyer ON o.buyer_id = buyer.id
          JOIN users seller ON o.seller_id = seller.id
          WHERE 1=1
        `;
                const params = [];
                if (filters.buyer_id) { sql += ' AND o.buyer_id = ?'; params.push(parseInt(filters.buyer_id, 10)); }
                if (filters.seller_id) { sql += ' AND o.seller_id = ?'; params.push(parseInt(filters.seller_id, 10)); }
                sql += ' ORDER BY o.id DESC';
                const [rows] = await pool.query(sql, params);
                return rows;
            } else {
                const data = readJsonDb();
                let list = [...data.orders];
                const mapped = list.map(o => {
                    const car = data.cars.find(c => c.id === o.car_id) || {};
                    const buyer = data.users.find(u => u.id === o.buyer_id) || {};
                    const seller = data.users.find(u => u.id === o.seller_id) || {};
                    return {
                        ...o,
                        make: car.make,
                        model: car.model,
                        year: car.year,
                        image_url: car.image_url,
                        buyer_name: buyer.name,
                        buyer_email: buyer.email,
                        seller_name: seller.name
                    };
                });
                let filtered = mapped;
                if (filters.buyer_id) filtered = filtered.filter(o => o.buyer_id === parseInt(filters.buyer_id, 10));
                if (filters.seller_id) filtered = filtered.filter(o => o.seller_id === parseInt(filters.seller_id, 10));
                return filtered.reverse();
            }
        },
        create: async (orderData) => {
            const { car_id, buyer_id, seller_id, price_paid, payment_method, shipping_address } = orderData;
            if (isMySQL) {
                const [result] = await pool.query(
                    'INSERT INTO orders (car_id, buyer_id, seller_id, price_paid, payment_method, shipping_address) VALUES (?, ?, ?, ?, ?, ?)',
                    [car_id, buyer_id, seller_id, price_paid, payment_method, shipping_address]
                );
                // Update car status to sold
                await pool.query("UPDATE cars SET status = 'Sold' WHERE id = ?", [car_id]);
                return result.insertId;
            } else {
                const data = readJsonDb();
                const newId = data.orders.length > 0 ? Math.max(...data.orders.map(o => o.id)) + 1 : 1;
                const newOrder = {
                    id: newId,
                    car_id: parseInt(car_id, 10),
                    buyer_id: parseInt(buyer_id, 10),
                    seller_id: parseInt(seller_id, 10),
                    price_paid: parseFloat(price_paid),
                    payment_method,
                    status: 'Paid',
                    shipping_address,
                    created_at: new Date().toISOString()
                };
                data.orders.push(newOrder);
                // Update car status to Sold
                const carIdx = data.cars.findIndex(c => c.id === parseInt(car_id, 10));
                if (carIdx !== -1) {
                    data.cars[carIdx].status = 'Sold';
                }
                writeJsonDb(data);
                return newId;
            }
        }
    },
    tickets: {
        getAll: async (filters = {}) => {
            if (isMySQL) {
                let sql = `
          SELECT t.*, u.name as buyer_name, u.email as buyer_email 
          FROM tickets t 
          JOIN users u ON t.buyer_id = u.id 
          WHERE 1=1
        `;
                const params = [];
                if (filters.buyer_id) { sql += ' AND t.buyer_id = ?'; params.push(parseInt(filters.buyer_id, 10)); }
                if (filters.status) { sql += ' AND t.status = ?'; params.push(filters.status); }
                sql += ' ORDER BY t.id DESC';
                const [rows] = await pool.query(sql, params);
                return rows;
            } else {
                const data = readJsonDb();
                let list = [...data.tickets];
                const mapped = list.map(t => {
                    const buyer = data.users.find(u => u.id === t.buyer_id) || {};
                    return {
                        ...t,
                        buyer_name: buyer.name,
                        buyer_email: buyer.email
                    };
                });
                let filtered = mapped;
                if (filters.buyer_id) filtered = filtered.filter(t => t.buyer_id === parseInt(filters.buyer_id, 10));
                if (filters.status) filtered = filtered.filter(t => t.status === filters.status);
                return filtered.reverse();
            }
        },
        getById: async (id) => {
            const numId = parseInt(id, 10);
            if (isMySQL) {
                const [rows] = await pool.query(
                    'SELECT t.*, u.name as buyer_name, u.email as buyer_email FROM tickets t JOIN users u ON t.buyer_id = u.id WHERE t.id = ?',
                    [numId]
                );
                return rows[0] || null;
            } else {
                const data = readJsonDb();
                const ticket = data.tickets.find(t => t.id === numId);
                if (!ticket) return null;
                const buyer = data.users.find(u => u.id === ticket.buyer_id) || {};
                return {
                    ...ticket,
                    buyer_name: buyer.name,
                    buyer_email: buyer.email
                };
            }
        },
        create: async (buyerId, subject) => {
            const numBuyerId = parseInt(buyerId, 10);
            if (isMySQL) {
                const [result] = await pool.query('INSERT INTO tickets (buyer_id, subject) VALUES (?, ?)', [numBuyerId, subject]);
                return result.insertId;
            } else {
                const data = readJsonDb();
                const newId = data.tickets.length > 0 ? Math.max(...data.tickets.map(t => t.id)) + 1 : 1;
                const newTicket = {
                    id: newId,
                    buyer_id: numBuyerId,
                    subject,
                    status: 'Open',
                    created_at: new Date().toISOString()
                };
                data.tickets.push(newTicket);
                writeJsonDb(data);
                return newId;
            }
        },
        updateStatus: async (id, status) => {
            const numId = parseInt(id, 10);
            if (isMySQL) {
                await pool.query('UPDATE tickets SET status = ? WHERE id = ?', [status, numId]);
            } else {
                const data = readJsonDb();
                const idx = data.tickets.findIndex(t => t.id === numId);
                if (idx !== -1) {
                    data.tickets[idx].status = status;
                    writeJsonDb(data);
                }
            }
        },
        getMessages: async (ticketId) => {
            const numTicketId = parseInt(ticketId, 10);
            if (isMySQL) {
                const [rows] = await pool.query(`
          SELECT tm.*, u.name as sender_name, u.role as sender_role 
          FROM ticket_messages tm 
          JOIN users u ON tm.sender_id = u.id 
          WHERE tm.ticket_id = ? 
          ORDER BY tm.id ASC
        `, [numTicketId]);
                return rows;
            } else {
                const data = readJsonDb();
                const msgs = data.ticket_messages.filter(tm => tm.ticket_id === numTicketId);
                return msgs.map(m => {
                    const user = data.users.find(u => u.id === m.sender_id) || {};
                    return {
                        ...m,
                        sender_name: user.name,
                        sender_role: user.role
                    };
                });
            }
        },
        addMessage: async (ticketId, senderId, message) => {
            const numTicketId = parseInt(ticketId, 10);
            const numSenderId = parseInt(senderId, 10);
            if (isMySQL) {
                await pool.query(
                    'INSERT INTO ticket_messages (ticket_id, sender_id, message) VALUES (?, ?, ?)',
                    [numTicketId, numSenderId, message]
                );
                // If responder is staff/support/admin, change ticket status to In Progress
                const [userRows] = await pool.query('SELECT role FROM users WHERE id = ?', [numSenderId]);
                if (userRows.length > 0 && userRows[0].role !== 'buyer') {
                    await pool.query("UPDATE tickets SET status = 'In Progress' WHERE id = ?", [numTicketId]);
                }
            } else {
                const data = readJsonDb();
                const newId = data.ticket_messages.length > 0 ? Math.max(...data.ticket_messages.map(m => m.id)) + 1 : 1;
                const newMsg = {
                    id: newId,
                    ticket_id: numTicketId,
                    sender_id: numSenderId,
                    message,
                    created_at: new Date().toISOString()
                };
                data.ticket_messages.push(newMsg);
                // Update status to In Progress if answered by staff
                const sender = data.users.find(u => u.id === numSenderId) || {};
                if (sender.role && sender.role !== 'buyer') {
                    const ticketIdx = data.tickets.findIndex(t => t.id === numTicketId);
                    if (ticketIdx !== -1) {
                        data.tickets[ticketIdx].status = 'In Progress';
                    }
                }
                writeJsonDb(data);
            }
        }
    },
    reports: {
        getDashboardStats: async (role, userId) => {
            const numUserId = parseInt(userId, 10);

            let totalSales = 0;
            let totalPurchases = 0;
            let carsCount = 0;
            let bookingsCount = 0;
            let ticketsCount = 0;
            let usersCount = 0;
            if (isMySQL) {
                if (role === 'admin' || role === 'staff') {
                    // General metrics
                    const [users] = await pool.query('SELECT COUNT(*) as total FROM users');
                    usersCount = users[0].total;
                    const [sales] = await pool.query('SELECT SUM(price_paid) as total FROM orders');
                    totalSales = sales[0].total || 0;
                    const [purchs] = await pool.query('SELECT SUM(price) as total FROM cars WHERE status = "Sold"');
                    totalPurchases = purchs[0].total || 0;
                    const [cars] = await pool.query('SELECT COUNT(*) as total FROM cars');
                    carsCount = cars[0].total;
                    const [bookings] = await pool.query('SELECT COUNT(*) as total FROM bookings');
                    bookingsCount = bookings[0].total;
                    const [tickets] = await pool.query('SELECT COUNT(*) as total FROM tickets WHERE status != "Resolved"');
                    ticketsCount = tickets[0].total;
                } else if (role === 'seller') {
                    // Seller specific metrics
                    const [sales] = await pool.query('SELECT SUM(price_paid) as total FROM orders WHERE seller_id = ?', [numUserId]);
                    totalSales = sales[0].total || 0;
                    const [cars] = await pool.query('SELECT COUNT(*) as total FROM cars WHERE seller_id = ?', [numUserId]);
                    carsCount = cars[0].total;
                    const [bookings] = await pool.query(
                        'SELECT COUNT(*) as total FROM bookings b JOIN cars c ON b.car_id = c.id WHERE c.seller_id = ?',
                        [numUserId]
                    );
                    bookingsCount = bookings[0].total;
                } else if (role === 'support') {
                    const [tickets] = await pool.query('SELECT COUNT(*) as total FROM tickets WHERE status != "Resolved"');
                    ticketsCount = tickets[0].total;
                } else if (role === 'buyer') {
                    const [bookings] = await pool.query('SELECT COUNT(*) as total FROM bookings WHERE buyer_id = ?', [numUserId]);
                    bookingsCount = bookings[0].total;
                    const [tickets] = await pool.query('SELECT COUNT(*) as total FROM tickets WHERE buyer_id = ? AND status != "Resolved"', [numUserId]);
                    ticketsCount = tickets[0].total;
                }
            } else {
                const data = readJsonDb();
                if (role === 'admin' || role === 'staff') {
                    usersCount = data.users.length;
                    totalSales = data.orders.reduce((sum, o) => sum + o.price_paid, 0);
                    totalPurchases = data.cars.filter(c => c.status === 'Sold').reduce((sum, c) => sum + c.price, 0) * 0.9; // simulated wholesale expense
                    carsCount = data.cars.length;
                    bookingsCount = data.bookings.length;
                    ticketsCount = data.tickets.filter(t => t.status !== 'Resolved').length;
                } else if (role === 'seller') {
                    const sellerCarsIds = data.cars.filter(c => c.seller_id === numUserId).map(c => c.id);
                    totalSales = data.orders.filter(o => sellerCarsIds.includes(o.car_id)).reduce((sum, o) => sum + o.price_paid, 0);
                    carsCount = sellerCarsIds.length;
                    bookingsCount = data.bookings.filter(b => sellerCarsIds.includes(b.car_id)).length;
                } else if (role === 'support') {
                    ticketsCount = data.tickets.filter(t => t.status !== 'Resolved').length;
                } else if (role === 'buyer') {
                    bookingsCount = data.bookings.filter(b => b.buyer_id === numUserId).length;
                    ticketsCount = data.tickets.filter(t => t.buyer_id === numUserId && t.status !== 'Resolved').length;
                }
            }
            return {
                totalSales: parseFloat(totalSales),
                totalPurchases: parseFloat(totalPurchases),
                carsCount,
                bookingsCount,
                ticketsCount,
                usersCount
            };
        }
    }
};
module.exports = {
    db,
    initializeDatabase
};