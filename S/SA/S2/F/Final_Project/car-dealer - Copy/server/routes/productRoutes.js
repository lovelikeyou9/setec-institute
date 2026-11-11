const express = require("express");

const router = express.Router();

const productController =
require("../controllers/productController");

router.post(
    "/add-product",
    productController.addProduct
);

router.get(
    "/products",
    productController.getProducts
);

router.delete(
    "/delete-product/:id",
    productController.deleteProduct
);

router.put(
    "/edit-product/:id",
    productController.editProduct
);

module.exports = router;