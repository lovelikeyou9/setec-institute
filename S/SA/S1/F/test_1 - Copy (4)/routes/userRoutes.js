const express = require("express");
const router = express.Router();
const auth = require("../middlewares/auth");
const { getProfile } = require("../controllers/userControllers");

router.get("/profile", auth, getProfile);

module.exports = router;
