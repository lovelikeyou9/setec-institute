// server.js - CORRECTED VERSION
require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
const bodyParser = require('body-parser');
const path = require('path');
const session = require('express-session');
const fs = require('fs');

const User = require('./models/User');
const bcrypt = require('bcryptjs');

const app = express();

// Middleware
app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.static('public'));
app.use(session({
    secret: process.env.SESSION_SECRET || 'computer-store-secret',
    resave: false,
    saveUninitialized: true,
    cookie: { 
        secure: false,
        maxAge: 1000 * 60 * 60 * 24 // 24 hours
    }
}));

// Initialize cart in session if not exists
app.use((req, res, next) => {
    if (!req.session.cart) {
        req.session.cart = [];
    }
    next();
});

// MongoDB Connection
mongoose.connect(process.env.MONGO_URI || 'mongodb://127.0.0.1:27017/computer_store')
    .then(() => console.log('✅ MongoDB Connected'))
    .catch(err => console.error('❌ MongoDB Error:', err));

// Import models
const Category = require('./models/Category');
const Product = require('./models/Product');

// ========== API ROUTES ==========

// Test route
app.get('/api/test', (req, res) => {
    res.json({ 
        message: 'Server is running!', 
        time: new Date(),
        sessionId: req.sessionID,
        cartCount: req.session.cart.length
    });
});

