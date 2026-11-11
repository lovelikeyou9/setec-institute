const express = require('express');
const router = express.Router();
const Product = require('../models/Product');
const Category = require('../models/Category');

// Get all products
router.get('/', async (req, res) => {
    try {
        const products = await Product.find().populate('category');
        res.json(products);
    } catch (err) { res.status(500).json({ error: err.message }); }
});

// Get single product
router.get('/:id', async (req, res) => {
    try {
        const product = await Product.findById(req.params.id).populate('category');
        if (!product) return res.status(404).json({ error: 'Not found' });
        res.json(product);
    } catch (err) { res.status(500).json({ error: err.message }); }
});

// NEW: Create a product (Admin only)
router.post('/', async (req, res) => {
    try {
        // We assume category comes as a name string from the form, so we find its ID
        let categoryId = req.body.category;
        
        // If they sent a category name, try to find the ID
        const categoryDoc = await Category.findOne({ name: req.body.category });
        if (categoryDoc) {
            categoryId = categoryDoc._id;
        }

        const newProduct = new Product({
            name: req.body.name,
            description: req.body.description,
            price: req.body.price,
            quantity: req.body.quantity,
            image: req.body.image,
            category: categoryId,
            isFeatured: req.body.isFeatured || false,
            createdAt: new Date()
        });

        await newProduct.save();
        res.json({ success: true, product: newProduct });
    } catch (err) {
        res.status(500).json({ success: false, error: err.message });
    }
});

module.exports = router;
