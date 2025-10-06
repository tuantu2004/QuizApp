import express from "express";
import User from "../models/User.js";

const router = express.Router();

router.post("/register", async (req, res) => {
  try {
    const { username, password } = req.body;
    const existing = await User.findOne({ username });
    if (existing) return res.status(400).json({ message: "Username already exists" });

    const newUser = new User({ username, password });
    await newUser.save();
    res.status(201).json({ message: "User registered successfully" });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

router.post("/login", async (req, res) => {
  try {
    const { username, password } = req.body;
    const user = await User.findOne({ username, password });
    if (!user) return res.status(400).json({ message: "Invalid username or password" });
    res.json({ message: "Login successful", user });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

router.put("/:id/settings", async (req, res) => {
  try {
    const updated = await User.findByIdAndUpdate(req.params.id, { settings: req.body }, { new: true });
    res.json(updated);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

export default router;
