const express = require('express');
const router = express.Router();
const Product = require('../models/Product');

router.get('/', (req, res) => {
    const cart = req.session.cart || [];
    res.json({
        success: true,
        cart,
        totalItems: cart.reduce((sum, item) => sum + item.quantity, 0)
    });
});

router.post('/add', async (req, res) => {
    const { productId, quantity = 1 } = req.body;
    const product = await Product.findById(productId).populate('category');
    if (!product) return res.status(404).json({ success: false, error: 'Not found' });

    const itemIndex = req.session.cart.findIndex(item => item.productId === productId);
    if (itemIndex > -1) {
        req.session.cart[itemIndex].quantity += parseInt(quantity);
    } else {
        req.session.cart.push({
            productId: product._id.toString(),
            name: product.name,
            price: product.price,
            image: product.image,
            quantity: parseInt(quantity),
            category: product.category?.name || 'General'
        });
    }
    res.json({ success: true });
});

router.put('/update', (req, res) => {
    const { productId, quantity } = req.body;
    const itemIndex = req.session.cart.findIndex(item => item.productId === productId);
    if (itemIndex > -1) {
        if (quantity <= 0) req.session.cart.splice(itemIndex, 1);
        else req.session.cart[itemIndex].quantity = parseInt(quantity);
    }
    res.json({ success: true });
});

router.delete('/remove/:id', (req, res) => {
    req.session.cart = req.session.cart.filter(item => item.productId !== req.params.id);
    res.json({ success: true });
});

router.delete('/clear', (req, res) => {
    req.session.cart = [];
    res.json({ success: true });
});

module.exports = router;
