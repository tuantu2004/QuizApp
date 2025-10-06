import express from "express";
import Question from "../models/Question.js";

const router = express.Router();

router.get("/", async (req, res) => {
  const questions = await Question.find();
  res.json(questions);
});

router.post("/", async (req, res) => {
  try {
    const newQuestion = new Question(req.body);
    await newQuestion.save();
    res.status(201).json(newQuestion);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

export default router;
