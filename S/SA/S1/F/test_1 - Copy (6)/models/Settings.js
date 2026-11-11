const mongoose = require('mongoose');

const settingsSchema = new mongoose.Schema({
    key: {
        type: String,
        required: true,
        unique: true
    },
    value: mongoose.Schema.Types.Mixed,
    type: {
        type: String,
        enum: ['string', 'number', 'boolean', 'array', 'object'],
        required: true
    },
    category: {
        type: String,
        enum: ['general', 'payment', 'shipping', 'tax', 'email', 'seo', 'social'],
        default: 'general'
    },
    label: String,
    description: String,
    isEditable: {
        type: Boolean,
        default: true
    },
    options: [String], // For select/dropdown settings
    createdAt: { 
        type: Date, 
        default: Date.now 
    },
    updatedAt: { 
        type: Date, 
        default: Date.now 
    }
});

module.exports = mongoose.model('Settings', settingsSchema);
