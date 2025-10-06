import mongoose from "mongoose";

const resultSchema = new mongoose.Schema({
  userId: { type: mongoose.Schema.Types.ObjectId, ref: "User", required: true },
  score: { type: Number, required: true },
  totalQuestions: { type: Number, required: true },
  correctAnswers: { type: Number, required: true },
  createdAt: { type: Date, default: Date.now },
});

export default mongoose.model("Result", resultSchema);