// Get all products
app.get('/api/products', async (req, res) => {
    try {
        const products = await Product.find().populate('category');
        res.json(products);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// Get single product
app.get('/api/products/:id', async (req, res) => {
    try {
        const product = await Product.findById(req.params.id).populate('category');
        if (!product) {
            return res.status(404).json({ error: 'Product not found' });
        }
        res.json(product);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// Search products
app.get('/api/products/search/:keyword', async (req, res) => {
    try {
        const keyword = req.params.keyword;
        const products = await Product.find({
            name: { $regex: keyword, $options: 'i' }
        }).populate('category');
        res.json(products);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// Get categories
app.get('/api/categories', async (req, res) => {
    try {
        const categories = await Category.find();
        res.json(categories);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// ========== CART FUNCTIONALITY ==========

// Get cart items
app.get('/api/cart', (req, res) => {
    res.json({
        success: true,
        cart: req.session.cart,
        totalItems: req.session.cart.reduce((sum, item) => sum + item.quantity, 0),
        totalPrice: req.session.cart.reduce((sum, item) => sum + (item.price * item.quantity), 0)
    });
});

// Add item to cart
app.post('/api/cart/add', async (req, res) => {
    try {
        const { productId, quantity = 1 } = req.body;
        
        // Validate input
        if (!productId) {
            return res.status(400).json({ 
                success: false, 
                error: 'Product ID is required' 
            });
        }
        
        // Get product details
        const product = await Product.findById(productId);
        if (!product) {
            return res.status(404).json({ 
                success: false, 
                error: 'Product not found' 
            });
        }
        
        // Check if product is already in cart
        const existingItemIndex = req.session.cart.findIndex(
            item => item.productId === productId
        );
        
        if (existingItemIndex > -1) {
            // Update quantity if already in cart
            req.session.cart[existingItemIndex].quantity += parseInt(quantity);
        } else {
            // Add new item to cart
            req.session.cart.push({
                productId: product._id.toString(),
                name: product.name,
                price: product.price,
                image: product.image || 'https://via.placeholder.com/300x200?text=Computer',
                quantity: parseInt(quantity),
                category: product.category?.name || 'Uncategorized',
                stock: product.quantity
            });
        }
        
        res.json({
            success: true,
            message: 'Product added to cart',
            cart: req.session.cart,
            totalItems: req.session.cart.reduce((sum, item) => sum + item.quantity, 0)
        });
        
    } catch (error) {
        console.error('Error adding to cart:', error);
        res.status(500).json({ 
            success: false, 
            error: 'Failed to add product to cart' 
        });
    }
});

// Update cart item quantity
app.put('/api/cart/update', (req, res) => {
    try {
        const { productId, quantity } = req.body;
        
        // Validate input
        if (!productId || quantity === undefined) {
            return res.status(400).json({ 
                success: false, 
                error: 'Product ID and quantity are required' 
            });
        }
        
        // Find item in cart
        const itemIndex = req.session.cart.findIndex(
            item => item.productId === productId
        );
        
        if (itemIndex === -1) {
            return res.status(404).json({ 
                success: false, 
                error: 'Product not found in cart' 
            });
        }
        
        // Update quantity (remove if quantity is 0)
        if (quantity <= 0) {
            req.session.cart.splice(itemIndex, 1);
        } else {
            req.session.cart[itemIndex].quantity = parseInt(quantity);
        }
        
        res.json({
            success: true,
            message: 'Cart updated',
            cart: req.session.cart,
            totalItems: req.session.cart.reduce((sum, item) => sum + item.quantity, 0),
            totalPrice: req.session.cart.reduce((sum, item) => sum + (item.price * item.quantity), 0)
        });
        
    } catch (error) {
        console.error('Error updating cart:', error);
        res.status(500).json({ 
            success: false, 
            error: 'Failed to update cart' 
        });
    }
});

// Remove item from cart
app.delete('/api/cart/remove/:productId', (req, res) => {
    try {
        const { productId } = req.params;
        
        // Remove item from cart
        req.session.cart = req.session.cart.filter(
            item => item.productId !== productId
        );
        
        res.json({
            success: true,
            message: 'Product removed from cart',
            cart: req.session.cart,
            totalItems: req.session.cart.reduce((sum, item) => sum + item.quantity, 0)
        });
        
    } catch (error) {
        console.error('Error removing from cart:', error);
        res.status(500).json({ 
            success: false, 
            error: 'Failed to remove product from cart' 
        });
    }
});

// Clear entire cart
app.delete('/api/cart/clear', (req, res) => {
    try {
        req.session.cart = [];
        
        res.json({
            success: true,
            message: 'Cart cleared',
            cart: []
        });
        
    } catch (error) {
        console.error('Error clearing cart:', error);
        res.status(500).json({ 
            success: false, 
            error: 'Failed to clear cart' 
        });
    }
});

// ========== AUTH ROUTES ==========

// User registration
app.post('/api/auth/register', async (req, res) => {
    try {
        const { name, email, password, address, phone } = req.body;
        
        // Check if user already exists
        const existingUser = await User.findOne({ email });
        if (existingUser) {
            return res.status(400).json({ 
                success: false, 
                error: 'Email already registered' 
            });
        }
        
        // Hash password
        const hashedPassword = await bcrypt.hash(password, 10);
        
        // Create user
        const user = await User.create({
            name,
            email,
            password: hashedPassword,
            address,
            phone
        });
        
        // Remove password from response
        const userResponse = user.toObject();
        delete userResponse.password;
        
        res.json({
            success: true,
            message: 'Registration successful',
            user: userResponse
        });
        
    } catch (error) {
        console.error('Registration error:', error);
        res.status(500).json({ 
            success: false, 
            error: 'Registration failed' 
        });
    }
});

// User login
app.post('/api/auth/login', async (req, res) => {
    try {
        const { email, password } = req.body;
        
        // Find user
        const user = await User.findOne({ email });
        if (!user) {
            return res.status(401).json({ 
                success: false, 
                error: 'Invalid credentials' 
            });
        }
        
        // Check password
        const validPassword = await bcrypt.compare(password, user.password);
        if (!validPassword) {
            return res.status(401).json({ 
                success: false, 
                error: 'Invalid credentials' 
            });
        }
        
        // Set user in session
        req.session.userId = user._id;
        req.session.userName = user.name;
        req.session.userEmail = user.email;
        
        // Remove password from response
        const userResponse = user.toObject();
        delete userResponse.password;
        
        res.json({
            success: true,
            message: 'Login successful',
            user: userResponse
        });
        
    } catch (error) {
        console.error('Login error:', error);
        res.status(500).json({ 
            success: false, 
            error: 'Login failed' 
        });
    }
});

// User logout
app.post('/api/auth/logout', (req, res) => {
    req.session.destroy();
    res.json({ success: true, message: 'Logged out' });
});

// Get current user
app.get('/api/auth/me', (req, res) => {
    if (req.session.userId) {
        res.json({
            success: true,
            user: {
                id: req.session.userId,
                name: req.session.userName,
                email: req.session.userEmail
            }
        });
    } else {
        res.json({ success: false, user: null });
    }
});

// Test auth endpoint
app.get('/api/auth/test', (req, res) => {
    res.json({
        success: true,
        sessionId: req.sessionID,
        userId: req.session.userId || null,
        userName: req.session.userName || null,
        userEmail: req.session.userEmail || null,
        session: {
            cart: req.session.cart || [],
            cartCount: (req.session.cart || []).length
        }
    });
});

// Simple test endpoint
app.get('/api/simple-test', (req, res) => {
    res.json({ 
        success: true,
        message: 'Simple test works!',
        timestamp: new Date().toISOString()
    });
});

// ========== PAGE ROUTES ==========

// Serve HTML pages
app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

app.get('/products', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'products.html'));
});

app.get('/cart', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'cart.html'));
});

// Product detail page
app.get('/product/:id', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'product-detail.html'));
});

// Serve all other HTML pages
app.get('/about', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'about.html'));
});

