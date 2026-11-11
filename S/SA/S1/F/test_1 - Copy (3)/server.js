require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
const bodyParser = require('body-parser');
const path = require('path');
const session = require('express-session');

const app = express();

// Middleware
app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.static('public'));

app.use(session({
    secret: 'secret-key',
    resave: false,
    saveUninitialized: true,
    cookie: { maxAge: 1000 * 60 * 60 * 24 } // 24 hours
}));

// Global Cart Middleware
app.use((req, res, next) => {
    if (!req.session.cart) req.session.cart = [];
    next();
});

// Database
mongoose.connect(process.env.MONGO_URI || 'mongodb://127.0.0.1:27017/computer_store')
    .then(() => console.log('✅ MongoDB Connected'))
    .catch(err => console.error('❌ MongoDB Error:', err));

// Import Routes
app.use('/api/auth', require('./routes/authRoutes'));
app.use('/api/cart', require('./routes/cartRoutes'));
app.use('/api/products', require('./routes/productRoutes'));
app.use('/api/orders', require('./routes/orderRoutes'));

// Categories API (Simple version)
app.get('/api/categories', async (req, res) => {
    const Category = require('./models/Category');
    const categories = await Category.find();
    res.json(categories);
});

// Frontend Routes
// Add 'search' to this list 👇
const pages = ['index', 'products', 'cart', 'checkout', 'login', 'register', 'profile', 'about', 'blog', 'admin', 'search'];

pages.forEach(page => {
    app.get(page === 'index' ? '/' : `/${page}`, (req, res) => {
        res.sendFile(path.join(__dirname, 'public', `${page}.html`));
    });
});


app.get('/product/:id', (req, res) => res.sendFile(path.join(__dirname, 'public', 'product-detail.html')));

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`🚀 Server running on http://localhost:${PORT}`));
