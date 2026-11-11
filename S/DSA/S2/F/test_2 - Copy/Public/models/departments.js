const mongoose = require('mongoose');

const departmentsSchema = new mongoose.Schema({
    department_id: { type: String, required: true, trim: true },
    name: { type: String, required: true, trim: true },
    head: { type: String, trim: true },
    location: { type: String, trim: true },
    contact: { type: String, trim: true },
    services: { type: [String], default: [] },
    established: { type: Date }
}, {
    versionKey: false
});

module.exports = mongoose.model('Departments', departmentsSchema);
