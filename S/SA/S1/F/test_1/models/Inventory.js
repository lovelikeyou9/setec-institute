const mongoose = require('mongoose');

const inventorySchema = new mongoose.Schema({
    product: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Product',
        required: true
    },
    sku: {
        type: String,
        required: true,
        unique: true
    },
    quantity: {
        type: Number,
        required: true,
        default: 0
    },
    reserved: {
        type: Number,
        default: 0
    },
    available: {
        type: Number,
        default: 0
    },
    lowStockThreshold: {
        type: Number,
        default: 10
    },
    location: {
        warehouse: String,
        aisle: String,
        shelf: String,
        bin: String
    },
    lastRestocked: Date,
    nextRestockDate: Date,
    supplier: {
        name: String,
        contact: String,
        leadTime: Number // in days
    },
    cost: Number,
    reorderQuantity: Number,
    status: {
        type: String,
        enum: ['in_stock', 'low_stock', 'out_of_stock', 'discontinued'],
        default: 'in_stock'
    },
    notes: String,
    createdAt: { 
        type: Date, 
        default: Date.now 
    },
    updatedAt: { 
        type: Date, 
        default: Date.now 
    }
});

// Calculate available quantity before saving
inventorySchema.pre('save', function(next) {
    this.available = this.quantity - this.reserved;
    next();
});

module.exports = mongoose.model('Inventory', inventorySchema);