app.get('/blog', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'blog.html'));
});

app.get('/checkout', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'checkout.html'));
});

app.get('/collections', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'collections.html'));
});

app.get('/login', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'login.html'));
});

app.get('/register', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'register.html'));
});

app.get('/profile', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'profile.html'));
});

app.get('/search', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'search.html'));
});

app.get('/settings', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'settings.html'));
});

// Create product-detail.html if it doesn't exist
const productDetailPath = path.join(__dirname, 'public', 'product-detail.html');
if (!fs.existsSync(productDetailPath)) {
    fs.writeFileSync(productDetailPath, `
        <!DOCTYPE html>
        <html>
        <head>
            <title>Product Details - Computer Store</title>
            <link rel="stylesheet" href="css/style.css">
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
            <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
        </head>
        <body>
            <nav class="navbar">
                <div class="container">
                    <a href="/" class="nav-logo">
                        <i class="fas fa-desktop"></i>
                        <span>ComputerStore</span>
                    </a>
                    <a href="/products" style="color: var(--primary); text-decoration: none; margin-left: auto;">
                        <i class="fas fa-arrow-left"></i> Back to Products
                    </a>
                </div>
            </nav>
            <div class="container" style="padding: 40px 20px;">
                <div id="productInfo">
                    <div class="loading-spinner">Loading product details...</div>
                </div>
            </div>
            <script src="js/product-detail.js"></script>
        </body>
        </html>
    `);
}

// ========== ERROR HANDLING ==========

// 404 handler for API routes (MUST BE LAST!)
app.use('/api/*', (req, res) => {
    res.status(404).json({ 
        success: false, 
        error: 'API endpoint not found',
        requestedUrl: req.originalUrl
    });
});

// 404 handler for pages (MUST BE LAST!)
app.use((req, res) => {
    res.status(404).sendFile(path.join(__dirname, 'public', '404.html'));
});

// ========== START SERVER ==========

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`🚀 Server running at http://localhost:${PORT}`);
    console.log(`📁 Homepage: http://localhost:${PORT}`);
    console.log(`🛒 Cart: http://localhost:${PORT}/cart`);
    console.log(`📦 Products: http://localhost:${PORT}/products`);
    console.log(`🔐 Login: http://localhost:${PORT}/login`);
    console.log(`📝 Register: http://localhost:${PORT}/register`);
    console.log(`⚡ Test API: http://localhost:${PORT}/api/test`);
    console.log(`🧪 Simple Test: http://localhost:${PORT}/api/simple-test`);
    console.log(`🔐 Auth Test: http://localhost:${PORT}/api/auth/test`);
    console.log(`📊 MongoDB: ${process.env.MONGO_URI || 'mongodb://127.0.0.1:27017/computer_store'}`);
});

