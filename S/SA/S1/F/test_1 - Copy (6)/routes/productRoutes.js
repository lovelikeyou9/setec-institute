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

// Create a product
router.post('/', async (req, res) => {
    try {
        let categoryId = req.body.category;
        
        // Lookup Category ID from Name
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

// NEW FIX: Update a product
router.put('/:id', async (req, res) => {
    try {
        // 1. Check if category is a Name string, convert to ID
        if (req.body.category) {
            const categoryDoc = await Category.findOne({ name: req.body.category });
            if (categoryDoc) {
                req.body.category = categoryDoc._id;
            }
        }

        // 2. Update the product
        const updatedProduct = await Product.findByIdAndUpdate(
            req.params.id,
            req.body,
            { new: true }
        );
        res.json({ success: true, product: updatedProduct });
    } catch (err) {
        res.status(500).json({ success: false, error: err.message });
    }
});

// Delete a product
router.delete('/:id', async (req, res) => {
    try {
        const result = await Product.findByIdAndDelete(req.params.id);
        if (!result) return res.status(404).json({ success: false, error: 'Product not found' });
        res.json({ success: true, message: 'Product deleted' });
    } catch (err) {
        res.status(500).json({ success: false, error: err.message });
    }
});

module.exports = router;
