package com.example.quizapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.cloudinary.android.callback.UploadCallback;
import com.example.quizapp.R;
import com.example.quizapp.data.SettingsManager;
import com.example.quizapp.utils.CloudinaryHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class AvatarActivity extends AppCompatActivity {

    private ImageView btnBack, ivCurrentAvatar;
    private Button btnChangeAvatar, btnSave;
    private TextInputEditText etDisplayName;
    private TextView tvEmail;

    private FirebaseAuth mAuth;
    private SettingsManager settings;
    private int selectedAvatarIndex = 0;
    private String uploadedAvatarUrl = null;
    private boolean isUploading = false;

    private final int[] avatars = {
            R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3,
            R.drawable.avatar4, R.drawable.avatar5, R.drawable.avatar6
    };

    private ActivityResultLauncher<Intent> avatarSelectorLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);

        uploadedAvatarUrl = null; // reset để tránh crash khi mở lại

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user != null ? user.getUid() : "guest";
        settings = new SettingsManager(this, uid);

        // Khởi tạo Cloudinary an toàn
        CloudinaryHelper.init(this,
                getString(R.string.cloud_name),
                getString(R.string.cloud_api_key),
                getString(R.string.cloud_api_secret));

        initViews();
        setupAvatarSelectorLauncher();
        loadUserData();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        ivCurrentAvatar = findViewById(R.id.ivCurrentAvatar);
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
        btnSave = findViewById(R.id.btnSave);
        etDisplayName = findViewById(R.id.etDisplayName);
        tvEmail = findViewById(R.id.tvEmail);
    }

    private void setupAvatarSelectorLauncher() {
        avatarSelectorLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        int index = result.getData().getIntExtra("SELECTED_AVATAR_INDEX", -1);
                        if (index != -1) {
                            selectedAvatarIndex = index;
                            ivCurrentAvatar.setImageResource(avatars[selectedAvatarIndex]);
                            uploadAvatarToCloudinary();
                        }
                    }
                });
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (user.getEmail() != null) tvEmail.setText(user.getEmail());
            etDisplayName.setText(user.getDisplayName() != null ? user.getDisplayName() : "");
        }

        // Load avatar đã lưu trong SettingsManager
        selectedAvatarIndex = settings.getAvatarIndex();
        uploadedAvatarUrl = settings.getAvatarUrl(); // load url cũ
        ivCurrentAvatar.setImageResource(avatars[selectedAvatarIndex]);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnChangeAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(AvatarActivity.this, SelectAvatarActivity.class);
            intent.putExtra("CURRENT_AVATAR_INDEX", selectedAvatarIndex);
            avatarSelectorLauncher.launch(intent);
        });

        btnSave.setOnClickListener(v -> {
            if (isUploading) {
                Toast.makeText(this, "Please wait for avatar upload to finish", Toast.LENGTH_SHORT).show();
                return;
            }
            saveProfile();
        });
    }

    private void uploadAvatarToCloudinary() {
        isUploading = true;
        Toast.makeText(this, "Uploading avatar...", Toast.LENGTH_SHORT).show();

        CloudinaryHelper.uploadDrawable(this, avatars[selectedAvatarIndex], new UploadCallback() {
            @Override
            public void onStart(String requestId) { }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) { }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                uploadedAvatarUrl = resultData.get("secure_url").toString();
                isUploading = false;
                Toast.makeText(AvatarActivity.this, "✅ Upload completed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String requestId, com.cloudinary.android.callback.ErrorInfo error) {
                isUploading = false;
                Toast.makeText(AvatarActivity.this, "❌ Upload failed", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onReschedule(String requestId, com.cloudinary.android.callback.ErrorInfo error) { }
        });
    }

    private void saveProfile() {
        String newName = etDisplayName.getText().toString().trim();
        if (newName.isEmpty() || newName.length() < 2) {
            etDisplayName.setError("Name must be at least 2 characters");
            etDisplayName.requestFocus();
            return;
        }

        btnSave.setEnabled(false);
        btnSave.setText("Saving...");

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        // Update SettingsManager
        settings.setAvatarIndex(selectedAvatarIndex);
        if (uploadedAvatarUrl != null && !uploadedAvatarUrl.isEmpty()) {
            settings.setAvatarUrl(uploadedAvatarUrl);
        }

        // Update FirebaseUser
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName);

        if (uploadedAvatarUrl != null && !uploadedAvatarUrl.isEmpty()) {
            builder.setPhotoUri(Uri.parse(uploadedAvatarUrl));
        }

        user.updateProfile(builder.build()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Update Firestore
                Map<String, Object> data = new HashMap<>();
                data.put("avatarIndex", selectedAvatarIndex);
                if (uploadedAvatarUrl != null && !uploadedAvatarUrl.isEmpty()) {
                    data.put("avatarUrl", uploadedAvatarUrl);
                }

                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(user.getUid())
                        .set(data, SetOptions.merge())
                        .addOnCompleteListener(firestoreTask -> {
                            btnSave.setEnabled(true);
                            btnSave.setText("Save");
                            if (firestoreTask.isSuccessful()) {
                                Toast.makeText(this, "✅ Profile updated!", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                Toast.makeText(this, "❌ Failed to update Firestore", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                btnSave.setEnabled(true);
                btnSave.setText("Save");
                Toast.makeText(this, "❌ Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
