package com.example.quizapp.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {
    private static final String PREFS = "quiz_prefs";
    private SharedPreferences prefs;

    public SettingsManager(Context ctx) {
        prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public boolean isMusicOn() { return prefs.getBoolean("music_on", false); }
    public void setMusicOn(boolean v) { prefs.edit().putBoolean("music_on", v).apply(); }

    public boolean isSfxOn() { return prefs.getBoolean("sfx_on", false); }
    public void setSfxOn(boolean v) { prefs.edit().putBoolean("sfx_on", v).apply(); }

    public boolean isPerQuestionTimer() { return prefs.getBoolean("per_question_timer", false); }
    public void setPerQuestionTimer(boolean v) { prefs.edit().putBoolean("per_question_timer", v).apply(); }

    public int numberOfQuestions() { return prefs.getInt("num_questions", 5); }
    public void setNumberOfQuestions(int n) { prefs.edit().putInt("num_questions", n).apply(); }

    public long totalTestTimerMillis() { return prefs.getLong("total_timer_ms", 0L); } // 0 = off
    public void setTotalTestTimerMillis(long ms) { prefs.edit().putLong("total_timer_ms", ms).apply(); }

    public int selectedAvatarResId() { return prefs.getInt("avatar_res", -1); }
    public void setSelectedAvatarResId(int res) { prefs.edit().putInt("avatar_res", res).apply(); }

    // SIMPLE local auth for demo (not secure)
    public void saveUser(String username, String password) {
        prefs.edit().putString("user_name", username).putString("user_pass", password).apply();
    }
    public boolean checkUser(String username, String password) {
        String u = prefs.getString("user_name", null);
        String p = prefs.getString("user_pass", null);
        if (u == null) return false;
        return u.equals(username) && p.equals(password);
    }
}
