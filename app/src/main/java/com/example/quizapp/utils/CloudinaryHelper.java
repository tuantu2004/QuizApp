package com.example.quizapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.UploadCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryHelper {

    private static final String TAG = "CloudinaryHelper";

    // Khởi tạo Cloudinary SDK
    public static void init(Context context, String cloudName, String apiKey, String apiSecret) {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        MediaManager.init(context, config);
        Log.d(TAG, "Cloudinary initialized");
    }

    /**
     * Upload drawable resource an toàn
     */
    public static void uploadDrawable(Context context, int drawableResId, UploadCallback callback) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableResId);
        if (bitmap == null) {
            Log.e(TAG, "Failed to decode drawable: " + drawableResId);
            if (callback != null) callback.onError(null, null);
            return;
        }

        // Tạo file cache duy nhất cho mỗi upload để tránh ghi đè
        String fileName = "avatar_upload_" + System.currentTimeMillis() + ".png";
        File file = new File(context.getCacheDir(), fileName);

        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
        } catch (IOException e) {
            Log.e(TAG, "Failed to save drawable to file", e);
            if (callback != null) callback.onError(null, null);
            return;
        }

        // Upload bằng Uri từ file
        Uri fileUri = Uri.fromFile(file);
        MediaManager.get().upload(fileUri)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        if (callback != null) callback.onStart(requestId);
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        if (callback != null) callback.onProgress(requestId, bytes, totalBytes);
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        if (callback != null) callback.onSuccess(requestId, resultData);
                        // Xóa file tạm sau khi upload xong
                        if (file.exists()) file.delete();
                    }

                    @Override
                    public void onError(String requestId, com.cloudinary.android.callback.ErrorInfo error) {
                        if (callback != null) callback.onError(requestId, error);
                        if (file.exists()) file.delete();
                    }

                    @Override
                    public void onReschedule(String requestId, com.cloudinary.android.callback.ErrorInfo error) {
                        if (callback != null) callback.onReschedule(requestId, error);
                    }
                })
                .dispatch();
    }

    /**
     * Upload từ Uri khác
     */
    public static void uploadUri(Uri uri, UploadCallback callback) {
        if (uri == null) {
            Log.e(TAG, "Uri is null, cannot upload");
            if (callback != null) callback.onError(null, null);
            return;
        }

        MediaManager.get().upload(uri)
                .callback(callback)
                .dispatch();
    }
}
