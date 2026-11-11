const express = require("express");
const cors = require("cors");
const mongoose = require("mongoose");

const app = express();

app.use(cors());
app.use(express.json());

// MongoDB Connect
mongoose.connect("mongodb://127.0.0.1:27017/carDealer");

// Schema
const Car = mongoose.model("Car", {
    name: String,
    price: Number,
    year: Number,
    image: String
});

// GET ALL CARS
app.get("/api/cars", async (req, res) => {
    const cars = await Car.find();
    res.json(cars);
});

// ADD CAR
app.post("/api/cars", async (req, res) => {
    const car = new Car(req.body);
    await car.save();
    res.json(car);
});

// DELETE CAR
app.delete("/api/cars/:id", async (req, res) => {
    await Car.findByIdAndDelete(req.params.id);
    res.json({ message: "Deleted" });
});

app.listen(3000, () => console.log("Server running on port 3000"));