package com.example.quizapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.R;
import com.example.quizapp.data.SettingsManager;

public class LoginActivity extends AppCompatActivity {
    EditText etUser, etPass;
    Button btnLogin, btnRegister;
    SettingsManager settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        settings = new SettingsManager(this);

        etUser = findViewById(R.id.etUsername);
        etPass = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String u = etUser.getText().toString().trim();
            String p = etPass.getText().toString();
            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Nhập username & password", Toast.LENGTH_SHORT).show();
                return;
            }
            settings.saveUser(u, p);
            Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
        });

        btnLogin.setOnClickListener(v -> {
            String u = etUser.getText().toString().trim();
            String p = etPass.getText().toString();
            if (settings.checkUser(u, p)) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Sai thông tin đăng nhập", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
