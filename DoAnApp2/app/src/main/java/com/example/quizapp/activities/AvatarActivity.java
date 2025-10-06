package com.example.quizapp.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.R;

public class AvatarActivity extends AppCompatActivity {

    GridView gvAvatars;
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
        setContentView(R.layout.activity_avatar);

        gvAvatars = findViewById(R.id.gvAvatars);
        gvAvatars.setAdapter(new AvatarAdapter());

        gvAvatars.setOnItemClickListener((parent, view, position, id) -> {
            saveAvatar(position);
            Toast.makeText(this, "Avatar đã được chọn!", Toast.LENGTH_SHORT).show();
            finish(); // Quay về MainActivity
        });
    }

    private void saveAvatar(int index) {
        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("avatar_index", index);
        editor.apply();
    }

    public static int getSavedAvatar(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        return prefs.getInt("avatar_index", 0); // mặc định avatar 0
    }

    // Adapter hiển thị avatar
    private class AvatarAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return avatars.length;
        }

        @Override
        public Object getItem(int position) {
            return avatars[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(AvatarActivity.this);
                imageView.setLayoutParams(new GridView.LayoutParams(250, 250));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(avatars[position]);

            // Thêm viền nếu avatar đang được chọn
            int savedIndex = getSavedAvatar(AvatarActivity.this);
            if (position == savedIndex) {
                imageView.setBackgroundResource(R.drawable.avatar_border);
            } else {
                imageView.setBackgroundResource(0);
            }

            return imageView;
        }
    }
}
