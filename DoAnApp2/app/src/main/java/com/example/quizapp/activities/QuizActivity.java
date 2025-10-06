package com.example.quizapp.activities;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.R;
import com.example.quizapp.data.SettingsManager;
import com.example.quizapp.data.models.Question;
import com.example.quizapp.utils.JsonUtils;

import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {
    private TextView tvQuestion, tvTimer;
    private RadioGroup rgOptions;
    private Button btnNext;

    private List<Question> questions;
    private int current = 0;
    private int score = 0;
    private SettingsManager settings;
    private CountDownTimer perQuestionTimer;
    private CountDownTimer totalTimer;
    private long perQuestionMs = 15000; // 15s mặc định
    private long totalMs = 0;

    private SoundPool soundPool;
    private int sCorrect = -1, sWrong = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        tvQuestion = findViewById(R.id.tvQuestion);
        tvTimer = findViewById(R.id.tvTimer);
        rgOptions = findViewById(R.id.rgOptions);
        btnNext = findViewById(R.id.btnNext);

        settings = new SettingsManager(this);

        // Load câu hỏi (ưu tiên internal, nếu rỗng thì fallback về assets)
        questions = JsonUtils.loadFromInternal(this);
        if (questions == null || questions.isEmpty()) {
            questions = JsonUtils.loadFromAssets(this);
        }

        if (questions == null || questions.isEmpty()) {
            Toast.makeText(this, "Không có câu hỏi để làm", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Trộn và giới hạn số câu hỏi
        Collections.shuffle(questions);
        int n = Math.min(settings.numberOfQuestions(), questions.size());
        if (n <= 0) n = 5; // mặc định 5 câu
        questions = questions.subList(0, n);

        // Timer toàn bài
        if (settings.totalTestTimerMillis() > 0) {
            totalMs = settings.totalTestTimerMillis();
            totalTimer = new CountDownTimer(totalMs, 1000) {
                @Override
                public void onTick(long l) {
                    tvTimer.setText("Total: " + (l / 1000) + "s");
                }

                @Override
                public void onFinish() {
                    finishQuiz();
                }
            }.start();
        } else {
            tvTimer.setText("");
        }

        // SoundPool cho SFX (nếu có file âm thanh trong res/raw)
        try {
            AudioAttributes attr = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder().setAudioAttributes(attr).setMaxStreams(2).build();
            sCorrect = soundPool.load(this, R.raw.correct, 1);
            sWrong = soundPool.load(this, R.raw.wrong, 1);
        } catch (Exception ignored) { }

        showQuestion();

        btnNext.setOnClickListener(v -> checkAnswerAndProceed());
    }

    // Timer cho từng câu
    private void startPerQuestionTimer() {
        if (perQuestionTimer != null) perQuestionTimer.cancel();
        if (!settings.isPerQuestionTimer()) {
            if (settings.totalTestTimerMillis() <= 0) tvTimer.setText("");
            return;
        }
        perQuestionTimer = new CountDownTimer(perQuestionMs, 1000) {
            @Override
            public void onTick(long l) {
                tvTimer.setText("Q timer: " + (l / 1000) + "s");
            }

            @Override
            public void onFinish() {
                playWrong();
                current++;
                if (current < questions.size()) showQuestion();
                else finishQuiz();
            }
        }.start();
    }

    // Hiển thị câu hỏi
    private void showQuestion() {
        rgOptions.removeAllViews();
        Question q = questions.get(current);
        tvQuestion.setText((current + 1) + ". " + q.question);

        if ("TF".equalsIgnoreCase(q.type)) {
            RadioButton rTrue = createRadioButton("True", 0);
            RadioButton rFalse = createRadioButton("False", 1);
            rgOptions.addView(rTrue);
            rgOptions.addView(rFalse);
        } else {
            if (q.options != null) {
                for (int i = 0; i < q.options.size(); i++) {
                    RadioButton rb = createRadioButton(q.options.get(i), i);
                    rgOptions.addView(rb);
                }
            }
        }

        rgOptions.clearCheck();
        startPerQuestionTimer();
    }

    // Tạo RadioButton động
    private RadioButton createRadioButton(String text, int index) {
        RadioButton rb = new RadioButton(this);
        rb.setText(text);
        rb.setId(View.generateViewId());
        rb.setTag(index);
        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        rb.setLayoutParams(lp);
        return rb;
    }

    // Kiểm tra đáp án
    private void checkAnswerAndProceed() {
        int checkedId = rgOptions.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(this, "Chưa chọn đáp án", Toast.LENGTH_SHORT).show();
            return;
        }

        View checkedView = rgOptions.findViewById(checkedId);
        int selectedIndex = (checkedView.getTag() instanceof Integer)
                ? (Integer) checkedView.getTag()
                : rgOptions.indexOfChild(checkedView);

        Question q = questions.get(current);
        boolean ok;
        if ("TF".equalsIgnoreCase(q.type)) {
            boolean ans = (selectedIndex == 0);
            ok = (q.correctTrue != null && q.correctTrue == ans);
        } else {
            ok = (q.correctIndex != null && q.correctIndex == selectedIndex);
        }

        if (ok) {
            score++;
            playCorrect();
        } else {
            playWrong();
        }

        if (perQuestionTimer != null) perQuestionTimer.cancel();
        current++;
        if (current < questions.size()) showQuestion();
        else finishQuiz();
    }

    // Kết thúc bài test
    private void finishQuiz() {
        if (perQuestionTimer != null) perQuestionTimer.cancel();
        if (totalTimer != null) totalTimer.cancel();
        Intent i = new Intent(this, ResultActivity.class);
        i.putExtra("score", score);
        i.putExtra("total", questions.size());
        startActivity(i);
        finish();
    }

    // Hiệu ứng âm thanh
    private void playCorrect() {
        if (settings != null && settings.isSfxOn() && soundPool != null && sCorrect != -1) {
            soundPool.play(sCorrect, 1f, 1f, 0, 0, 1f);
        }
    }

    private void playWrong() {
        if (settings != null && settings.isSfxOn() && soundPool != null && sWrong != -1) {
            soundPool.play(sWrong, 1f, 1f, 0, 0, 1f);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (perQuestionTimer != null) perQuestionTimer.cancel();
        if (totalTimer != null) totalTimer.cancel();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}
