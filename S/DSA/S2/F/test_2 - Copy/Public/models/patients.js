const mongoose = require('mongoose');

const addressSchema = new mongoose.Schema({
    street: { type: String, trim: true },
    city: { type: String, trim: true },
    state: { type: String, trim: true },
    zip: { type: String, trim: true }
}, { _id: false });

const emergencyContactsSchema = new mongoose.Schema({
    name: { type: String, trim: true },
    relationship: { type: String, trim: true },
    phone: { type: String, trim: true }
}, { _id: false });

const patientsSchema = new mongoose.Schema({
    patient_id: { type: String, required: true, trim: true, unique: true },
    first_name: { type: String, required: true, trim: true },
    last_name: { type: String, required: true, trim: true },
    date_of_birth: { type: Date },
    gender: { type: String, trim: true },
    address: { type: addressSchema },
    phone: { type: String, trim: true },
    email: { type: String, trim: true, lowercase: true, unique: true },
    blood_type: { type: String, trim: true },
    allergies: { type: [String], default: [] },
    emergency_contact: { type: emergencyContactsSchema },
    registration_date: { type: Date, default: Date.now }
}, { versionKey: false });

module.exports = mongoose.model('Patients', patientsSchema);
