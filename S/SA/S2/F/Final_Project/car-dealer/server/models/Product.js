const mongoose = require("mongoose");

const ProductSchema = new mongoose.Schema({

    title: String,

    brand: String,

    price: Number,

    image: String,

    description: String,

    stock: Number,

    category: String

});

module.exports = mongoose.model("Product", ProductSchema);