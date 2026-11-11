const express = require("express");

const router = express.Router();

const upload = require("../middleware/uploadMiddleware");

const {
    getProducts,
    getSingleProduct,
    addProduct,
    updateProduct,
    deleteProduct
}
= require("../controllers/productController");

router.get("/", getProducts);

router.get("/:id", getSingleProduct);

router.post(
    "/",
    upload.single("image"),
    addProduct
);

router.put("/:id", updateProduct);

router.delete("/:id", deleteProduct);

module.exports = router;