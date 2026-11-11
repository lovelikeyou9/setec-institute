const mongoose = require('mongoose');

const employeeSchema = new mongoose.Schema({
    user: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true
    },
    employeeId: {
        type: String,
        required: true,
        unique: true
    },
    department: {
        type: String,
        enum: ['sales', 'support', 'warehouse', 'management', 'hr'],
        required: true
    },
    position: String,
    hireDate: {
        type: Date,
        default: Date.now
    },
    salary: Number,
    office: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Office'
    },
    manager: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Employee'
    },
    employmentStatus: {
        type: String,
        enum: ['active', 'on-leave', 'terminated', 'retired'],
        default: 'active'
    },
    skills: [String],
    emergencyContact: {
        name: String,
        relationship: String,
        phone: String
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

module.exports = mongoose.model('Employee', employeeSchema);
