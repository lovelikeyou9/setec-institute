const mongoose = require('mongoose');

const cartSchema = new mongoose.Schema({
    user: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true
    },
    sessionId: String, // For guest users
    items: [{
        product: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'Product',
            required: true
        },
        quantity: {
            type: Number,
            required: true,
            min: 1,
            default: 1
        },
        price: Number, // Price at time of adding to cart
        addedAt: {
            type: Date,
            default: Date.now
        }
    }],
    couponCode: String,
    discountAmount: {
        type: Number,
        default: 0
    },
    notes: String,
    lastUpdated: {
        type: Date,
        default: Date.now
    },
    expiresAt: {
        type: Date,
        default: () => new Date(+new Date() + 7*24*60*60*1000) // 7 days
    }
});

module.exports = mongoose.model('Cart', cartSchema);
