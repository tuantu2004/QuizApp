package com.example.quizapp.utils;

import android.content.Context;

import com.example.quizapp.data.models.Question;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    private static final String INTERNAL_FILE = "questions.json";

    // Đọc từ assets/questions.json
    public static List<Question> loadFromAssets(Context ctx) {
        try {
            InputStream is = ctx.getAssets().open("questions.json");
            InputStreamReader reader = new InputStreamReader(is);
            Type listType = new TypeToken<ArrayList<Question>>() {}.getType();
            List<Question> list = new Gson().fromJson(reader, listType);
            reader.close();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Đọc từ bộ nhớ trong (user đã thêm)
    public static List<Question> loadFromInternal(Context ctx) {
        try {
            FileInputStream fis = ctx.openFileInput(INTERNAL_FILE);
            InputStreamReader reader = new InputStreamReader(fis);
            Type listType = new TypeToken<ArrayList<Question>>() {}.getType();
            List<Question> list = new Gson().fromJson(reader, listType);
            reader.close();
            return list;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // Lưu danh sách vào bộ nhớ trong
    public static boolean saveToInternal(Context ctx, List<Question> list) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(list);
            FileOutputStream fos = ctx.openFileOutput(INTERNAL_FILE, Context.MODE_PRIVATE);
            fos.write(json.getBytes());
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
