package com.example.quizapp.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {

    private static final String PREFS_NAME = "quiz_settings";
    private SharedPreferences prefs;
    private final String userId;

    public SettingsManager(Context context, String userId) {
        this.userId = userId != null ? userId : "guest";
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Nếu lần đầu, set default
        if (!prefs.contains(key("init"))) setDefaultSettings();
    }

    private String key(String key) {
        return userId + "_" + key; // phân biệt theo user
    }

    // ----- Default settings -----
    private void setDefaultSettings() {
        setMusicOn(true);
        setSfxOn(true);
        setPerQuestionTimer(true);
        setNumberOfQuestions(5);
        setPerQuestionMillis(30_000L); // mặc định 30s mỗi câu
        setAvatarIndex(0);
        setAvatarUrl("");
        prefs.edit().putBoolean(key("init"), true).apply();
    }

    // ----- Background music -----
    public boolean isMusicOn() {
        return prefs.getBoolean(key("music"), true);
    }

    public void setMusicOn(boolean value) {
        prefs.edit().putBoolean(key("music"), value).apply();
    }

    // ----- Sound effects -----
    public boolean isSfxOn() {
        return prefs.getBoolean(key("sfx"), true);
    }

    public void setSfxOn(boolean value) {
        prefs.edit().putBoolean(key("sfx"), value).apply();
    }

    // ----- Per question timer -----
    public boolean isPerQuestionTimer() {
        return prefs.getBoolean(key("perQ"), true);
    }

    public void setPerQuestionTimer(boolean value) {
        prefs.edit().putBoolean(key("perQ"), value).apply();
    }

    // ----- Number of questions -----
    public int numberOfQuestions() {
        return prefs.getInt(key("numQ"), 5);
    }

    public void setNumberOfQuestions(int value) {
        prefs.edit().putInt(key("numQ"), value).apply();
    }

    // ----- Total test timer (ms) -----
    public long totalTestTimerMillis() {
        long value = prefs.getLong(key("totalTimer"), 0);
        if (value <= 0) value = numberOfQuestions() * getPerQuestionMillis(); // lấy từ mỗi câu
        return value;
    }

    public void setTotalTestTimerMillis(long value) {
        prefs.edit().putLong(key("totalTimer"), value).apply();
    }

    // ----- Per question timer (ms) -----
    public long getPerQuestionMillis() {
        long value = prefs.getLong(key("perQMs"), 30_000L); // mặc định 30s
        return value;
    }

    public void setPerQuestionMillis(long millis) {
        prefs.edit().putLong(key("perQMs"), millis).apply();
    }

    // ----- Avatar index -----
    public int getAvatarIndex() {
        return prefs.getInt(key("avatar"), 0);
    }

    public void setAvatarIndex(int index) {
        prefs.edit().putInt(key("avatar"), index).apply();
    }

    // ----- Avatar URL -----
    public String getAvatarUrl() {
        return prefs.getString(key("avatarUrl"), "");
    }

    public void setAvatarUrl(String url) {
        prefs.edit().putString(key("avatarUrl"), url).apply();
    }
}
