package vn.edu.usth.smartwaro.network;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import vn.edu.usth.smartwaro.utils.FileUtils;

public class FlaskNetwork {
    private static final String TAG = "FlaskNetwork";
    public static final String BASE_URL = "http://192.168.91.85:5001";
    private final OkHttpClient client;

    public FlaskNetwork() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public interface OnBackgroundRemovalListener {
        void onProcessing();
        void onSuccess(File processedImage);
        void onError(String message);
    }

    public interface OnImagesLoadedListener {
        void onSuccess(String[] imageUrls);
        void onError(String message);
    }

    private String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in");
        }
        return currentUser.getUid();
    }

    public void removeBackground(Context context, Uri imageUri, OnBackgroundRemovalListener listener) {
        new Thread(() -> {
            try {
                String userId = getCurrentUserId();
                File imageFile = FileUtils.getFileFromUri(context, imageUri);
                if (imageFile == null) {
                    listener.onError("Failed to read image file");
                    return;
                }

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image", imageFile.getName(),
                                RequestBody.create(MediaType.parse("image/*"), imageFile))
                        .addFormDataPart("user_id", userId)
                        .build();
                Request request = new Request.Builder()
                        .url(BASE_URL + "/remove-background")
                        .post(requestBody)
                        .build();

                listener.onProcessing();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body() != null ? response.body().string() : "No error details";
                        Log.e(TAG, "Server error: " + response.code() + ", Body: " + errorBody);
                        listener.onError("Server error: " + response.code() + "\n" + errorBody);
                        return;
                    }

                    ResponseBody responseBody = response.body();
                    if (responseBody == null) {
                        listener.onError("Empty response from server");
                            return;
                    }

                    File outputFile = new File(context.getCacheDir(), "processed_" + System.currentTimeMillis() + ".png");
                    try (BufferedSink sink = Okio.buffer(Okio.sink(outputFile))) {
                        sink.writeAll(responseBody.source());
                    }
                    listener.onSuccess(outputFile);
                }
            } catch (IllegalStateException e) {
                Log.e(TAG, "Authentication error", e);
                listener.onError("Please log in to continue");
            } catch (IOException e) {
                Log.e(TAG, "Network error", e);
                listener.onError("Network error: " + e.getMessage());
            }
        }).start();
    }

    public void getUserImages(Context context, OnImagesLoadedListener listener) {
        new Thread(() -> {
            try {
                String userId = getCurrentUserId();
                HttpUrl url = HttpUrl.parse(BASE_URL + "/get-user-images")
                        .newBuilder()
                        .addQueryParameter("user_id", userId)
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body() != null ? response.body().string() : "No error details";
                        Log.e(TAG, "Server error: " + response.code() + ", Body: " + errorBody);
                        listener.onError("Server error: " + response.code() + "\n" + errorBody);
                        return;
                    }

                    ResponseBody responseBody = response.body();
                    if (responseBody == null) {
                        listener.onError("Empty response from server");
                        return;
                    }

                    String jsonResponse = responseBody.string();
                    Log.d(TAG, "Server response: " + jsonResponse);
                    String[] imageUrls = parseImageUrlsFromResponse(jsonResponse);
                    listener.onSuccess(imageUrls);
                }
            } catch (IllegalStateException e) {
                Log.e(TAG, "Authentication error", e);
                listener.onError("Please log in to continue");
            } catch (IOException e) {
                Log.e(TAG, "Network error", e);
                listener.onError("Network error: " + e.getMessage());
            }
        }).start();
    }

    private String[] parseImageUrlsFromResponse(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            String[] urls = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                String url = jsonArray.getString(i);
                urls[i] = url.startsWith("/") ? url : "/" + url;
            }
            return urls;
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON response", e);
            return new String[]{};
        }
    }
}