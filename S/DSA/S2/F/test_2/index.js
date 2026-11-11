const express = require('express');
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const Appointments = require('./Public/models/appointments');

const app = express();
const PORT = 3000;

const path = require('path');
app.use(express.static(path.join(__dirname, 'Public')));

app.use(express.urlencoded({ extended: true }));


app.use(bodyParser.json());
mongoose.connect('mongodb://127.0.0.1:27017/hospital', {
  useNewUrlParser: true,
  useUnifiedTopology: true,
});
const db = mongoose.connection;
db.on('error', console.error.bind(console, 'MongoDB connection error:'));
db.once('open', () => console.log('Connected to MongoDB locally'));




// Add to appointments
app.post('/submit', async (req, res) => {
  const { appointment_id, patient_id, doctor_id, department, appointment_date, duration, status, reason, notes } = req.body;
  await Appointments.collection.insertOne({ appointment_id, patient_id, doctor_id, department, appointment_date, duration, status, reason, notes });

  res.redirect('/appointments.html');
  es.status(500).send('Error saving category: ' + error.message);
});

// delete appointment
app.post('/delete/:id', async (req, res) => {
  try {
    await Appointments.findByIdAndDelete(req.params.id);
    res.redirect('/appointments.html');
  } catch (error) {
    res.status(500).send('Error deleting appointment: ' + error.message);
  }
});

// list Data appointment in new form with html
app.get('/appointments', async (req, res) => {
  try {
    const appointments = await Appointments.find(); // this fetches all documents

    let html = `
    <!DOCTYPE html>
    <html lang="en">
    <head>
      <meta charset="UTF-8" />
      <meta name="viewport" content="width=device-width, initial-scale=1" />
      <title>Appointments List</title>
      <style>
        body {
          font-family: Arial, sans-serif;
          padding: 20px;
          background-color: #f5f7fb;
        }
        h1 {
          color: #333;
        }
        table {
          width: 100%;
          border-collapse: collapse;
          background: white;
          box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        th, td {
          padding: 12px 15px;
          border: 1px solid #ddd;
          text-align: left;
        }
        th {
          background-color: #2563eb; /* Blue */
          color: white;
        }
        tr:nth-child(even) {
          background-color: #f2f7ff;
        }
      </style>
    </head>
    <body>
      <h1>Appointments</h1>
      <table>
        <thead>
          <tr>
            <th>Appointment ID</th>
            <th>Patient ID</th>
            <th>Doctor ID</th>
            <th>Department</th>
            <th>Date</th>
            <th>Duration (min)</th>
            <th>Status</th>
            <th>Reason</th>
            <th>Notes</th>
          </tr>
        </thead>
        <tbody>
          ${appointments.map(appt => `
            <tr>
              <td>${appt.appointment_id}</td>
              <td>${appt.patient_id}</td>
              <td>${appt.doctor_id}</td>
              <td>${appt.department}</td>
              <td>${new Date(appt.appointment_date).toLocaleString()}</td>
              <td>${appt.duration}</td>
              <td>${appt.status}</td>
              <td>${appt.reason || ''}</td>
              <td>${appt.notes || ''}</td>
            </tr>
          `).join('')}
        </tbody>
      </table>
    </body>
    </html>`;

    res.send(html);
  } catch (error) {
    res.status(500).send('Error fetching appointments: ' + error.message);
  }
});

// display data to update on form
app.get('/update/:id', async (req, res) => {
  try {
    const appointments = await Appointments.findById(req.params.id);
    if (!appointments) return res.status(404).send('Category not found');

    const html = `
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>Edit Appointment</title>
<style>
  body {
    font-family: Arial, sans-serif;
    background: #f5f7fb;
    margin: 0; padding: 20px;
  }
  .container {
    max-width: 600px;
    background: white;
    padding: 20px 30px;
    margin: 40px auto;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  }
  h1 {
    text-align: center;
    color: #1b2230;
  }
  form {
    display: flex;
    flex-direction: column;
    gap: 15px;
  }
  label {
    font-weight: 600;
    color: #334155;
  }
  input, select, textarea {
    padding: 10px;
    border-radius: 6px;
    border: 1px solid #cbd5e1;
    font-size: 1rem;
  }
  textarea {
    resize: vertical;
    min-height: 80px;
  }
  button {
    background-color: #2563eb;
    color: white;
    padding: 12px;
    font-weight: 700;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    transition: background-color 0.2s ease;
  }
  button:hover {
    background-color: #1d4ed8;
  }
</style>
</head>
<body>
  <div class="container">
    <h1>Edit Appointment</h1>
    <form action="/update/${appointments._id}" method="POST">
      <label for="appointment_id">Appointment ID</label>
      <input id="appointment_id" name="appointment_id" value="${appointments.appointment_id}" required />

      <label for="patient_id">Patient ID</label>
      <input id="patient_id" name="patient_id" value="${appointments.patient_id}" required />

      <label for="doctor_id">Doctor ID</label>
      <input id="doctor_id" name="doctor_id" value="${appointments.doctor_id}" required />

      <label for="department">Department</label>
      <input id="department" name="department" value="${appointments.department}" required />

      <label for="appointment_date">Appointment Date</label>
      <input id="appointment_date" name="appointment_date" type="datetime-local" value="${new Date(appointments.appointment_date).toISOString().slice(0, 16)}" required />

      <label for="duration">Duration (minutes)</label>
      <input id="duration" name="duration" type="number" value="${appointments.duration}" required />

      <label for="status">Status</label>
      <select id="status" name="status" required>
        <option value="Scheduled" ${appointments.status === "Scheduled" ? "selected" : ""}>Scheduled</option>
        <option value="Completed" ${appointments.status === "Completed" ? "selected" : ""}>Completed</option>
        <option value="Cancelled" ${appointments.status === "Cancelled" ? "selected" : ""}>Cancelled</option>
      </select>

      <label for="reason">Reason</label>
      <input id="reason" name="reason" value="${appointments.reason || ""}" />

      <label for="notes">Notes</label>
      <textarea id="notes" name="notes">${appointments.notes || ""}</textarea>

      <button type="submit">Update Appointment</button>
    </form>
  </div>
</body>
</html>
`;

    res.send(html);
  } catch (error) {
    res.status(500).send('Error loading update form: ' + error.message);
  }
});

// update appointment data
app.post('/update/:id', async (req, res) => {

  const { appointment_id, patient_id, doctor_id, department, appointment_date, duration, status, reason, notes } = req.body;

  await Appointments.findByIdAndUpdate(req.params.id, {
    appointment_id, patient_id, doctor_id, department, appointment_date,
    duration, status, reason, notes
  });

  res.redirect('/appointments'); // redirect to your appointments list page


});



// Start server
app.listen(PORT, () => {
  console.log(`Server running at http://localhost:${PORT}`);
});
