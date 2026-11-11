const express = require('express');
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const path = require('path');
const Appointments = require('./Public/models/appointments');
const Billings = require('./Public/models/billings');
const Departments = require('./Public/models/departments');
const Doctors = require('./Public/models/doctors');
const MedicalRecords = require('./Public/models/medicalRecords');
const Patients = require('./Public/models/patients');
const Prescriptions = require('./Public/models/prescriptions');
const Staffs = require('./Public/models/staffs');

const app = express();
const PORT = 3000;

// Serve static files (HTML, CSS, JS)
app.use(express.static(path.join(__dirname, 'Public')));

// Parse incoming JSON and URL-encoded data
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Connect to MongoDB
mongoose.connect('mongodb://127.0.0.1:27017/hospital', {
  useNewUrlParser: true,
  useUnifiedTopology: true,
});

const db = mongoose.connection;
db.on('error', console.error.bind(console, 'MongoDB connection error:'));
db.once('open', () => console.log('Connected to MongoDB'));





/* ====== APPOINTMENTS API ROUTES ====== */

// Get all appointments
app.get('/api/appointments', async (req, res) => {
  try {
    const appointments = await Appointments.find().sort({ created_at: -1 });
    res.json(appointments);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Get one appointment by ID
app.get('/api/appointments/:id', async (req, res) => {
  try {
    const appt = await Appointments.findById(req.params.id);
    if (!appt) return res.status(404).json({ error: 'Appointment not found' });
    res.json(appt);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Create a new appointment
app.post('/api/appointments', async (req, res) => {
  try {
    const { appointment_id, patient_id, doctor_id, department, appointment_date, duration, status, reason, notes } = req.body;
    const newAppt = await Appointments.create({ appointment_id, patient_id, doctor_id, department, appointment_date, duration, status, reason, notes });
    res.status(201).json(newAppt);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Update an appointment
app.put('/api/appointments/:id', async (req, res) => {
  try {
    const { appointment_id, patient_id, doctor_id, department, appointment_date, duration, status, reason, notes } = req.body;
    const updatedAppt = await Appointments.findByIdAndUpdate(
      req.params.id,
      { appointment_id, patient_id, doctor_id, department, appointment_date, duration, status, reason, notes },
      { new: true }
    );
    if (!updatedAppt) return res.status(404).json({ error: 'Appointment not found' });
    res.json(updatedAppt);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Delete an appointment
app.delete('/api/appointments/:id', async (req, res) => {
  try {
    const deleted = await Appointments.findByIdAndDelete(req.params.id);
    if (!deleted) return res.status(404).json({ error: 'Appointment not found' });
    res.json({ message: 'Appointment deleted successfully' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});






/* ====== BILLINGS API ROUTES ====== */

// Get all billings
app.get('/api/billings', async (req, res) => {
  try {
    const billings = await Billings.find().sort({ created_at: -1 });
    res.json(billings);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Get one billing by ID
app.get('/api/billings/:id', async (req, res) => {
  try {
    const bill = await Billings.findById(req.params.id);
    if (!bill) return res.status(404).json({ error: 'Billing not found' });
    res.json(bill);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Create a new billing
app.post('/api/billings', async (req, res) => {
  try {
    const { billing_id, patient_id, date, amount, payment_method, status } = req.body;
    const newBill = await Billings.create({ billing_id, patient_id, date, amount, payment_method, status });
    res.status(201).json(newBill);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Update a billing
app.put('/api/billings/:id', async (req, res) => {
  try {
    const { billing_id, patient_id, date, amount, payment_method, status } = req.body;
    const updatedBill = await Billings.findByIdAndUpdate(
      req.params.id,
      { billing_id, patient_id, date, amount, payment_method, status },
      { new: true }
    );
    if (!updatedBill) return res.status(404).json({ error: 'Billing not found' });
    res.json(updatedBill);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Delete a billing
app.delete('/api/billings/:id', async (req, res) => {
  try {
    const deleted = await Billings.findByIdAndDelete(req.params.id);
    if (!deleted) return res.status(404).json({ error: 'Billing not found' });
    res.json({ message: 'Billing deleted successfully' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});







/* ====== DEPARTMENTS API ROUTES ====== */

// Get all departments
app.get('/api/departments', async (req, res) => {
  try {
    const departments = await Departments.find().sort({ name: 1 });
    res.json(departments);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Get one department by ID
app.get('/api/departments/:id', async (req, res) => {
  try {
    const dept = await Departments.findById(req.params.id);
    if (!dept) return res.status(404).json({ error: 'Department not found' });
    res.json(dept);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Create a new department
app.post('/api/departments', async (req, res) => {
  try {
    const { department_id, name, head, location, contact, services, established } = req.body;
    const newDept = await Departments.create({ department_id, name, head, location, contact, services, established });
    res.status(201).json(newDept);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Update a department
app.put('/api/departments/:id', async (req, res) => {
  try {
    const { department_id, name, head, location, contact, services, established } = req.body;
    const updatedDept = await Departments.findByIdAndUpdate(
      req.params.id,
      { department_id, name, head, location, contact, services, established },
      { new: true }
    );
    if (!updatedDept) return res.status(404).json({ error: 'Department not found' });
    res.json(updatedDept);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Delete a department
app.delete('/api/departments/:id', async (req, res) => {
  try {
    const deleted = await Departments.findByIdAndDelete(req.params.id);
    if (!deleted) return res.status(404).json({ error: 'Department not found' });
    res.json({ message: 'Department deleted successfully' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});





/* ====== DOCTORS API ROUTES ====== */

// Get all doctors
app.get('/api/doctors', async (req, res) => {
  try {
    const doctors = await Doctors.find().sort({ created_at: -1 });
    res.json(doctors);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Get one doctor by ID
app.get('/api/doctors/:id', async (req, res) => {
  try {
    const doc = await Doctors.findById(req.params.id);
    if (!doc) return res.status(404).json({ error: 'Doctor not found' });
    res.json(doc);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Create a new doctor
app.post('/api/doctors', async (req, res) => {
  try {
    const {
      doctor_id,
      first_name,
      last_name,
      specialization,
      department,
      phone,
      email,
      license_number,
      years_of_experience
    } = req.body;

    const newDoctor = await Doctors.create({
      doctor_id,
      first_name,
      last_name,
      specialization,
      department,
      phone,
      email,
      license_number,
      years_of_experience
    });

    res.status(201).json(newDoctor);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Update a doctor
app.put('/api/doctors/:id', async (req, res) => {
  try {
    const {
      doctor_id,
      first_name,
      last_name,
      specialization,
      department,
      phone,
      email,
      license_number,
      years_of_experience
    } = req.body;

    const updatedDoctor = await Doctors.findByIdAndUpdate(
      req.params.id,
      {
        doctor_id,
        first_name,
        last_name,
        specialization,
        department,
        phone,
        email,
        license_number,
        years_of_experience
      },
      { new: true }
    );

    if (!updatedDoctor) return res.status(404).json({ error: 'Doctor not found' });
    res.json(updatedDoctor);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Delete a doctor
app.delete('/api/doctors/:id', async (req, res) => {
  try {
    const deleted = await Doctors.findByIdAndDelete(req.params.id);
    if (!deleted) return res.status(404).json({ error: 'Doctor not found' });
    res.json({ message: 'Doctor deleted successfully' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});







/* ====== MEDICAL RECORDS API ROUTES ====== */

// Get all medical records
app.get('/api/medicalRecords', async (req, res) => {
  try {
    const records = await MedicalRecords.find().sort({ visit_date: -1 });
    res.json(records);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Get one medical record by ID
app.get('/api/medicalRecords/:id', async (req, res) => {
  try {
    const record = await MedicalRecords.findById(req.params.id);
    if (!record) return res.status(404).json({ error: 'Medical record not found' });
    res.json(record);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Create a new medical record
app.post('/api/medicalRecords', async (req, res) => {
  try {
    const { record_id, patient_id, doctor_id, department, diagnosis, treatment, medications, tests_ordered, follow_up } = req.body;
    const newRecord = await MedicalRecords.create({ record_id, patient_id, doctor_id, department, diagnosis, treatment, medications, tests_ordered, follow_up });
    res.status(201).json(newRecord);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Update a medical record
app.put('/api/medicalRecords/:id', async (req, res) => {
  try {
    const { record_id, patient_id, doctor_id, department, diagnosis, treatment, medications, tests_ordered, follow_up } = req.body;
    const updatedRecord = await MedicalRecords.findByIdAndUpdate(
      req.params.id,
      { record_id, patient_id, doctor_id, department, diagnosis, treatment, medications, tests_ordered, follow_up },
      { new: true }
    );
    if (!updatedRecord) return res.status(404).json({ error: 'Medical record not found' });
    res.json(updatedRecord);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Delete a medical record
app.delete('/api/medicalRecords/:id', async (req, res) => {
  try {
    const deleted = await MedicalRecords.findByIdAndDelete(req.params.id);
    if (!deleted) return res.status(404).json({ error: 'Medical record not found' });
    res.json({ message: 'Medical record deleted successfully' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});







/* ====== PATIENTS API ROUTES ====== */

// Get all patients
app.get('/api/patients', async (req, res) => {
  try {
    const patients = await Patients.find().sort({ registration_date: -1 });
    res.json(patients);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Get one patient by ID
app.get('/api/patients/:id', async (req, res) => {
  try {
    const patient = await Patients.findById(req.params.id);
    if (!patient) return res.status(404).json({ error: 'Patient not found' });
    res.json(patient);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Create a new patient
app.post('/api/patients', async (req, res) => {
  try {
    const {
      patient_id, first_name, last_name, date_of_birth, gender,
      phone, email, blood_type, allergies, emergency_contact
    } = req.body;

    const newPatient = await Patients.create({
      patient_id, first_name, last_name, date_of_birth, gender,
      phone, email, blood_type, allergies,
      emergency_contact
    });

    res.status(201).json(newPatient);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Update a patient
app.put('/api/patients/:id', async (req, res) => {
  try {
    const {
      patient_id, first_name, last_name, date_of_birth, gender,
      phone, email, blood_type, allergies, emergency_contact
    } = req.body;

    const updatedPatient = await Patients.findByIdAndUpdate(
      req.params.id,
      { patient_id, first_name, last_name, date_of_birth, gender, phone, email, blood_type, allergies, emergency_contact },
      { new: true }
    );

    if (!updatedPatient) return res.status(404).json({ error: 'Patient not found' });
    res.json(updatedPatient);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Delete a patient
app.delete('/api/patients/:id', async (req, res) => {
  try {
    const deleted = await Patients.findByIdAndDelete(req.params.id);
    if (!deleted) return res.status(404).json({ error: 'Patient not found' });
    res.json({ message: 'Patient deleted successfully' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});






/* ====== PRESCRIPTIONS API ROUTES ====== */

// Get all prescriptions
app.get('/api/prescriptions', async (req, res) => {
    try {
        const prescriptions = await Prescriptions.find().sort({ created_at: -1 });
        res.json(prescriptions);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Get one prescription by ID
app.get('/api/prescriptions/:id', async (req, res) => {
    try {
        const presc = await Prescriptions.findById(req.params.id);
        if (!presc) return res.status(404).json({ error: 'Prescription not found' });
        res.json(presc);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Create a new prescription
app.post('/api/prescriptions', async (req, res) => {
    try {
        const { prescription_id, patient_id, doctor_id, medication, dosage, frequency, start_date, end_date } = req.body;
        const newPresc = await Prescriptions.create({
            prescription_id, patient_id, doctor_id, medication, dosage, frequency, start_date, end_date
        });
        res.status(201).json(newPresc);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Update a prescription
app.put('/api/prescriptions/:id', async (req, res) => {
    try {
        const { prescription_id, patient_id, doctor_id, medication, dosage, frequency, start_date, end_date } = req.body;
        const updatedPresc = await Prescriptions.findByIdAndUpdate(
            req.params.id,
            { prescription_id, patient_id, doctor_id, medication, dosage, frequency, start_date, end_date },
            { new: true }
        );
        if (!updatedPresc) return res.status(404).json({ error: 'Prescription not found' });
        res.json(updatedPresc);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Delete a prescription
app.delete('/api/prescriptions/:id', async (req, res) => {
    try {
        const deleted = await Prescriptions.findByIdAndDelete(req.params.id);
        if (!deleted) return res.status(404).json({ error: 'Prescription not found' });
        res.json({ message: 'Prescription deleted successfully' });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});






/* ====== STAFFS API ROUTES ====== */

// Get all staffs
app.get('/api/staffs', async (req, res) => {
  try {
    const staffs = await Staffs.find().sort({ name: 1 });
    res.json(staffs);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Get one staff by ID
app.get('/api/staffs/:id', async (req, res) => {
  try {
    const staff = await Staffs.findById(req.params.id);
    if (!staff) return res.status(404).json({ error: 'Staff not found' });
    res.json(staff);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Create a new staff
app.post('/api/staffs', async (req, res) => {
  try {
    const { staff_id, name, role, department, email, phone } = req.body;
    const newStaff = await Staffs.create({ staff_id, name, role, department, email, phone });
    res.status(201).json(newStaff);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Update a staff
app.put('/api/staffs/:id', async (req, res) => {
  try {
    const { staff_id, name, role, department, email, phone } = req.body;
    const updatedStaff = await Staffs.findByIdAndUpdate(
      req.params.id,
      { staff_id, name, role, department, email, phone },
      { new: true }
    );
    if (!updatedStaff) return res.status(404).json({ error: 'Staff not found' });
    res.json(updatedStaff);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Delete a staff
app.delete('/api/staffs/:id', async (req, res) => {
  try {
    const deleted = await Staffs.findByIdAndDelete(req.params.id);
    if (!deleted) return res.status(404).json({ error: 'Staff not found' });
    res.json({ message: 'Staff deleted successfully' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});









// Start server
app.listen(PORT, () => {
  console.log(`Server running at http://localhost:${PORT}`);
});
