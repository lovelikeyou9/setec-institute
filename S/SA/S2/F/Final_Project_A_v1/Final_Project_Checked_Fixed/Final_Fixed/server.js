// Core Express Server for Premium Car Dealer Marketplace & Admin Portal
const express = require('express');
const cors = require('cors');
const path = require('path');
const fs = require('fs');
const multer = require('multer');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');
require('dotenv').config();
const { initializeDatabase, db } = require('./db');
const app = express();
const PORT = process.env.PORT || 3000;
const JWT_SECRET = process.env.JWT_SECRET || 'premium-car-dealer-token-secret-key-2026';
// Middlewares
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
// Serve static frontend files
app.use(express.static(path.join(__dirname, 'public')));
// Ensure Uploads folder exists
const uploadsDir = path.join(__dirname, 'public', 'uploads');
if (!fs.existsSync(uploadsDir)) {
    fs.mkdirSync(uploadsDir, { recursive: true });
}
// Write default SVG placeholder images for cars if they don't exist
const placeholderFiles = [
    { name: 'tesla_s.svg', color: '#06b6d4', text: 'Tesla Model S' },
    { name: 'porsche_911.svg', color: '#3b82f6', text: 'Porsche 911' },
    { name: 'mustang.svg', color: '#ef4444', text: 'Shelby GT500' },
    { name: 'audi_etron.svg', color: '#8b5cf6', text: 'Audi e-tron GT' },
    { name: 'range_rover.svg', color: '#10b981', text: 'Range Rover' }
];
placeholderFiles.forEach(file => {
    const filePath = path.join(uploadsDir, file.name);
    if (!fs.existsSync(filePath)) {
        const svgContent = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 450" width="100%" height="100%">
      <rect width="100%" height="100%" fill="#1e293b"/>
      <defs>
        <linearGradient id="g_${file.name.replace('.svg', '')}" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="${file.color}" stop-opacity="0.8"/>
          <stop offset="100%" stop-color="#0f172a" stop-opacity="0.9"/>
        </linearGradient>
      </defs>
      <rect width="100%" height="100%" fill="url(#g_${file.name.replace('.svg', '')})"/>
      <circle cx="400" cy="225" r="100" fill="#0f172a" opacity="0.4"/>
      <path d="M 250 260 L 300 210 L 500 210 L 550 260 Z" fill="#ffffff" opacity="0.1"/>
      <path d="M 220 280 L 260 220 L 540 220 L 580 280 Z" fill="none" stroke="#ffffff" stroke-width="4" opacity="0.6"/>
      <circle cx="300" cy="280" r="30" fill="#111" stroke="#fff" stroke-width="3"/>
      <circle cx="500" cy="280" r="30" fill="#111" stroke="#fff" stroke-width="3"/>
      <text x="400" y="380" font-family="'Outfit', sans-serif" font-size="32" font-weight="bold" fill="#ffffff" text-anchor="middle">${file.text}</text>
      <text x="400" y="410" font-family="'Outfit', sans-serif" font-size="16" fill="#94a3b8" text-anchor="middle">PREMIUM SHOWROOM SELECTION</text>
    </svg>`;
        fs.writeFileSync(filePath, svgContent, 'utf8');
    }
});
// Configure Multer for Car Listings File Upload
const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, uploadsDir);
    },
    filename: (req, file, cb) => {
        const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1e9);
        cb(null, 'car-' + uniqueSuffix + path.extname(file.originalname));
    }
});
const fileFilter = (req, file, cb) => {
    if (file.mimetype && file.mimetype.startsWith('image/')) {
        cb(null, true);
    } else {
        cb(new Error('Only image files are allowed!'), false);
    }
};
const upload = multer({
    storage: storage,
    fileFilter: fileFilter,
    limits: {
        fileSize: 10 * 1024 * 1024 // 10MB
    }
});
// -------------------------------------------------------------
// Authentication Middleware
// -------------------------------------------------------------
function authenticateToken(req, res, next) {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1];
    if (!token) return res.status(401).json({ error: 'Access token required' });
    jwt.verify(token, JWT_SECRET, (err, user) => {
        if (err) return res.status(403).json({ error: 'Session expired. Please log in again.' });
        req.user = user;
        next();
    });
}
function authorizeRoles(...allowedRoles) {
    return (req, res, next) => {
        if (!req.user || !allowedRoles.includes(req.user.role)) {
            return res.status(403).json({ error: 'Unauthorized access for your user role' });
        }
        next();
    };
}

function requireFields(body, fields) {
    const missing = fields.filter(field => body[field] === undefined || body[field] === null || String(body[field]).trim() === '');
    return missing;
}
function isSupportRole(role) {
    return ['support', 'staff', 'admin'].includes(role);
}
function normalizeEmail(email) {
    return String(email || '').trim().toLowerCase();
}
function isValidRole(role) {
    return ['admin', 'staff', 'support', 'seller', 'buyer'].includes(role);
}
function isValidStatus(status, allowed) {
    return allowed.includes(status);
}
// -------------------------------------------------------------
// API 1. AUTHENTICATION & REGISTRATION
// -------------------------------------------------------------
app.post('/api/auth/register', async (req, res) => {
    try {
        const name = (req.body.name || '').trim();
        const email = (req.body.email || '').trim().toLowerCase();
        const password = req.body.password || '';
        const { role } = req.body;
        if (!name || !email || !password) {
            return res.status(400).json({ error: 'All fields are required' });
        }
        const assignedRole = role === 'seller' ? 'seller' : 'buyer'; // Users can sign up as Buyer or Seller
        const existingUser = await db.users.getByEmail(email);
        if (existingUser) {
            return res.status(400).json({ error: 'Email is already registered' });
        }
        const hashedPassword = await bcrypt.hash(password, 10);
        const userId = await db.users.create(name, email, hashedPassword, assignedRole);
        const token = jwt.sign({ id: userId, name, email, role: assignedRole }, JWT_SECRET, { expiresIn: '7d' });
        res.status(201).json({
            message: 'Registration successful',
            token,
            user: { id: userId, name, email, role: assignedRole }
        });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
app.post('/api/auth/login', async (req, res) => {
    try {
        const email = (req.body.email || '').trim().toLowerCase();
        const password = req.body.password || '';
        if (!email || !password) {
            return res.status(400).json({ error: 'Email and password are required' });
        }
        const user = await db.users.getByEmail(email);
        if (!user) {
            return res.status(401).json({ error: 'Invalid email or password' });
        }
        const match = await bcrypt.compare(password, user.password);
        if (!match) {
            return res.status(401).json({ error: 'Invalid email or password' });
        }
        const token = jwt.sign({ id: user.id, name: user.name, email: user.email, role: user.role }, JWT_SECRET, { expiresIn: '24h' });
        res.json({
            message: 'Login successful',
            token,
            user: { id: user.id, name: user.name, email: user.email, role: user.role }
        });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
app.post('/api/auth/forgot-password', async (req, res) => {
    try {
        const { email } = req.body;
        if (!email) return res.status(400).json({ error: 'Email is required' });
        const user = await db.users.getByEmail(email);
        if (!user) {
            return res.status(404).json({ error: 'No account associated with this email' });
        }

        // Simulate reset token
        res.json({ message: 'A secure reset link has been dispatched to your email address.' });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
app.get('/api/auth/me', authenticateToken, async (req, res) => {
    try {
        const user = await db.users.getById(req.user.id);
        if (!user) return res.status(404).json({ error: 'User profile not found' });
        res.json({ user });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
// -------------------------------------------------------------
// API 2. CARS (PRODUCTS)
// -------------------------------------------------------------
app.get('/api/cars', async (req, res) => {
    try {
        const list = await db.cars.getAll(req.query);
        res.json(list);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
app.get('/api/cars/:id', async (req, res) => {
    try {
        const car = await db.cars.getById(req.params.id);
        if (!car) return res.status(404).json({ error: 'Car listing not found' });
        res.json(car);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
// Create Listing (Sellers, Staff, Admin)
app.post('/api/cars', authenticateToken, authorizeRoles('seller', 'staff', 'admin'), async (req, res) => {
    try {
        const missing = requireFields(req.body, ['make', 'model', 'year', 'price', 'mileage', 'transmission', 'fuel_type', 'body_type', 'condition', 'description']);
        if (missing.length) return res.status(400).json({ error: 'Missing car fields: ' + missing.join(', ') });
        const carData = {
            ...req.body,
            seller_id: req.user.role === 'admin' || req.user.role === 'staff'
                ? (req.body.seller_id || req.user.id)
                : req.user.id
        };
        const carId = await db.cars.create(carData);
        res.status(201).json({ message: 'Car listing created successfully', carId });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
// Edit Listing
app.put('/api/cars/:id', authenticateToken, authorizeRoles('seller', 'staff', 'admin'), async (req, res) => {
    try {
        const car = await db.cars.getById(req.params.id);
        if (!car) return res.status(404).json({ error: 'Car listing not found' });
        // Validate ownership
        if (req.user.role === 'seller' && car.seller_id !== req.user.id) {
            return res.status(403).json({ error: 'Unauthorized to modify this listing' });
        }
        const allowedCarStatuses = ['Available', 'Reserved', 'Sold'];
        if (req.body.status && !isValidStatus(req.body.status, allowedCarStatuses)) {
            return res.status(400).json({ error: 'Invalid car status' });
        }
        await db.cars.update(req.params.id, req.body);
        res.json({ message: 'Listing updated successfully' });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
// Delete Listing
app.delete('/api/cars/:id', authenticateToken, authorizeRoles('seller', 'staff', 'admin'), async (req, res) => {
    try {
        const car = await db.cars.getById(req.params.id);
        if (!car) return res.status(404).json({ error: 'Car listing not found' });
        if (req.user.role === 'seller' && car.seller_id !== req.user.id) {
            return res.status(403).json({ error: 'Unauthorized to delete this listing' });
        }
        await db.cars.delete(req.params.id);
        res.json({ message: 'Listing deleted successfully' });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
// Upload image endpoint
app.post('/api/cars/upload', authenticateToken, authorizeRoles('seller', 'staff', 'admin'), upload.single('image'), (req, res) => {
    if (!req.file) return res.status(400).json({ error: 'Please upload an image file' });
    res.json({ imageUrl: '/uploads/' + req.file.filename });
});
// -------------------------------------------------------------
// API 3. CART SYSTEM (Buyers)
// -------------------------------------------------------------
app.get('/api/cart', authenticateToken, authorizeRoles('buyer'), async (req, res) => {
    try {
        const items = await db.cart.get(req.user.id);
        res.json(items);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
app.post('/api/cart/add', authenticateToken, authorizeRoles('buyer'), async (req, res) => {
    try {
        const { carId } = req.body;
        if (!carId) return res.status(400).json({ error: 'carId is required' });
        const car = await db.cars.getById(carId);
        if (!car || car.status !== 'Available') {
            return res.status(400).json({ error: 'This car is no longer available for purchase' });
        }
        if (parseInt(car.seller_id, 10) === req.user.id) {
            return res.status(400).json({ error: 'You cannot add your own listing to cart' });
        }
        await db.cart.add(req.user.id, carId);
        res.json({ message: 'Car added to your cart successfully!' });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
app.delete('/api/cart/remove/:carId', authenticateToken, authorizeRoles('buyer'), async (req, res) => {
    try {
        await db.cart.remove(req.user.id, req.params.carId);
        res.json({ message: 'Car removed from cart' });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
// -------------------------------------------------------------
// API 4. CHECKOUT & MOCK PAYMENT GATEWAY
// -------------------------------------------------------------
app.post('/api/checkout', authenticateToken, authorizeRoles('buyer'), async (req, res) => {
    try {
        const { carId, paymentMethod, shippingAddress, cardName, cardNumber } = req.body;
        if (!carId || !paymentMethod || !shippingAddress) {
            return res.status(400).json({ error: 'Missing checkout parameters' });
        }
        const car = await db.cars.getById(carId);
        if (!car) return res.status(404).json({ error: 'Car listing not found' });
        if (car.status !== 'Available') {
            return res.status(400).json({ error: `This car is currently ${String(car.status).toLowerCase()}` });
        }
        if (parseInt(car.seller_id, 10) === req.user.id) {
            return res.status(400).json({ error: 'You cannot buy your own listing' });
        }
        // Mock payment verification processing delay
        if (paymentMethod === 'Credit Card' && (!cardName || !cardNumber)) {
            return res.status(400).json({ error: 'Credit card details are incomplete' });
        }
        // Record the purchase order
        const orderId = await db.orders.create({
            car_id: car.id,
            buyer_id: req.user.id,
            seller_id: car.seller_id,
            price_paid: car.price,
            payment_method: paymentMethod,
            shipping_address: shippingAddress
        });
        // Automatically remove this car from ALL shopping carts since it is sold
        // Our db layer clears cart globally or during cart lists, but we can do a broad clean:
        if (db.cart.removeFromAll) {
            await db.cart.removeFromAll(car.id);
        } else if (db.cart.remove) {
            await db.cart.remove(req.user.id, car.id);
        }
        res.status(201).json({
            message: 'Payment approved! Your purchase order is finalized.',
            orderId,
            receipt: {
                car: `${car.year} ${car.make} ${car.model}`,
                price: car.price,
                transactionId: 'TXN-' + Math.random().toString(36).substr(2, 9).toUpperCase(),
                date: new Date().toLocaleDateString()
            }
        });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
app.get('/api/orders', authenticateToken, async (req, res) => {
    try {
        let list;
        if (req.user.role === 'buyer') {
            list = await db.orders.getAll({ buyer_id: req.user.id });
        } else if (req.user.role === 'seller') {
            list = await db.orders.getAll({ seller_id: req.user.id });
        } else {
            // Admin/Staff/Support see all platform orders
            list = await db.orders.getAll();
        }
        res.json(list);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
// -------------------------------------------------------------
// API 5. DRIVE & RESERVATION BOOKINGS
// -------------------------------------------------------------
app.post('/api/bookings', authenticateToken, authorizeRoles('buyer'), async (req, res) => {
    try {
        const { carId, bookingDate, type, notes } = req.body;
        if (!carId || !bookingDate || !type) {
            return res.status(400).json({ error: 'Missing scheduling variables' });
        }
        if (!['Test Drive', 'Reservation'].includes(type)) {
            return res.status(400).json({ error: 'Invalid booking type' });
        }
        const car = await db.cars.getById(carId);
        if (!car) return res.status(404).json({ error: 'Car listing not found' });
        if (car.status !== 'Available') {
            return res.status(400).json({ error: `This vehicle is currently ${car.status.toLowerCase()}` });
        }
        const bookingId = await db.bookings.create({
            car_id: carId,
            buyer_id: req.user.id,
            booking_date: bookingDate,
            type,
            notes
        });
        res.status(201).json({
            message: `${type} request filed successfully! Check your Profile tab for approval status.`,
            bookingId
        });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
app.get('/api/bookings', authenticateToken, async (req, res) => {
    try {
        let list;
        if (req.user.role === 'buyer') {
            list = await db.bookings.getAll({ buyer_id: req.user.id });
        } else if (req.user.role === 'seller') {
            list = await db.bookings.getAll({ seller_id: req.user.id });
        } else {
            // Admin, Staff, Support see everything
            list = await db.bookings.getAll();
        }
        res.json(list);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
app.put('/api/bookings/:id/status', authenticateToken, authorizeRoles('seller', 'staff', 'admin'), async (req, res) => {
    try {
        const { status } = req.body;
        if (!status) return res.status(400).json({ error: 'status is required' });
        if (!isValidStatus(status, ['Pending', 'Approved', 'Completed', 'Cancelled'])) {
            return res.status(400).json({ error: 'Invalid booking status' });
        }
        // Validate ownership if seller
        if (req.user.role === 'seller') {
            const list = await db.bookings.getAll({ seller_id: req.user.id });
            const ownsBooking = list.some(b => b.id === parseInt(req.params.id, 10));
            if (!ownsBooking) return res.status(403).json({ error: 'Unauthorized to manage this scheduling' });
        }
        await db.bookings.updateStatus(req.params.id, status);
        res.json({ message: `Booking status updated to ${status}` });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
// -------------------------------------------------------------
// API 6. CUSTOMER SUPPORT TICKETING (Support, Staff, Admin)
// -------------------------------------------------------------
app.post('/api/tickets', authenticateToken, authorizeRoles('buyer'), async (req, res) => {
    try {
        const { subject, message } = req.body;
        if (!subject || !message) {
            return res.status(400).json({ error: 'Subject and message are required' });
        }
        const ticketId = await db.tickets.create(req.user.id, subject);
        await db.tickets.addMessage(ticketId, req.user.id, message);
        res.status(201).json({ message: 'Support ticket logged. Support team has been alerted.', ticketId });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
app.get('/api/tickets', authenticateToken, async (req, res) => {
    try {
        let list;
        if (req.user.role === 'buyer') {
            list = await db.tickets.getAll({ buyer_id: req.user.id });
        } else if (req.user.role === 'support' || req.user.role === 'staff' || req.user.role === 'admin') {
            list = await db.tickets.getAll();
        } else {
            return res.status(403).json({ error: 'Role unauthorized to access tickets' });
        }
        res.json(list);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
app.get('/api/tickets/:id', authenticateToken, async (req, res) => {
    try {
        const ticket = await db.tickets.getById(req.params.id);
        if (!ticket) return res.status(404).json({ error: 'Support ticket not found' });
        // Validate access: only ticket owner or support/staff/admin can view
        if (req.user.role === 'buyer' && ticket.buyer_id !== req.user.id) {
            return res.status(403).json({ error: 'Unauthorized to view this ticket' });
        }
        if (req.user.role !== 'buyer' && !isSupportRole(req.user.role)) {
            return res.status(403).json({ error: 'Role unauthorized to access tickets' });
        }
        const messages = await db.tickets.getMessages(req.params.id);
        res.json({ ticket, messages });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
app.post('/api/tickets/:id/messages', authenticateToken, async (req, res) => {
    try {
        const { message } = req.body;
        if (!message) return res.status(400).json({ error: 'Message body cannot be empty' });
        const ticket = await db.tickets.getById(req.params.id);
        if (!ticket) return res.status(404).json({ error: 'Support ticket not found' });
        if (req.user.role === 'buyer' && ticket.buyer_id !== req.user.id) {
            return res.status(403).json({ error: 'Unauthorized to update this ticket' });
        }
        if (req.user.role !== 'buyer' && !isSupportRole(req.user.role)) {
            return res.status(403).json({ error: 'Role unauthorized to reply to tickets' });
        }
        await db.tickets.addMessage(req.params.id, req.user.id, message);
        res.status(201).json({ message: 'Reply sent' });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
app.put('/api/tickets/:id/status', authenticateToken, authorizeRoles('support', 'staff', 'admin'), async (req, res) => {
    try {
        const { status } = req.body;
        if (!status) return res.status(400).json({ error: 'status is required' });
        if (!isValidStatus(status, ['Open', 'In Progress', 'Resolved'])) {
            return res.status(400).json({ error: 'Invalid ticket status' });
        }
        await db.tickets.updateStatus(req.params.id, status);
        res.json({ message: `Ticket status set to ${status}` });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
// -------------------------------------------------------------
// API 7. USER MANAGEMENT CRUD (Admin only)
// -------------------------------------------------------------
app.get('/api/users', authenticateToken, authorizeRoles('admin'), async (req, res) => {
    try {
        const list = await db.users.getAll();
        res.json(list);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
app.post('/api/users', authenticateToken, authorizeRoles('admin'), async (req, res) => {
    try {
        const name = String(req.body.name || '').trim();
        const email = normalizeEmail(req.body.email);
        const password = req.body.password || '';
        const role = req.body.role;
        if (!name || !email || !password || !role) {
            return res.status(400).json({ error: 'All fields are required' });
        }
        if (!isValidRole(role)) return res.status(400).json({ error: 'Invalid user role' });
        const existingUser = await db.users.getByEmail(email);
        if (existingUser) return res.status(400).json({ error: 'Email is already registered' });
        const hashedPassword = await bcrypt.hash(password, 10);
        const userId = await db.users.create(name, email, hashedPassword, role);
        res.status(201).json({ message: 'User account created successfully', userId });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
app.put('/api/users/:id', authenticateToken, authorizeRoles('admin'), async (req, res) => {
    try {
        const user = await db.users.getById(req.params.id);
        if (!user) return res.status(404).json({ error: 'User account not found' });
        const updateData = {
            name: String(req.body.name || user.name).trim(),
            email: normalizeEmail(req.body.email || user.email),
            role: req.body.role || user.role
        };
        if (!updateData.name || !updateData.email || !isValidRole(updateData.role)) {
            return res.status(400).json({ error: 'Invalid user update data' });
        }
        await db.users.update(req.params.id, updateData);
        res.json({ message: 'User account updated successfully' });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
app.delete('/api/users/:id', authenticateToken, authorizeRoles('admin'), async (req, res) => {
    try {
        if (parseInt(req.params.id, 10) === req.user.id) {
            return res.status(400).json({ error: 'You cannot delete your own admin account' });
        }
        await db.users.delete(req.params.id);
        res.json({ message: 'User account deleted successfully' });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
// -------------------------------------------------------------
// API 8. DASHBOARD & SYSTEM REPORTS (ApexCharts)
// -------------------------------------------------------------
app.get('/api/reports/dashboard', authenticateToken, async (req, res) => {
    try {
        const stats = await db.reports.getDashboardStats(req.user.role, req.user.id);

        // Provide dynamic historical metrics formatted specifically for ApexCharts
        const reportsData = {
            dbMode: db.getMode(),
            stats,
            // Charts seed arrays
            salesChart: {
                categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
                sales: [15000, 24000, 35000, 48000, stats.totalSales || 75000, 0, 0, 0, 0, 0, 0, 0],
                expenses: [12000, 18000, 28000, 32000, stats.totalPurchases || 62000, 0, 0, 0, 0, 0, 0, 0]
            },
            demographics: {
                labels: ['SUV', 'Sedan', 'Coupe', 'Electric', 'Convertible'],
                data: [44, 55, 13, 33, 22]
            }
        };
        res.json(reportsData);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// -------------------------------------------------------------
// API 0. DATABASE STATUS CHECK
// -------------------------------------------------------------
app.get('/api/db-status', (req, res) => {
    res.json({
        connected: true,
        mode: db.getMode(),
        message: db.getMode() === 'MySQL'
            ? 'Connected to MySQL database.'
            : 'Using local JSON database fallback. Start MySQL and check .env to use real MySQL.'
    });
});

// -------------------------------------------------------------
// API 8B. REALTIME ADMIN NOTIFICATIONS
// -------------------------------------------------------------
app.get('/api/admin/notifications', authenticateToken, async (req, res) => {
    try {
        const stats = await db.reports.getDashboardStats(req.user.role, req.user.id);
        const notifications = [];

        // Notification for all roles
        notifications.push({
            type: 'system',
            icon: 'ti ti-database',
            title: 'System Online',
            message: `Database engine is running with ${db.getMode()}.`
        });

        // Admin notification
        if (req.user.role === 'admin') {
            notifications.push({
                type: 'users',
                icon: 'ti ti-users',
                title: 'User Accounts',
                message: `${stats.usersCount || 0} user account(s) are registered.`
            });

            notifications.push({
                type: 'inventory',
                icon: 'ti ti-car',
                title: 'Inventory Updated',
                message: `${stats.carsCount || 0} showroom vehicle(s) are available.`
            });

            notifications.push({
                type: 'bookings',
                icon: 'ti ti-calendar',
                title: 'Visit Appointments',
                message: `${stats.bookingsCount || 0} booking request(s) are recorded.`
            });

            notifications.push({
                type: 'tickets',
                icon: 'ti ti-headset',
                title: 'Support Tickets',
                message: `${stats.ticketsCount || 0} support ticket(s) need attention.`
            });
        }

        // Staff notification
        if (req.user.role === 'staff') {
            notifications.push({
                type: 'inventory',
                icon: 'ti ti-car',
                title: 'Inventory Management',
                message: `${stats.carsCount || 0} vehicle listing(s) are available.`
            });

            notifications.push({
                type: 'bookings',
                icon: 'ti ti-calendar',
                title: 'Booking Management',
                message: `${stats.bookingsCount || 0} booking request(s) are recorded.`
            });

            notifications.push({
                type: 'tickets',
                icon: 'ti ti-headset',
                title: 'Support Desk',
                message: `${stats.ticketsCount || 0} ticket(s) are active.`
            });
        }

        // Seller notification
        if (req.user.role === 'seller') {
            notifications.push({
                type: 'inventory',
                icon: 'ti ti-car',
                title: 'Your Listings',
                message: `${stats.carsCount || 0} vehicle listing(s) are connected to your account.`
            });

            notifications.push({
                type: 'bookings',
                icon: 'ti ti-calendar',
                title: 'Buyer Appointments',
                message: `${stats.bookingsCount || 0} booking request(s) for your vehicles.`
            });
        }

        // Support notification
        if (req.user.role === 'support') {
            notifications.push({
                type: 'tickets',
                icon: 'ti ti-headset',
                title: 'Support Tickets',
                message: `${stats.ticketsCount || 0} active support ticket(s) need attention.`
            });
        }

        // Buyer notification
        if (req.user.role === 'buyer') {
            notifications.push({
                type: 'marketplace',
                icon: 'ti ti-shopping-cart',
                title: 'Marketplace Ready',
                message: 'You can browse cars, add to cart, book test drives, and contact support.'
            });

            notifications.push({
                type: 'bookings',
                icon: 'ti ti-calendar',
                title: 'Your Bookings',
                message: `${stats.bookingsCount || 0} booking request(s) are linked to your account.`
            });

            notifications.push({
                type: 'tickets',
                icon: 'ti ti-headset',
                title: 'Your Support Tickets',
                message: `${stats.ticketsCount || 0} support ticket(s) are linked to your account.`
            });
        }

        res.json({
            notifications,
            count: notifications.length,
            role: req.user.role,
            updated_at: new Date().toISOString()
        });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Fallback Route to serve SPA or standard error page for APIs
app.use('/api/*', (req, res) => {
    res.status(404).json({ error: 'API endpoint not found' });
});
// Catch all - redirect back to index.html for undefined requests
app.get('*', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'index.html'));
});
// Start Server after Database Initialization
// Error handler - put this BEFORE initializeDatabase()
app.use((err, req, res, next) => {
    console.error(err);

    res.status(400).json({
        success: false,
        error: err.message || 'Upload failed',
        message: err.message || 'Upload failed'
    });
});

// Start Server after Database Initialization
initializeDatabase().then(() => {
    app.listen(PORT, () => {
        console.log(`\n\x1b[35m========================================================\x1b[0m`);
        console.log(`\x1b[32m[SERVER] Premium Car Dealer active at http://localhost:${PORT}\x1b[0m`);
        console.log(`\x1b[36m[SERVER] Pair-programming Workspace: C:/Users/Admin/OneDrive/Desktop/S/SA/S2/F/Final_Project_A\x1b[0m`);
        console.log(`\x1b[35m========================================================\x1b[0m\n`);
    });
}).catch(err => {
    console.error('[SERVER] Critical failed to initialize database', err);
});