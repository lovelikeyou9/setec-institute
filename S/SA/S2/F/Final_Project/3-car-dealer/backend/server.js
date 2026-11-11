const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');

mongoose.connect('mongodb://localhost:27017/carDealer', {
  useNewUrlParser: true,
  useUnifiedTopology: true
});

const app = express();
app.use(cors());
app.use(express.json());

// Routes
app.use('/auth', require('./routes/auth'));
app.use('/cars', require('./routes/cars'));
app.use('/orders', require('./routes/orders'));

app.listen(5000, () => console.log('Server running on http://localhost:5000'));
