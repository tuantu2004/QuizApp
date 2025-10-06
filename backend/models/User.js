import mongoose from "mongoose";

const userSchema = new mongoose.Schema({
  username: { type: String, required: true, unique: true },
  password: { type: String, required: true },
  avatar: { type: String, default: "default.png" },
  settings: {
    backgroundMusic: { type: Boolean, default: false },
    soundEffects: { type: Boolean, default: false },
    questionTimer: { type: Boolean, default: false },
    totalTimer: { type: Number, default: 0 },
    numberOfQuestions: { type: Number, default: 5 },
  },
});

export default mongoose.model("User", userSchema);
