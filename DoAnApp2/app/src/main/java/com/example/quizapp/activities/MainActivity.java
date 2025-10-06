package com.example.quizapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.R;
import com.example.quizapp.data.SettingsManager;

public class MainActivity extends AppCompatActivity {

    Button btnStartQuiz, btnCreate, btnSettings, btnLogout, btnChangeAvatar;
    ImageView ivAvatar;
    SettingsManager settings;

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
        settings = new SettingsManager(this);

        // Tìm view
        ivAvatar = findViewById(R.id.ivAvatar);
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
        btnStartQuiz = findViewById(R.id.btnStartQuiz);
        btnCreate = findViewById(R.id.btnCreate);
        btnSettings = findViewById(R.id.btnSettings);
        btnLogout = findViewById(R.id.btnLogout);

        // Hiển thị avatar đã chọn
        updateAvatar();

        // Bấm nút đổi avatar
        btnChangeAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AvatarActivity.class);
            startActivity(intent);
        });

        // Các nút khác
        btnStartQuiz.setOnClickListener(v -> startActivity(new Intent(this, QuizActivity.class)));
        btnCreate.setOnClickListener(v -> startActivity(new Intent(this, CreateQuestionActivity.class)));
        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật avatar khi quay lại MainActivity
        updateAvatar();
    }

    private void updateAvatar() {
        int savedIndex = AvatarActivity.getSavedAvatar(this);
        ivAvatar.setImageResource(avatars[savedIndex]);
    }
}
