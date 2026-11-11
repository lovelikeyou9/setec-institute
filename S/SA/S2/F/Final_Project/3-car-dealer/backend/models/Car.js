const mongoose = require('mongoose');

const CarSchema = new mongoose.Schema({
  make: String,
  model: String,
  year: Number,
  price: Number,
  mileage: Number,
  description: String,
  images: [String],
  available: { type: Boolean, default: true }
});

module.exports = mongoose.model('Car', CarSchema);
