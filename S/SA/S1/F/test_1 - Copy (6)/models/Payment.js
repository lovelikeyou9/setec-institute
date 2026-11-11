const mongoose = require('mongoose');

const paymentSchema = new mongoose.Schema({
    order: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Order',
        required: true
    },
    user: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true
    },
    paymentId: {
        type: String,
        required: true,
        unique: true
    },
    amount: {
        type: Number,
        required: true
    },
    currency: {
        type: String,
        default: 'USD'
    },
    method: {
        type: String,
        enum: ['credit_card', 'paypal', 'apple_pay', 'google_pay', 'bank_transfer'],
        required: true
    },
    status: {
        type: String,
        enum: ['pending', 'completed', 'failed', 'refunded', 'cancelled'],
        default: 'pending'
    },
    paymentDetails: {
        // For credit cards
        cardLastFour: String,
        cardBrand: String,
        cardExpiry: String,
        
        // For PayPal
        paypalEmail: String,
        paypalTransactionId: String,
        
        // For bank transfer
        bankName: String,
        accountLastFour: String
    },
    receiptUrl: String,
    refundedAmount: {
        type: Number,
        default: 0
    },
    refundReason: String,
    processedBy: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Employee'
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

// Generate payment ID before saving
paymentSchema.pre('save', async function(next) {
    if (!this.paymentId) {
        const timestamp = Date.now();
        const random = Math.floor(100000 + Math.random() * 900000);
        this.paymentId = `PAY-${timestamp}-${random}`;
    }
    next();
});

module.exports = mongoose.model('Payment', paymentSchema);
