package com.example.quizapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.quizapp.R;
import com.example.quizapp.data.SettingsManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {

    // Views
    private SwitchCompat swMusic, swSfx, swPerQ;
    private TextInputEditText etNumQ, etPerQTime; // đổi tên
    private Button btnSaveSettings;

    // Data
    private SettingsManager settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Lấy UID của user hiện tại
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user != null ? user.getUid() : "guest";

        // Khởi tạo SettingsManager riêng cho user
        settings = new SettingsManager(this, uid);

        initViews();
        loadSettings();
        setupListeners();
    }

    private void initViews() {
        swMusic = findViewById(R.id.swMusic);
        swSfx = findViewById(R.id.swSfx);
        swPerQ = findViewById(R.id.swPerQ);
        etNumQ = findViewById(R.id.etNumQ);
        etPerQTime = findViewById(R.id.etTotalTimer); // đổi tên cho rõ ràng
        btnSaveSettings = findViewById(R.id.btnSaveSettings);
    }

    private void loadSettings() {
        swMusic.setChecked(settings.isMusicOn());
        swSfx.setChecked(settings.isSfxOn());
        swPerQ.setChecked(settings.isPerQuestionTimer());
        etNumQ.setText(String.valueOf(settings.numberOfQuestions()));
        etPerQTime.setText(String.valueOf(settings.getPerQuestionMillis() / 1000)); // hiển thị giây
    }

    private void setupListeners() {
        swMusic.setOnCheckedChangeListener((buttonView, isChecked) -> settings.setMusicOn(isChecked));
        swSfx.setOnCheckedChangeListener((buttonView, isChecked) -> settings.setSfxOn(isChecked));
        swPerQ.setOnCheckedChangeListener((buttonView, isChecked) -> settings.setPerQuestionTimer(isChecked));
        btnSaveSettings.setOnClickListener(v -> saveSettings());
    }

    private void saveSettings() {
        try {
            String numQStr = etNumQ.getText().toString().trim();
            String perQStr = etPerQTime.getText().toString().trim();

            if (numQStr.isEmpty()) {
                etNumQ.setError("Please enter number of questions");
                etNumQ.requestFocus();
                return;
            }

            if (perQStr.isEmpty()) {
                etPerQTime.setError("Please enter time per question (seconds)");
                etPerQTime.requestFocus();
                return;
            }

            int numQuestions = Integer.parseInt(numQStr);
            int perQSeconds = Integer.parseInt(perQStr);

            if (numQuestions < 1) {
                etNumQ.setError("Must be at least 1 question");
                etNumQ.requestFocus();
                return;
            }

            if (numQuestions > 20) {
                etNumQ.setError("Maximum 20 questions");
                etNumQ.requestFocus();
                return;
            }

            if (perQSeconds < 1) {
                etPerQTime.setError("Must be at least 1 second per question");
                etPerQTime.requestFocus();
                return;
            }

            // Lưu settings
            settings.setNumberOfQuestions(numQuestions);
            settings.setPerQuestionMillis(perQSeconds * 1000L); // lưu ms

            Toast.makeText(this, "Settings saved successfully! ✓", Toast.LENGTH_SHORT).show();
            finish();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }
}
