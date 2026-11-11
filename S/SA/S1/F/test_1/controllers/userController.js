exports.getProfile = async (req, res) => {
  try {
    res.json(req.user); // ✅ logged-in user ONLY
  } catch (err) {
    res.status(500).json({ message: "Server error" });
  }
};
