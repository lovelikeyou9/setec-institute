const express = require("express");
const cors = require("cors");

require("./db");

const userRoutes = require("./routes/userRoutes");
const productRoutes = require("./routes/productRoutes");
const orderRoutes = require("./routes/orderRoutes");
const cartRoutes = require("./routes/cartRoutes");
const paymentRoutes = require("./routes/paymentRoutes");
const supportRoutes = require("./routes/supportRoutes");

const app = express();

app.use(cors());

app.use(express.json());

/* ROUTES */

app.use("/", userRoutes);

app.use("/", productRoutes);

app.use("/", orderRoutes);

app.use("/", cartRoutes);

app.use("/", paymentRoutes);

app.use("/", supportRoutes);

/* SERVER */

app.listen(3000, () => {

    console.log(
        "Server Running On Port 3000"
    );

});