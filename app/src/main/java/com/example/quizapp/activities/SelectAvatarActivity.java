package com.example.quizapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;import android.widget.GridView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quizapp.R;

public class SelectAvatarActivity extends AppCompatActivity {

    private GridView gvAvatars;
    private final int[] avatars = {
            R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3,
            R.drawable.avatar4, R.drawable.avatar5, R.drawable.avatar6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_avatar);

        gvAvatars = findViewById(R.id.gvAvatars);

        // Lấy avatar hiện tại được gửi từ AvatarActivity
        int currentAvatarIndex = getIntent().getIntExtra("CURRENT_AVATAR_INDEX", 0);

        AvatarAdapter adapter = new AvatarAdapter(this, avatars, currentAvatarIndex);
        gvAvatars.setAdapter(adapter);

        gvAvatars.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Tạo một Intent để chứa dữ liệu trả về
                Intent resultIntent = new Intent();
                // Đặt index của avatar đã chọn vào intent
                resultIntent.putExtra("SELECTED_AVATAR_INDEX", position);
                // Đặt kết quả là OK và gửi intent trở lại
                setResult(RESULT_OK, resultIntent);
                // Đóng Activity này
                finish();
            }
        });
    }
}
    