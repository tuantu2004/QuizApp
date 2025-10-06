package com.example.quizapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.R;
import com.example.quizapp.data.SettingsManager;

public class SettingsActivity extends AppCompatActivity {
    SettingsManager settings;
    Switch swMusic, swSfx, swPerQ;
    EditText etNumQ, etTotalTimer;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = new SettingsManager(this);

        swMusic = findViewById(R.id.swMusic);
        swSfx = findViewById(R.id.swSfx);
        swPerQ = findViewById(R.id.swPerQ);
        etNumQ = findViewById(R.id.etNumQ);
        etTotalTimer = findViewById(R.id.etTotalTimer);
        btnSave = findViewById(R.id.btnSaveSettings);

        // load current
        swMusic.setChecked(settings.isMusicOn());
        swSfx.setChecked(settings.isSfxOn());
        swPerQ.setChecked(settings.isPerQuestionTimer());
        etNumQ.setText(String.valueOf(settings.numberOfQuestions()));
        etTotalTimer.setText(String.valueOf(settings.totalTestTimerMillis() / 1000));

        btnSave.setOnClickListener(v -> {
            settings.setMusicOn(swMusic.isChecked());
            settings.setSfxOn(swSfx.isChecked());
            settings.setPerQuestionTimer(swPerQ.isChecked());
            int n = 5;
            try { n = Integer.parseInt(etNumQ.getText().toString()); } catch (Exception ignored) {}
            settings.setNumberOfQuestions(n);
            long totalSec = 0;
            try { totalSec = Long.parseLong(etTotalTimer.getText().toString()); } catch (Exception ignored) {}
            settings.setTotalTestTimerMillis(totalSec * 1000);
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
