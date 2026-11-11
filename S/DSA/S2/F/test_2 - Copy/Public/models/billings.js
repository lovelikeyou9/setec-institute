const mongoose = require('mongoose');

const billingsSchema = new mongoose.Schema({
    billing_id: { type: String, required: true, trim: true },
    patient_id: { type: String, required: true, trim: true },
    date: { type: Date, required: true },
    amount: { type: Number, required: true },
    payment_method: { type: String, required: true, trim: true },
    status: { type: String, required: true, trim: true }
}, {
    versionKey: false
});

module.exports = mongoose.model('Billings', billingsSchema);
