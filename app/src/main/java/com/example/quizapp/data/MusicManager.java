package com.example.quizapp.data;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.quizapp.R;

public class MusicManager {

    private static MediaPlayer appMusic;
    private static MediaPlayer quizMusic;

    public static void startAppMusic(Context context, boolean isMusicOn) {
        if (!isMusicOn) return;

        try {
            if (appMusic == null) {
                appMusic = MediaPlayer.create(context, R.raw.app_music);
                if (appMusic != null) {
                    appMusic.setLooping(true);
                    appMusic.start();
                }
            } else if (!appMusic.isPlaying()) {
                appMusic.start(); // resume nhạc đang pause
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void stopAppMusic() {
        if (appMusic != null) {
            if (appMusic.isPlaying()) appMusic.stop();
            appMusic.release();
            appMusic = null;
        }
    }

    public static void startQuizMusic(Context context, boolean isMusicOn) {
        stopQuizMusic(); // dừng nhạc quiz cũ nếu có
        if (isMusicOn) {
            quizMusic = MediaPlayer.create(context, R.raw.quiz_music); // file quiz_music.mp3 trong res/raw
            quizMusic.setLooping(true);
            quizMusic.start();
        }
    }

    public static void stopQuizMusic() {
        if (quizMusic != null) {
            if (quizMusic.isPlaying()) quizMusic.stop();
            quizMusic.release();
            quizMusic = null;
        }
    }
    public static void resumeAppMusic() {
        if (appMusic != null && !appMusic.isPlaying()) {
            appMusic.start();
        }
    }

    public static void pauseAppMusic() {
        if (appMusic != null && appMusic.isPlaying()) {
            appMusic.pause();
        }
    }

    public static int getAppMusicPosition() {
        return appMusic != null ? appMusic.getCurrentPosition() : 0;
    }

    public static void seekAppMusic(int position) {
        if (appMusic != null) appMusic.seekTo(position);
    }

}
