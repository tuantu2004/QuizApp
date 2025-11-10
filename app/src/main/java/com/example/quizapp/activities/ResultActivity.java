package com.example.quizapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.R;

public class ResultActivity extends AppCompatActivity {

    private ImageView ivTrophy;
    private TextView tvCongrats, tvResultMessage;
    private TextView tvScore, tvOutOf, tvPercentage;
    private TextView tvCorrect, tvWrong, tvTotal;
    private Button btnRetry, btnBack;

    private int score;
    private int totalQuestions;
    private int correctAnswers;
    private int wrongAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Get data from intent
        score = getIntent().getIntExtra("score", 0);
        totalQuestions = getIntent().getIntExtra("total", 10);

        // Calculate stats
        int scorePerQuestion = 10; // Assuming 10 points per question
        correctAnswers = score / scorePerQuestion;
        wrongAnswers = totalQuestions - correctAnswers;

        initViews();
        displayResults();
        setupListeners();
    }

    private void initViews() {
        ivTrophy = findViewById(R.id.ivTrophy);
        tvCongrats = findViewById(R.id.tvCongrats);
        tvResultMessage = findViewById(R.id.tvResultMessage);

        tvScore = findViewById(R.id.tvScore);
        tvOutOf = findViewById(R.id.tvOutOf);
        tvPercentage = findViewById(R.id.tvPercentage);

        tvCorrect = findViewById(R.id.tvCorrect);
        tvWrong = findViewById(R.id.tvWrong);
        tvTotal = findViewById(R.id.tvTotal);

        btnRetry = findViewById(R.id.btnRetry);
        btnBack = findViewById(R.id.btnBack);
    }

    private void displayResults() {
        // Calculate percentage
        int maxScore = totalQuestions * 10;
        int percentage = (int) ((score * 100.0) / maxScore);

        // Set congratulations message based on performance
        if (percentage >= 80) {
            tvCongrats.setText("Excellent!");
            tvResultMessage.setText("Outstanding performance! 🎉");
        } else if (percentage >= 60) {
            tvCongrats.setText("Great Job!");
            tvResultMessage.setText("Well done! Keep it up! 👏");
        } else if (percentage >= 40) {
            tvCongrats.setText("Good Try!");
            tvResultMessage.setText("You can do better! 💪");
        } else {
            tvCongrats.setText("Keep Practicing!");
            tvResultMessage.setText("Practice makes perfect! 📚");
        }

        // Display score
        tvScore.setText(String.valueOf(score));
        tvOutOf.setText("out of " + (totalQuestions * 10));
        tvPercentage.setText(percentage + "%");

        // Display statistics
        tvCorrect.setText(String.valueOf(correctAnswers));
        tvWrong.setText(String.valueOf(wrongAnswers));
        tvTotal.setText(String.valueOf(totalQuestions));

        // Optional: Change trophy icon based on performance
        if (percentage >= 80) {
            ivTrophy.setImageResource(R.drawable.ic_trophy); // Gold trophy
        } else if (percentage >= 60) {
            ivTrophy.setImageResource(R.drawable.ic_trophy); // Silver trophy
        } else {
            ivTrophy.setImageResource(R.drawable.ic_trophy); // Bronze trophy
        }
    }

    private void setupListeners() {
        // Retry button - start a new quiz
        btnRetry.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, QuizActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        // Back button - return to main activity
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        // Go back to main activity instead of quiz
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}