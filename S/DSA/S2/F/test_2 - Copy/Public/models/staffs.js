const mongoose = require('mongoose');

const staffsSchema = new mongoose.Schema({
    staff_id: { type: String, required: true, trim: true, unique: true },
    name: { type: String, required: true, trim: true },
    role: { type: String, trim: true },            // e.g., Doctor, Nurse, Admin
    department: { type: String, trim: true },
    email: { type: String, trim: true, lowercase: true, unique: true },
    phone: { type: String, trim: true }
}, { versionKey: false });

module.exports = mongoose.model('Staffs', staffsSchema);
