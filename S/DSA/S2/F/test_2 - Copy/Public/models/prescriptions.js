const mongoose = require('mongoose');

const prescriptionsSchema = new mongoose.Schema({
    prescription_id: { type: String, required: true, trim: true, unique: true },
    patient_id: { type: String, required: true, trim: true, ref: 'Patient' },
    doctor_id: { type: String, required: true, trim: true, ref: 'Doctor' },
    medication: { type: String, required: true, trim: true },
    dosage: { type: String, trim: true },
    frequency: { type: String, trim: true },
    start_date: { type: Date, required: true },
    end_date: { type: Date }
}, { versionKey: false });

module.exports = mongoose.model('Prescriptions', prescriptionsSchema);
