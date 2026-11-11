const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
    name: { 
        type: String, 
        required: true 
    },
    email: { 
        type: String, 
        required: true, 
        unique: true,
        lowercase: true,
        trim: true
    },
    password: { 
        type: String, 
        required: true 
    },
    role: {
        type: String,
        enum: ['customer', 'employee', 'admin'],
        default: 'customer'
    },
    address: {
        street: String,
        city: String,
        state: String,
        zipCode: String,
        country: { 
            type: String, 
            default: 'USA' 
        }
    },
    phone: String,
    isActive: {
        type: Boolean,
        default: true
    },
    emailVerified: {
        type: Boolean,
        default: false
    },
    lastLogin: Date,
    preferences: {
        newsletter: { type: Boolean, default: true },
        notifications: { type: Boolean, default: true }
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

module.exports = mongoose.model('User', userSchema);
