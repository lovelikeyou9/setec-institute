const mongoose = require('mongoose');

const orderDetailSchema = new mongoose.Schema({
    order: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Order',
        required: true
    },
    status: {
        type: String,
        enum: [
            'pending', 
            'confirmed', 
            'processing', 
            'shipped', 
            'delivered', 
            'cancelled', 
            'refunded'
        ],
        required: true
    },
    description: String,
    location: String, // Warehouse location or shipping location
    updatedBy: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User'
    },
    notes: String,
    createdAt: { 
        type: Date, 
        default: Date.now 
    }
});

module.exports = mongoose.model('OrderDetail', orderDetailSchema);
