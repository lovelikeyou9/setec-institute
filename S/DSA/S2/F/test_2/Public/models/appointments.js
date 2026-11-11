const mongoose = require('mongoose');

const appointmentsSchema = new mongoose.Schema({
    appointment_id: { type: String, required: true, trim: true },
    patient_id: { type: String, required: true, trim: true },
    doctor_id: { type: String, required: true, trim: true },
    department: { type: String, required: true, trim: true },
    appointment_date: { type: Date, required: true },
    duration: { type: Number, required: true },
    status: { type: String, required: true, trim: true },
    reason: { type: String, trim: true },
    notes: { type: String, trim: true },
    created_at: { type: Date, default: Date.now }
}, {
    versionKey: false
});

module.exports = mongoose.model('Appointments', appointmentsSchema);
