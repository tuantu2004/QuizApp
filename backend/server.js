import express from "express";
import mongoose from "mongoose";
import cors from "cors";
import dotenv from "dotenv";
import userRoutes from "./routes/userRoutes.js";
import questionRoutes from "./routes/questionRoutes.js";
import resultRoutes from "./routes/resultRoutes.js";
import Quiz from "./models/Quiz.js";

dotenv.config();

const app = express();


app.use(cors());
app.use(express.json());


app.use("/api/users", userRoutes);
app.use("/api/questions", questionRoutes);
app.use("/api/results", resultRoutes);


mongoose
  .connect(process.env.MONGO_URI)
  .then(async () => {
    console.log("✅ Connected to MongoDB Atlas");

    const count = await Quiz.countDocuments();

    if (count === 0) {
      await Quiz.create({
        title: "JavaScript Basics",
        description: "Test your knowledge of basic JS concepts",
        category: "Programming", 
        questions: [
          {
            questionText: "What does '===' mean in JavaScript?",
            options: ["Equal", "Strict Equal", "Assign", "Compare"],
            correctAnswer: "Strict Equal",
          },
          {
            questionText: "Which company developed JavaScript?",
            options: ["Netscape", "Google", "Microsoft", "Oracle"],
            correctAnswer: "Netscape",
          },
        ],
      });
      console.log("🌱 Sample quiz inserted!");
    } else {
      console.log("📚 Quizzes already exist, skip seeding.");
    }
  })
  .catch((error) => console.error("❌ MongoDB connection error:", error));

const PORT = process.env.PORT || 5000;
app.listen(PORT, () => console.log(`🚀 Server running on port ${PORT}`));
