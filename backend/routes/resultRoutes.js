import express from "express";
import Result from "../models/Result.js";

const router = express.Router();

router.post("/", async (req, res) => {
  try {
    const newResult = new Result(req.body);
    await newResult.save();
    res.status(201).json(newResult);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

router.get("/:userId", async (req, res) => {
  const results = await Result.find({ userId: req.params.userId });
  res.json(results);
});

export default router;
