package com.example.quizapp.data.models;

import java.util.List;

public class Question {
    public int id;
    public String type;         // "MCQ" hoặc "TF"
    public String question;
    public List<String> options;  // null nếu TF
    public Integer correctIndex;  // null nếu TF
    public Boolean correctTrue;   // null nếu MCQ
}
