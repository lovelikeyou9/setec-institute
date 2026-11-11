const express = require("express");
const cors = require("cors");
const mongoose = require("mongoose");
const jwt = require("jsonwebtoken");

const app = express();
app.use(cors());
app.use(express.json());

// DB
mongoose.connect("mongodb://127.0.0.1:27017/cardb");

// ================= MODELS =================
const Product = mongoose.model("Product", {
    name: String,
    price: Number,
    image: String,
    description: String
});

const Order = mongoose.model("Order", {
    userId: String,
    items: Array,
    total: Number,
    status: String
});

const User = mongoose.model("User", {
    email: String,
    password: String,
    role: String
});

// ================= AUTH =================
function auth(req, res, next) {
    const token = req.headers.authorization;
    if (!token) return res.sendStatus(401);

    try {
        req.user = jwt.verify(token, "secret");
        next();
    } catch {
        res.sendStatus(403);
    }
}

// ================= PRODUCTS =================
app.get("/api/products", async (req, res) => {
    res.json(await Product.find());
});

app.get("/api/products/:id", async (req, res) => {
    res.json(await Product.findById(req.params.id));
});

app.post("/api/products", auth, async (req, res) => {
    res.json(await Product.create(req.body));
});

app.delete("/api/products/:id", auth, async (req, res) => {
    res.json(await Product.findByIdAndDelete(req.params.id));
});

// ================= ORDERS =================
app.post("/api/orders", async (req, res) => {
    res.json(await Order.create(req.body));
});

app.get("/api/orders", auth, async (req, res) => {
    res.json(await Order.find());
});

// ================= LOGIN =================
app.post("/api/login", async (req, res) => {
    const user = { id: 1, role: "admin" };

    const token = jwt.sign(user, "secret", { expiresIn: "7d" });

    res.json({ token });
});

app.listen(5000, () => console.log("Server running"));