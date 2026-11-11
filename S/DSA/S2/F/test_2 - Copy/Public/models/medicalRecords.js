// models/MedicalRecords.js
const mongoose = require('mongoose');

const medicalRecordsSchema = new mongoose.Schema({
  record_id: { type: String, required: true, trim: true },
  patient_id: { type: String, required: true, trim: true },
  visit_date: { type: Date, default: Date.now },
  doctor_id: { type: String, required: true, trim: true },
  department: { type: String, trim: true },
  diagnosis: { type: String, trim: true },
  treatment: { type: String, trim: true },
  medications: { type: [String], default: [] },
  tests_ordered: { type: [String], default: [] },
  follow_up: { type: Date }
});

module.exports = mongoose.model('MedicalRecords', medicalRecordsSchema, 'medicalRecords');
