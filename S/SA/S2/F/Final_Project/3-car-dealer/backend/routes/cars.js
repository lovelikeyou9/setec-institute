const express = require('express');
const Car = require('../models/Car');
const router = express.Router();

// Get all cars
router.get('/', async (req, res) => {
  const cars = await Car.find();
  res.json(cars);
});

// Get single car
router.get('/:id', async (req, res) => {
  const car = await Car.findById(req.params.id);
  res.json(car);
});

// Add new car
router.post('/', async (req, res) => {
  const car = new Car(req.body);
  await car.save();
  res.json({ message: 'Car added successfully' });
});

module.exports = router;
