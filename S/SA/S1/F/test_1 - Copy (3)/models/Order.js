const mongoose = require('mongoose');

const orderSchema = new mongoose.Schema({
    orderNumber: {
        type: String,
        unique: true
    },
    user: { 
        type: mongoose.Schema.Types.ObjectId, 
        ref: 'User', 
        required: false 
    },
    items: [{
        product: { 
            type: mongoose.Schema.Types.ObjectId, 
            ref: 'Product' 
        },
        name: String,
        sku: String,
        price: Number,
        quantity: Number,
        image: String,
        subtotal: Number
    }],
    subtotal: { 
        type: Number, 
        required: true 
    },
    tax: { 
        type: Number, 
        default: 0 
    },
    shippingCost: { 
        type: Number, 
        default: 0 
    },
    discount: {
        type: Number,
        default: 0
    },
    total: { 
        type: Number, 
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
        default: 'pending'
    },
    shippingAddress: {
        recipientName: String,
        street: String,
        city: String,
        state: String,
        zipCode: String,
        country: String,
        phone: String
    },
    billingAddress: {
        sameAsShipping: { type: Boolean, default: true },
        recipientName: String,
        street: String,
        city: String,
        state: String,
        zipCode: String,
        country: String
    },
    paymentMethod: {
        type: String,
        enum: ['credit_card', 'paypal', 'apple_pay', 'google_pay', 'bank_transfer'],
        required: true
    },
    paymentStatus: {
        type: String,
        enum: ['pending', 'paid', 'failed', 'refunded'],
        default: 'pending'
    },
    trackingNumber: String,
    shippingCarrier: String,
    estimatedDelivery: Date,
    actualDelivery: Date,
    notes: String,
    customerNotes: String,
    couponCode: String,
    salesRep: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Employee'
    },
    ipAddress: String,
    userAgent: String,
    createdAt: { 
        type: Date, 
        default: Date.now 
    },
    updatedAt: { 
        type: Date, 
        default: Date.now 
    }
});

// Generate order number before saving
orderSchema.pre('save', async function(next) {
    if (!this.orderNumber) {
        const date = new Date();
        const year = date.getFullYear().toString().slice(-2);
        const month = (date.getMonth() + 1).toString().padStart(2, '0');
        const day = date.getDate().toString().padStart(2, '0');
        const random = Math.floor(1000 + Math.random() * 9000);
        this.orderNumber = `ORD-${year}${month}${day}-${random}`;
    }
    next();
});

module.exports = mongoose.model('Order', orderSchema);
