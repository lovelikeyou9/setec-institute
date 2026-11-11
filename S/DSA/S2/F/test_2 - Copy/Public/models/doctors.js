const mongoose = require('mongoose');

const educationsSchema = new mongoose.Schema({
    degree: { type: String, trim: true },
    university: { type: String, trim: true },
    hospital: { type: String, trim: true },
    year: { type: Number }
}, { _id: false }); // No separate _id for each education entry

const doctorsSchema = new mongoose.Schema({
    doctor_id: { type: String, required: true, trim: true },
    first_name: { type: String, required: true, trim: true },
    last_name: { type: String, required: true, trim: true },
    specialization: { type: String, trim: true },
    department: { type: String, trim: true },
    phone: { type: String, trim: true },
    email: { type: String, trim: true },
    license_number: { type: String, trim: true },
    years_of_experience: { type: Number },
    education: { type: [educationsSchema], default: [] }
}, {
    versionKey: false
});

module.exports = mongoose.model('Doctors', doctorsSchema);
