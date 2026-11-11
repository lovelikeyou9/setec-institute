const mongoose = require('mongoose');

const productLineSchema = new mongoose.Schema({
    productLine: {
        type: String,
        required: true,
        unique: true
    },
    name: {
        type: String,
        required: true
    },
    description: String,
    image: String,
    category: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Category'
    },
    manufacturer: String,
    warrantyPeriod: Number, // in months
    isActive: {
        type: Boolean,
        default: true
    },
    specifications: {
        type: Map,
        of: String
    },
    createdAt: { 
        type: Date, 
        default: Date.now 
    },
    updatedAt: { 
        type: Date, 
        default: Date.now 
    }
});

module.exports = mongoose.model('ProductLine', productLineSchema);
