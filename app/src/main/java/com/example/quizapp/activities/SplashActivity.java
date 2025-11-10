package com.example.quizapp.activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.quizapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private CardView cardLogo;
    private ImageView ivLogo;
    private TextView tvAppName, tvTagline, tvLoading;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private static final int SPLASH_DURATION = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Hide status bar for fullscreen
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        mAuth = FirebaseAuth.getInstance();

        startAnimations();
        startProgressBar();

        // Navigate after delay
        new Handler().postDelayed(this::checkUserAndNavigate, SPLASH_DURATION);
    }

    private void initViews() {
        cardLogo = findViewById(R.id.cardLogo);
        ivLogo = findViewById(R.id.ivLogo);
        tvAppName = findViewById(R.id.tvAppName);
        tvTagline = findViewById(R.id.tvTagline);
        tvLoading = findViewById(R.id.tvLoading);
        progressBar = findViewById(R.id.progressBar);
    }

    private void startAnimations() {
        // Logo scale animation
        cardLogo.setScaleX(0f);
        cardLogo.setScaleY(0f);
        cardLogo.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(800)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // App name fade in
        new Handler().postDelayed(() -> {
            tvAppName.animate()
                    .alpha(1f)
                    .setDuration(600)
                    .start();
        }, 400);

        // Tagline fade in
        new Handler().postDelayed(() -> {
            tvTagline.animate()
                    .alpha(1f)
                    .setDuration(600)
                    .start();
        }, 700);

        // Logo rotation animation (subtle)
        ivLogo.animate()
                .rotation(360f)
                .setDuration(2000)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    private void startProgressBar() {
        // Animate progress bar from 0 to 100
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100);
        progressAnimator.setDuration(SPLASH_DURATION);
        progressAnimator.setInterpolator(new DecelerateInterpolator());
        progressAnimator.start();
    }

    private void checkUserAndNavigate() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Intent intent;
        if (currentUser != null) {
            // User is already logged in, go to MainActivity
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            // User not logged in, go to LoginActivity
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish(); // Close splash activity

        // Optional: Add transition animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}