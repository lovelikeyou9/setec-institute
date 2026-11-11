// server.js - Clean Modular Version
require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
const bodyParser = require('body-parser');
const path = require('path');
const session = require('express-session');
const fs = require('fs');
const bcrypt = require('bcryptjs');

// Import Models
const User = require('./models/User');
const Product = require('./models/Product');
const Category = require('./models/Category');

// Import Modular Routes (Created in Step 1 & 2)
const productRoutes = require('./routes/productRoutes');
const orderRoutes = require('./routes/orderRoutes');

const app = express();

// ==========================================
// 1. MIDDLEWARE
// ==========================================
app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.static('public')); // Serves CSS, JS, Images
app.use(session({
    secret: process.env.SESSION_SECRET || 'computer-store-secret',
    resave: false,
    saveUninitialized: true,
    cookie: { 
        secure: false, // Set to true if using HTTPS
        maxAge: 1000 * 60 * 60 * 24 // 24 hours
    }
}));

// Initialize cart in session if it doesn't exist
app.use((req, res, next) => {
    if (!req.session.cart) {
        req.session.cart = [];
    }
    next();
});

// ==========================================
// 2. DATABASE CONNECTION
// ==========================================
mongoose.connect(process.env.MONGO_URI || 'mongodb://127.0.0.1:27017/computer_store')
    .then(() => console.log('✅ MongoDB Connected'))
    .catch(err => console.error('❌ MongoDB Error:', err));

// ==========================================
// 3. API ROUTES (MODULAR)
// ==========================================
app.use('/api/products', productRoutes);
app.use('/api/orders', orderRoutes);

// Helper API for Categories (Keep here for simplicity)
app.get('/api/categories', async (req, res) => {
    try {
        const categories = await Category.find();
        res.json(categories);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// ==========================================
// 4. CART & AUTH API (To be modularized next)
// ==========================================

// --- Cart API ---
app.get('/api/cart', (req, res) => {
    res.json({
        success: true,
        cart: req.session.cart,
        totalItems: req.session.cart.reduce((sum, item) => sum + item.quantity, 0),
        totalPrice: req.session.cart.reduce((sum, item) => sum + (item.price * item.quantity), 0)
    });
});

app.post('/api/cart/add', async (req, res) => {
    try {
        const { productId, quantity = 1 } = req.body;
        const product = await Product.findById(productId);
        if (!product) return res.status(404).json({ success: false, error: 'Product not found' });

        const existingItemIndex = req.session.cart.findIndex(item => item.productId === productId);
        if (existingItemIndex > -1) {
            req.session.cart[existingItemIndex].quantity += parseInt(quantity);
        } else {
            req.session.cart.push({
                productId: product._id.toString(),
                name: product.name,
                price: product.price,
                image: product.image,
                quantity: parseInt(quantity),
                category: product.category?.name || 'Uncategorized'
            });
        }
        res.json({ success: true, cart: req.session.cart });
    } catch (error) {
        res.status(500).json({ success: false, error: 'Failed to add to cart' });
    }
});

app.delete('/api/cart/clear', (req, res) => {
    req.session.cart = [];
    res.json({ success: true, message: 'Cart cleared' });
});

// --- Auth API ---
app.post('/api/auth/login', async (req, res) => {
    try {
        const { email, password } = req.body;
        const user = await User.findOne({ email });
        if (!user || !(await bcrypt.compare(password, user.password))) {
            return res.status(401).json({ success: false, error: 'Invalid credentials' });
        }
        req.session.userId = user._id;
        req.session.userName = user.name;
        res.json({ success: true, user: { name: user.name, email: user.email } });
    } catch (error) {
        res.status(500).json({ success: false, error: 'Login failed' });
    }
});

app.get('/api/auth/me', (req, res) => {
    if (req.session.userId) {
        res.json({ success: true, user: { name: req.session.userName } });
    } else {
        res.json({ success: false });
    }
});

app.post('/api/auth/logout', (req, res) => {
    req.session.destroy();
    res.json({ success: true });
});

// ==========================================
// 5. PAGE ROUTES (HTML SERVING)
// ==========================================
app.get('/', (req, res) => res.sendFile(path.join(__dirname, 'public', 'index.html')));
app.get('/products', (req, res) => res.sendFile(path.join(__dirname, 'public', 'products.html')));
app.get('/product/:id', (req, res) => res.sendFile(path.join(__dirname, 'public', 'product-detail.html')));
app.get('/cart', (req, res) => res.sendFile(path.join(__dirname, 'public', 'cart.html')));
app.get('/checkout', (req, res) => res.sendFile(path.join(__dirname, 'public', 'checkout.html')));
app.get('/login', (req, res) => res.sendFile(path.join(__dirname, 'public', 'login.html')));
app.get('/register', (req, res) => res.sendFile(path.join(__dirname, 'public', 'register.html')));
app.get('/profile', (req, res) => res.sendFile(path.join(__dirname, 'public', 'profile.html')));
app.get('/about', (req, res) => res.sendFile(path.join(__dirname, 'public', 'about.html')));
app.get('/blog', (req, res) => res.sendFile(path.join(__dirname, 'public', 'blog.html')));

// 404 Handler
app.use((req, res) => {
    res.status(404).sendFile(path.join(__dirname, 'public', '404.html'));
});

// ==========================================
// 6. START SERVER
// ==========================================
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`🚀 Server running at http://localhost:${PORT}`);
});
