import mongoose from "mongoose";

const questionSchema = new mongoose.Schema({
  type: { type: String, enum: ["truefalse", "multiple"], required: true },
  questionText: { type: String, required: true },
  options: [{ type: String }], 
  correctAnswer: { type: String, required: true },
  createdBy: { type: String, default: "admin" },
});

export default mongoose.model("Question", questionSchema);
