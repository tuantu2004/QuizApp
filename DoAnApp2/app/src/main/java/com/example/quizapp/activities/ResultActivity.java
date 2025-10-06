package com.example.quizapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.R;

public class ResultActivity extends AppCompatActivity {
    TextView tvResult;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        tvResult = findViewById(R.id.tvResult);
        btnBack = findViewById(R.id.btnBack);

        int score = getIntent().getIntExtra("score", 0);
        int total = getIntent().getIntExtra("total", 0);
        tvResult.setText("Score: " + score + " / " + total);

        btnBack.setOnClickListener(v -> {
            finish();
        });
    }
}
