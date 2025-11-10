package com.example.quizapp.data.models;

import java.util.List;

public class Question {
    private String type; // "MC" hoặc "TF"
    private String question;
    private List<String> options;
    private Integer correctIndex;
    private Boolean correctTrue;

    public Question() { } // Bắt buộc cho Firestore

    public Question(String type, String question, List<String> options, Integer correctIndex, Boolean correctTrue) {
        this.type = type;
        this.question = question;
        this.options = options;
        this.correctIndex = correctIndex;
        this.correctTrue = correctTrue;
    }

    public String getType() { return type; }
    public String getQuestion() { return question; }
    public List<String> getOptions() { return options; }
    public Integer getCorrectIndex() { return correctIndex; }
    public Boolean isCorrectTrue() { return correctTrue; }
}
