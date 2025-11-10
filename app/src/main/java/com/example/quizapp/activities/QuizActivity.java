package com.example.quizapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.quizapp.R;
import com.example.quizapp.data.MusicManager;
import com.example.quizapp.data.SettingsManager;
import com.example.quizapp.data.models.Question;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizActivity extends AppCompatActivity {

    // Views
    private TextView tvProgress, tvTimer, tvScore, tvQuestion;
    private TextView tvOptionA, tvOptionB, tvOptionC, tvOptionD;
    private CardView cardOptionA, cardOptionB, cardOptionC, cardOptionD;
    private ImageView ivCheckA, ivCheckB, ivCheckC, ivCheckD;
    private ProgressBar progressBar;
    private Button btnNext;

    // Data
    private List<Question> questions = new ArrayList<>();
    private int currentQuestion = 0;
    private int score = 0;
    private int selectedOption = -1;

    // Settings & Timer
    private SettingsManager settings;
    private CountDownTimer perQuestionTimer;
    private long perQuestionMs = 30000;
    private long timeLeftInMillis = 30000;

    // Sound
    private SoundPool soundPool;
    private int sCorrect = -1, sWrong = -1;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // Arrays
    private CardView[] optionCards;
    private TextView[] optionTexts;
    private ImageView[] optionChecks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        initViews();
        initFirebase();
        initSettings();
        initSound();
        setupListeners();
        loadQuestionsFromFirestore();

        // Dừng nhạc app MainActivity nếu đang chạy
        MusicManager.stopAppMusic();
    }

    private void initViews() {
        tvProgress = findViewById(R.id.tvProgress);
        tvTimer = findViewById(R.id.tvTimer);
        tvScore = findViewById(R.id.tvScore);
        tvQuestion = findViewById(R.id.tvQuestion);

        tvOptionA = findViewById(R.id.tvOptionA);
        tvOptionB = findViewById(R.id.tvOptionB);
        tvOptionC = findViewById(R.id.tvOptionC);
        tvOptionD = findViewById(R.id.tvOptionD);

        cardOptionA = findViewById(R.id.cardOptionA);
        cardOptionB = findViewById(R.id.cardOptionB);
        cardOptionC = findViewById(R.id.cardOptionC);
        cardOptionD = findViewById(R.id.cardOptionD);

        ivCheckA = findViewById(R.id.ivCheckA);
        ivCheckB = findViewById(R.id.ivCheckB);
        ivCheckC = findViewById(R.id.ivCheckC);
        ivCheckD = findViewById(R.id.ivCheckD);

        progressBar = findViewById(R.id.progressBar);
        btnNext = findViewById(R.id.btnNext);

        optionCards = new CardView[]{cardOptionA, cardOptionB, cardOptionC, cardOptionD};
        optionTexts = new TextView[]{tvOptionA, tvOptionB, tvOptionC, tvOptionD};
        optionChecks = new ImageView[]{ivCheckA, ivCheckB, ivCheckC, ivCheckD};
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    private void initSettings() {
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = (user != null) ? user.getUid() : "guest";
        settings = new SettingsManager(this, uid);

        perQuestionMs = settings.isPerQuestionTimer() ? settings.getPerQuestionMillis() : 30000;
    }

    private void initSound() {
        try {
            AudioAttributes attr = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(attr)
                    .setMaxStreams(2)
                    .build();
            sCorrect = soundPool.load(this, R.raw.correct, 1);
            sWrong = soundPool.load(this, R.raw.wrong, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupListeners() {
        for (int i = 0; i < optionCards.length; i++) {
            final int index = i;
            optionCards[i].setOnClickListener(v -> selectOption(index));
        }
        btnNext.setOnClickListener(v -> checkAnswerAndProceed());
    }

    private void loadQuestionsFromFirestore() {
        db.collection("questions")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        questions.add(doc.toObject(Question.class));
                    }

                    if (questions.isEmpty()) {
                        Toast.makeText(this, "Không có câu hỏi trong Firestore", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }

                    Collections.shuffle(questions);
                    int n = Math.min(settings.numberOfQuestions(), questions.size());
                    if (n <= 0) n = 5;
                    questions = questions.subList(0, n);

                    progressBar.setMax(questions.size());
                    updateProgress();
                    updateScore();
                    showQuestion();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải câu hỏi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    private void showQuestion() {
        if (currentQuestion >= questions.size()) {
            finishQuiz();
            return;
        }

        Question q = questions.get(currentQuestion);
        tvQuestion.setText(q.getQuestion());

        if ("TF".equalsIgnoreCase(q.getType())) {
            tvOptionA.setText("True");
            tvOptionB.setText("False");
            cardOptionC.setVisibility(View.GONE);
            cardOptionD.setVisibility(View.GONE);
        } else {
            List<String> options = q.getOptions();
            if (options != null && options.size() > 0) {
                tvOptionA.setText(options.get(0));
                tvOptionB.setText(options.size() > 1 ? options.get(1) : "");
                tvOptionC.setText(options.size() > 2 ? options.get(2) : "");
                tvOptionD.setText(options.size() > 3 ? options.get(3) : "");

                cardOptionC.setVisibility(options.size() > 2 ? View.VISIBLE : View.GONE);
                cardOptionD.setVisibility(options.size() > 3 ? View.VISIBLE : View.GONE);
            }
        }

        resetOptions();
        updateProgress();
        startPerQuestionTimer();
    }

    private void startPerQuestionTimer() {
        if (!settings.isPerQuestionTimer()) {
            tvTimer.setText("⏱ --");
            return;
        }

        if (perQuestionTimer != null) perQuestionTimer.cancel();
        timeLeftInMillis = perQuestionMs;

        perQuestionTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                int seconds = (int) (millisUntilFinished / 1000);
                tvTimer.setText(String.format("⏱ %02ds", seconds));
                tvTimer.setTextColor(seconds <= 10 ? Color.RED : Color.parseColor("#2196F3"));
            }

            @Override
            public void onFinish() {
                tvTimer.setText("00s");
                playWrong();
                Toast.makeText(QuizActivity.this, "Hết thời gian!", Toast.LENGTH_SHORT).show();
                currentQuestion++;
                if (currentQuestion < questions.size()) showQuestion();
                else finishQuiz();
            }
        }.start();
    }

    private void selectOption(int option) {
        selectedOption = option;
        for (int i = 0; i < optionCards.length; i++) {
            optionCards[i].setCardBackgroundColor(Color.WHITE);
        }
        optionCards[option].setCardBackgroundColor(Color.parseColor("#E3F2FD")); // Highlight chọn
        btnNext.setEnabled(true);
        btnNext.setAlpha(1f);
    }


    private void checkAnswerAndProceed() {
        if (selectedOption == -1) {
            Toast.makeText(this, "Vui lòng chọn đáp án", Toast.LENGTH_SHORT).show();
            return;
        }

        Question q = questions.get(currentQuestion);
        boolean isCorrect;

        if ("TF".equalsIgnoreCase(q.getType())) {
            boolean userAnswer = (selectedOption == 0);
            isCorrect = (q.isCorrectTrue() == userAnswer);
        } else {
            isCorrect = (q.getCorrectIndex() != null && q.getCorrectIndex() == selectedOption);
        }


        if (isCorrect) {
            score += 10;
            playCorrect();

            optionChecks[selectedOption].setVisibility(View.VISIBLE);
            optionChecks[selectedOption].setImageResource(R.drawable.ic_check); // icon check
            optionCards[selectedOption].setCardBackgroundColor(Color.parseColor("#C8E6C9")); // màu xanh đúng
        } else {
            playWrong();
            optionChecks[selectedOption].setVisibility(View.VISIBLE);
            optionChecks[selectedOption].setImageResource(R.drawable.ic_cross); // icon x
            optionCards[selectedOption].setCardBackgroundColor(Color.parseColor("#FFCDD2")); // màu đỏ sai


            int correct = q.getCorrectIndex() != null ? q.getCorrectIndex() : (q.isCorrectTrue() ? 0 : 1);
            optionChecks[correct].setVisibility(View.VISIBLE);
            optionChecks[correct].setImageResource(R.drawable.ic_check);
            optionCards[correct].setCardBackgroundColor(Color.parseColor("#C8E6C9")); // màu xanh đúng
        }

        updateScore();
        if (perQuestionTimer != null) perQuestionTimer.cancel();

        btnNext.postDelayed(() -> {
            currentQuestion++;
            if (currentQuestion < questions.size()) showQuestion();
            else finishQuiz();
        }, 1500); // delay để người dùng nhìn thấy đúng/sai
    }


    private void showCorrectAnswer(int index) {
        if (index >= 0 && index < optionCards.length)
            optionCards[index].setCardBackgroundColor(Color.parseColor("#C8E6C9"));
    }

    private void showWrongAnswer(int index) {
        if (index >= 0 && index < optionCards.length)
            optionCards[index].setCardBackgroundColor(Color.parseColor("#FFCDD2"));
    }

    private void resetOptions() {
        selectedOption = -1;
        btnNext.setEnabled(false);
        btnNext.setAlpha(0.5f);
        for (int i = 0; i < optionCards.length; i++) {
            optionCards[i].setCardBackgroundColor(Color.WHITE);
            optionChecks[i].setVisibility(View.GONE);
        }
    }

    private void updateProgress() {
        tvProgress.setText("Câu " + (currentQuestion + 1) + "/" + questions.size());
        progressBar.setProgress(currentQuestion + 1);
    }

    private void updateScore() {
        tvScore.setText("Điểm: " + score);
    }

    private void finishQuiz() {
        if (perQuestionTimer != null) perQuestionTimer.cancel();
        stopQuizMusic();

        // Resume app music nếu bật
        if (settings.isMusicOn()) {
            MusicManager.startAppMusic(this, true);
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            DocumentReference userRef = db.collection("users").document(user.getUid());
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                long prevQuizCount = documentSnapshot.contains("quizCount") ? documentSnapshot.getLong("quizCount") : 0;
                long prevAvgScore = documentSnapshot.contains("avgScore") ? documentSnapshot.getLong("avgScore") : 0;

                long newQuizCount = prevQuizCount + 1;
                long newAvgScore = (prevAvgScore * prevQuizCount + score) / newQuizCount;

                Map<String, Object> stats = new HashMap<>();
                stats.put("quizCount", newQuizCount);
                stats.put("avgScore", newAvgScore);

                userRef.set(stats, SetOptions.merge());
            });
        }

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("total", questions.size());
        startActivity(intent);
        finish();
    }

    private void playCorrect() {
        if (settings.isSfxOn() && soundPool != null && sCorrect != -1)
            soundPool.play(sCorrect, 1f, 1f, 0, 0, 1f);
    }

    private void playWrong() {
        if (settings.isSfxOn() && soundPool != null && sWrong != -1)
            soundPool.play(sWrong, 1f, 1f, 0, 0, 1f);
    }

    private void startQuizMusic() {
        MusicManager.startQuizMusic(this, settings.isMusicOn());
    }

    private void stopQuizMusic() {
        MusicManager.stopQuizMusic();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startQuizMusic();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopQuizMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (perQuestionTimer != null) perQuestionTimer.cancel();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        stopQuizMusic();
    }
}
