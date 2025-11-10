package com.example.quizapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.quizapp.R;
import com.example.quizapp.data.SettingsManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Views
    private ImageView ivAvatar;
    private TextView tvUserName, tvQuizCount, tvScoreAvg;
    private Button btnChangeAvatar;
    private CardView cardStartQuiz, cardCreate, cardSettings, cardLogout;

    // Data
    private SettingsManager settings;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    int[] avatars = {
            R.drawable.avatar1,
            R.drawable.avatar2,
            R.drawable.avatar3,
            R.drawable.avatar4,
            R.drawable.avatar5,
            R.drawable.avatar6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Khởi tạo SettingsManager theo user hiện tại
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = (user != null) ? user.getUid() : "guest";
        settings = new SettingsManager(this, uid);

        initViews();
        setupListeners();
        loadUserInfo();
        updateAvatar();
    }

    private void initViews() {
        ivAvatar = findViewById(R.id.ivAvatar);
        tvUserName = findViewById(R.id.tvUserName);
        tvQuizCount = findViewById(R.id.tvQuizCount);
        tvScoreAvg = findViewById(R.id.tvScoreAvg);
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
        cardStartQuiz = findViewById(R.id.cardStartQuiz);
        cardCreate = findViewById(R.id.cardCreate);
        cardSettings = findViewById(R.id.cardSettings);
        cardLogout = findViewById(R.id.cardLogout);
    }

    private void setupListeners() {
        btnChangeAvatar.setOnClickListener(v -> startActivity(new Intent(this, AvatarActivity.class)));

        cardStartQuiz.setOnClickListener(v -> startActivity(new Intent(this, QuizActivity.class)));

        cardCreate.setOnClickListener(v -> startActivity(new Intent(this, CreateQuestionActivity.class)));

        cardSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

        cardLogout.setOnClickListener(v -> {
            if (mAuth != null) mAuth.signOut();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String displayName = user.getDisplayName();
            String email = user.getEmail();

            if (displayName != null && !displayName.isEmpty()) {
                tvUserName.setText(displayName);
            } else if (email != null) {
                tvUserName.setText(email.split("@")[0]);
            } else {
                tvUserName.setText("Quiz Master");
            }

            loadQuizStatsFromFirebase();
        } else {
            tvUserName.setText("Quiz Master");
            updateStats(0, 0);
        }
    }

    private void loadQuizStatsFromFirebase() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        DocumentReference userRef = db.collection("users").document(user.getUid());
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                int quizCount = documentSnapshot.contains("quizCount") ?
                        documentSnapshot.getLong("quizCount").intValue() : 0;
                int avgScore = documentSnapshot.contains("avgScore") ?
                        documentSnapshot.getLong("avgScore").intValue() : 0;

                updateStats(quizCount, avgScore);
            } else {
                createDefaultUserStats(userRef);
            }
        }).addOnFailureListener(e -> {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load stats", Toast.LENGTH_SHORT).show();
        });
    }

    private void createDefaultUserStats(DocumentReference userRef) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("quizCount", 0);
        stats.put("avgScore", 0);

        userRef.set(stats, SetOptions.merge())
                .addOnSuccessListener(aVoid -> updateStats(0, 0))
                .addOnFailureListener(e -> Log.e("Firestore", "Failed to create default stats", e));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAvatar();
        loadUserInfo();
    }

    private void updateAvatar() {
        try {
            String avatarUrl = settings.getAvatarUrl(); // Nếu sau này dùng Cloudinary
            int savedIndex = settings.getAvatarIndex(); // Avatar local

            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                // TODO: Load image từ URL (Glide / Picasso)
                // Glide.with(this).load(avatarUrl).into(ivAvatar);
            } else {
                ivAvatar.setImageResource((savedIndex >= 0 && savedIndex < avatars.length) ? avatars[savedIndex] : avatars[0]);
            }
        } catch (Exception e) {
            ivAvatar.setImageResource(R.drawable.avatar1);
            e.printStackTrace();
        }
    }

    public void updateStats(int quizCount, int avgScore) {
        tvQuizCount.setText(String.valueOf(quizCount));
        tvScoreAvg.setText(avgScore + "%");
    }

    public void updateUserStats(int quizCount, int avgScore) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        DocumentReference userRef = db.collection("users").document(user.getUid());
        Map<String, Object> stats = new HashMap<>();
        stats.put("quizCount", quizCount);
        stats.put("avgScore", avgScore);

        userRef.set(stats, SetOptions.merge())
                .addOnSuccessListener(aVoid -> updateStats(quizCount, avgScore))
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Failed to update user stats", e);
                    Toast.makeText(this, "Failed to update stats", Toast.LENGTH_SHORT).show();
                });
    }
}
