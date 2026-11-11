const express = require('express');
const router = express.Router();
const Order = require('../models/Order');

// Create a new order
router.post('/', async (req, res) => {
    try {
        const cart = req.session.cart;
        if (!cart || cart.length === 0) return res.status(400).json({ success: false, error: "Cart empty" });

        const subtotal = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
        // Calculate tax and shipping exactly like frontend to avoid mismatch
        const tax = subtotal * 0.08;
        const shipping = subtotal > 100 ? 0 : 9.99;
        const total = subtotal + tax + shipping;
        
        const orderNumber = `ORD-${Date.now()}`;

        const newOrder = new Order({
            orderNumber,
            user: req.session.userId || null, 
            items: cart.map(item => ({
                product: item.productId,
                name: item.name,
                price: item.price,
                quantity: item.quantity,
                subtotal: item.price * item.quantity
            })),
            subtotal,
            tax,
            shippingCost: shipping,
            total,
            shippingAddress: req.body.shippingAddress,
            paymentMethod: req.body.paymentMethod,
            status: 'pending', // <--- FIXED: Must be lowercase 'pending' to match Schema
            createdAt: new Date()
        });

        await newOrder.save();
        req.session.cart = []; // Clear cart
        res.json({ success: true, orderNumber });
    } catch (error) {
        console.error("Order Error:", error);
        res.status(500).json({ success: false, error: error.message });
    }
});

// Get orders for the logged-in user
router.get('/my-orders', async (req, res) => {
    try {
        if (!req.session.userId) {
            return res.json({ success: false, error: "Not logged in" });
        }

        const orders = await Order.find({ user: req.session.userId }).sort({ createdAt: -1 });
        res.json({ success: true, orders });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});


// NEW: Admin - Get ALL orders from everyone
router.get('/all-orders', async (req, res) => {
    try {
        // In a real app, check if user is admin here
        const orders = await Order.find()
            .populate('user', 'name email') // Get buyer details
            .sort({ createdAt: -1 });
        res.json({ success: true, orders });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});


module.exports = router;
