package vn.edu.usth.smartwaro.network;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

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
import vn.edu.usth.smartwaro.mycloset.FavoriteSet;
import vn.edu.usth.smartwaro.utils.FileUtils;

public class FlaskNetwork {
    private static final String TAG = "FlaskNetwork";
    public static final String BASE_URL = "http://192.168.1.11:5000";
    private final OkHttpClient client;
    private final Handler mainHandler;
    public static final String CATEGORY_UNCATEGORIZED = "uncategorized";
    public static final String CATEGORY_LONG_SLEEVES = "long sleeves";
    public static final String CATEGORY_SHORT_SLEEVES = "short sleeves";
    public static final String CATEGORY_LONG_LEGGINGS = "long leggings";
    public static final String CATEGORY_SHORT_LEGGINGS = "short leggings";

    public static final List<String> DEFAULT_CATEGORIES = Arrays.asList(
            "long sleeves",
            "short sleeves",
            "long leggings",
            "short leggings"
    );

    public interface OnCategoryOperationListener {
        void onSuccess(String message);
        void onError(String message);
    }

    public interface OnMoveImageListener {
        void onSuccess(String newCategory);
        void onError(String message);
    }

    public interface OnCategoriesLoadedListener {
        void onSuccess(List<String> categories);
        void onError(String message);
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

    public interface OnImageSaveListener {
        void onProcessing();
        void onSuccess(String message);
        void onError(String message);
    }

    public interface OnFavoriteSetsLoadedListener {
        void onSuccess(List<FavoriteSet> favoriteSets);
        void onError(String message);
    }

    public interface OnDeleteImagesListener {
        void onSuccess(String[] deletedFiles, String[] failedFiles);
        void onError(String message);
    }

    public interface OnRecommendationReceivedListener {
        void onSuccess(String topWear, String bottomWear);
        void onError(String message);
    }

    public interface OnRandomOutfitReceivedListener {
        void onSuccess(RandomOutfit outfit);
        void onError(String message);
    }

    // Thêm interface OnAllClothesLoadedListener
    public interface OnAllClothesLoadedListener {
        void onSuccess(List<Category> categories);
        void onError(String message);
    }

    // Định nghĩa class Category
    public static class Category {
        public String name;
        public String[] images;

        public Category(String name, String[] images) {
            this.name = name;
            this.images = images;
        }
    }

    public FlaskNetwork() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        mainHandler = new Handler(Looper.getMainLooper());
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

    public void saveImageToServer(Context context, Uri imageUri, String category, OnImageSaveListener listener) {
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
                        .addFormDataPart("category", category)
                        .build();

                Request request = new Request.Builder()
                        .url(BASE_URL + "/save-image")
                        .post(requestBody)
                        .build();

                listener.onProcessing();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body() != null ? response.body().string() : "No error details";
                        listener.onError("Server error: " + response.code() + "\n" + errorBody);
                        return;
                    }

                    JSONObject jsonResponse = new JSONObject(response.body().string());
                    listener.onSuccess(jsonResponse.getString("message"));
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to save image", e);
                listener.onError("Failed to save image: " + e.getMessage());
            }
        }).start();
    }

    public void getUserImages(String category, OnImagesLoadedListener listener) {
        new Thread(() -> {
            try {
                String userId = getCurrentUserId();

                HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + "/get-user-images")
                        .newBuilder()
                        .addQueryParameter("user_id", userId);

                if (category != null) {
                    urlBuilder.addQueryParameter("category", category);
                }

                Request request = new Request.Builder()
                        .url(urlBuilder.build())
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body() != null ? response.body().string() : "No error details";
                        listener.onError("Server error: " + response.code() + "\n" + errorBody);
                        return;
                    }

                    String jsonResponse = response.body().string();
                    JSONArray jsonArray = new JSONArray(jsonResponse);
                    String[] urls = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject imageObject = jsonArray.getJSONObject(i);
                        urls[i] = imageObject.getString("url");
                    }
                    listener.onSuccess(urls);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to get images", e);
                listener.onError("Failed to get images: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Lấy đề xuất quần áo dựa trên nhiệt độ và dữ liệu người dùng
     * @param temperature Nhiệt độ hiện tại
     * @param listener Listener để nhận kết quả đề xuất
     */
    public void getRecommendedClothing(double temperature, int humidity, double windSpeed, OnRecommendationReceivedListener listener) {
        new Thread(() -> {
            try {
                String userId = getCurrentUserId();

                HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + "/recommend-clothing")
                        .newBuilder()
                        .addQueryParameter("temperature", String.valueOf(temperature))
                        .addQueryParameter("humidity", String.valueOf(humidity))
                        .addQueryParameter("wind_speed", String.valueOf(windSpeed))
                        .addQueryParameter("user_id", userId);

                Request request = new Request.Builder()
                        .url(urlBuilder.build())
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body() != null ? response.body().string() : "No error details";
                        mainHandler.post(() -> listener.onError("Server error: " + response.code() + "\n" + errorBody));
                        return;
                    }

                    String jsonResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonResponse);

                    String topWear = jsonObject.optString("topWear", "long sleeves");
                    String bottomWear = jsonObject.optString("bottomWear", "long leggings");

                    // Kiểm tra danh sách hình ảnh
                    JSONArray topImagesArray = jsonObject.optJSONArray("topImages");
                    JSONArray bottomImagesArray = jsonObject.optJSONArray("bottomImages");

                    List<String> topImages = new ArrayList<>();
                    List<String> bottomImages = new ArrayList<>();

                    if (topImagesArray != null) {
                        for (int i = 0; i < topImagesArray.length(); i++) {
                            topImages.add(topImagesArray.getString(i));
                        }
                    }

                    if (bottomImagesArray != null) {
                        for (int i = 0; i < bottomImagesArray.length(); i++) {
                            bottomImages.add(bottomImagesArray.getString(i));
                        }
                    }

                    // Gửi dữ liệu về UI
                    mainHandler.post(() -> listener.onSuccess(topWear, bottomWear));
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to get clothing recommendations", e);
                mainHandler.post(() -> listener.onError("Failed to get recommendations: " + e.getMessage()));
            }
        }).start();
    }



    public void deleteImages(String[] filenames, OnDeleteImagesListener listener) {
        new Thread(() -> {
            try {
                String userId = getCurrentUserId();

                // Create JSON request body
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("user_id", userId);
                jsonBody.put("filenames", new JSONArray(Arrays.asList(filenames)));

                RequestBody requestBody = RequestBody.create(
                        MediaType.parse("application/json"),
                        jsonBody.toString()
                );

                Request request = new Request.Builder()
                        .url(BASE_URL + "/delete-images")
                        .post(requestBody)
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
                    JSONObject responseJson = new JSONObject(jsonResponse);

                    JSONArray deletedFilesArray = responseJson.getJSONArray("deleted_files");
                    JSONArray failedFilesArray = responseJson.getJSONArray("failed_files");

                    String[] deletedFiles = new String[deletedFilesArray.length()];
                    String[] failedFiles = new String[failedFilesArray.length()];

                    for (int i = 0; i < deletedFilesArray.length(); i++) {
                        deletedFiles[i] = deletedFilesArray.getString(i);
                    }

                    for (int i = 0; i < failedFilesArray.length(); i++) {
                        failedFiles[i] = failedFilesArray.getString(i);
                    }

                    listener.onSuccess(deletedFiles, failedFiles);
                }
            } catch (IllegalStateException e) {
                Log.e(TAG, "Authentication error", e);
                listener.onError("Please log in to continue");
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Network or parsing error", e);
                listener.onError("Error: " + e.getMessage());
            }
        }).start();
    }

    public void getCategories(OnCategoriesLoadedListener listener) {
        new Thread(() -> {
            try {
                String userId = getCurrentUserId();
                HttpUrl url = HttpUrl.parse(BASE_URL + "/get-categories")
                        .newBuilder()
                        .addQueryParameter("user_id", userId)
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body() != null ? response.body().string() : "No error details";
                        listener.onError("Server error: " + response.code() + "\n" + errorBody);
                        return;
                    }

                    JSONObject jsonResponse = new JSONObject(response.body().string());
                    JSONArray categoriesArray = jsonResponse.getJSONArray("categories");
                    List<String> categories = new ArrayList<>();
                    for (int i = 0; i < categoriesArray.length(); i++) {
                        categories.add(categoriesArray.getString(i));
                    }
                    listener.onSuccess(categories);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to get categories", e);
                listener.onError("Failed to get categories: " + e.getMessage());
            }
        }).start();
    }

    public void addCategory(String categoryName, OnCategoryOperationListener listener) {
        new Thread(() -> {
            try {
                String userId = getCurrentUserId();
                JSONObject jsonBody = new JSONObject()
                        .put("user_id", userId)
                        .put("category_name", categoryName);

                RequestBody requestBody = RequestBody.create(
                        MediaType.parse("application/json"),
                        jsonBody.toString()
                );

                Request request = new Request.Builder()
                        .url(BASE_URL + "/add-category")
                        .post(requestBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body() != null ? response.body().string() : "No error details";
                        listener.onError("Server error: " + response.code() + "\n" + errorBody);
                        return;
                    }

                    JSONObject jsonResponse = new JSONObject(response.body().string());
                    listener.onSuccess(jsonResponse.getString("message"));
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to add category", e);
                listener.onError("Failed to add category: " + e.getMessage());
            }
        }).start();
    }

    public void removeCategory(String categoryName, OnCategoryOperationListener listener) {
        new Thread(() -> {
            try {
                String userId = getCurrentUserId();
                JSONObject jsonBody = new JSONObject()
                        .put("user_id", userId)
                        .put("category_name", categoryName);

                RequestBody requestBody = RequestBody.create(
                        MediaType.parse("application/json"),
                        jsonBody.toString()
                );

                Request request = new Request.Builder()
                        .url(BASE_URL + "/remove-categories")
                        .delete(requestBody)  // Using DELETE method as specified in server
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body() != null ? response.body().string() : "No error details";
                        listener.onError("Server error: " + response.code() + "\n" + errorBody);
                        return;
                    }

                    JSONObject jsonResponse = new JSONObject(response.body().string());
                    if (jsonResponse.has("message")) {
                        listener.onSuccess(jsonResponse.getString("message"));
                    } else if (jsonResponse.has("error")) {
                        listener.onError(jsonResponse.getString("error"));
                    }
                }
            } catch (IllegalStateException e) {
                Log.e(TAG, "Authentication error", e);
                listener.onError("Please log in to continue");
            } catch (Exception e) {
                Log.e(TAG, "Failed to remove category", e);
                listener.onError("Failed to remove category: " + e.getMessage());
            }
        }).start();
    }

    public void moveImage(String filename, String newCategory, OnMoveImageListener listener) {
        new Thread(() -> {
            try {
                String userId = getCurrentUserId();
                JSONObject jsonBody = new JSONObject()
                        .put("user_id", userId)
                        .put("filename", filename)
                        .put("new_category", newCategory);

                RequestBody requestBody = RequestBody.create(
                        MediaType.parse("application/json"),
                        jsonBody.toString()
                );

                Request request = new Request.Builder()
                        .url(BASE_URL + "/move-image")
                        .post(requestBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body() != null ? response.body().string() : "No error details";
                        listener.onError("Server error: " + response.code() + "\n" + errorBody);
                        return;
                    }

                    JSONObject jsonResponse = new JSONObject(response.body().string());
                    listener.onSuccess(jsonResponse.getString("new_category"));
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to move image", e);
                listener.onError("Failed to move image: " + e.getMessage());
            }
        }).start();
    }

    public void getRandomOutfit(OnRandomOutfitReceivedListener listener) {
        new Thread(() -> {
            try {
                String userId = getCurrentUserId();

                HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + "/random-outfit")
                        .newBuilder()
                        .addQueryParameter("user_id", userId);

                Request request = new Request.Builder()
                        .url(urlBuilder.build())
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body() != null ? response.body().string() : "No error details";
                        listener.onError("Server error: " + response.code() + "\n" + errorBody);
                        return;
                    }

                    String jsonResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonResponse);

                    // Parse top item
                    JSONObject topJson = jsonObject.getJSONObject("top");
                    RandomOutfit.OutfitItem top = new RandomOutfit.OutfitItem(
                            topJson.getString("category"),
                            topJson.getString("filename"),
                            topJson.getString("url")
                    );

                    // Parse bottom item
                    JSONObject bottomJson = jsonObject.getJSONObject("bottom");
                    RandomOutfit.OutfitItem bottom = new RandomOutfit.OutfitItem(
                            bottomJson.getString("category"),
                            bottomJson.getString("filename"),
                            bottomJson.getString("url")
                    );

                    // Create and return RandomOutfit
                    RandomOutfit outfit = new RandomOutfit(top, bottom);
                    listener.onSuccess(outfit);

                } catch (JSONException e) {
                    Log.e(TAG, "JSON parsing error", e);
                    listener.onError("Failed to parse server response: " + e.getMessage());
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

    // Thêm phương thức getAllUserClothes
    public void getAllUserClothes(OnAllClothesLoadedListener listener) {
        new Thread(() -> {
            try {
                String userId = getCurrentUserId();

                HttpUrl url = HttpUrl.parse(BASE_URL + "/getAllUserClothes")
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

                    String jsonResponse = response.body().string();
                    JSONArray jsonArray = new JSONArray(jsonResponse);
                    List<Category> categories = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject categoryObject = jsonArray.getJSONObject(i);
                        String categoryName = categoryObject.getString("category");
                        JSONArray imagesArray = categoryObject.getJSONArray("images");
                        String[] imageUrls = new String[imagesArray.length()];
                        for (int j = 0; j < imagesArray.length(); j++) {
                            imageUrls[j] = imagesArray.getString(j);
                        }
                        categories.add(new Category(categoryName, imageUrls));
                    }
                    listener.onSuccess(categories);
                }
            } catch (IllegalStateException e) {
                Log.e(TAG, "Authentication error", e);
                listener.onError("Please log in to continue");
            } catch (Exception e) {
                Log.e(TAG, "Failed to get all user clothes", e);
                listener.onError("Failed to get all user clothes: " + e.getMessage());
            }
        }).start();
    }

    public static class RandomOutfit {
        public final OutfitItem top;
        public final OutfitItem bottom;

        public RandomOutfit(OutfitItem top, OutfitItem bottom) {
            this.top = top;
            this.bottom = bottom;
        }

        public static class OutfitItem {
            public final String category;
            public final String filename;
            public final String url;

            public OutfitItem(String category, String filename, String url) {
                this.category = category;
                this.filename = filename;
                this.url = url;
            }
        }
    }

    public interface OnFavoriteOperationListener {
        void onSuccess(String message);
        void onError(String message);
    }

    public void addImageToFavoriteSet(String filename, OnFavoriteOperationListener listener) {
        new Thread(() -> {
            try {
                String userId = getCurrentUserId();
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("user_id", userId);
                jsonBody.put("filename", filename);
                // Nếu cần, bạn có thể gửi thêm thông tin set (ở đây mặc định là favourite)
                jsonBody.put("set", "favourite");

                RequestBody requestBody = RequestBody.create(
                        MediaType.parse("application/json"),
                        jsonBody.toString()
                );

                Request request = new Request.Builder()
                        .url(BASE_URL + "/add-to-favorite")
                        .post(requestBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body() != null ? response.body().string() : "No error details";
                        listener.onError("Server error: " + response.code() + "\n" + errorBody);
                        return;
                    }
                    JSONObject jsonResponse = new JSONObject(response.body().string());
                    listener.onSuccess(jsonResponse.getString("message"));
                }
            } catch (Exception e) {
                listener.onError("Failed to add image to favorite set: " + e.getMessage());
            }
        }).start();
    }

    public interface OnFavoriteSetSaveListener {
        void onSuccess(String message);
        void onError(String message);
    }

    public void createFavoriteSet(String setName, List<String> shirtFilenames, List<String> pantFilenames, OnFavoriteSetSaveListener listener) {
        new Thread(() -> {
            try {
                String userId = getCurrentUserId();
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("user_id", userId);
                jsonBody.put("set_name", setName);
                // Server ở đây mong đợi key "shirt_images" và "pant_images"
                JSONArray shirtArray = new JSONArray(shirtFilenames);
                JSONArray pantArray = new JSONArray(pantFilenames);
                jsonBody.put("shirt_images", shirtArray);
                jsonBody.put("pant_images", pantArray);

                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody.toString());
                Request request = new Request.Builder()
                        .url(BASE_URL + "/create-favorite-set")
                        .post(requestBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body() != null ? response.body().string() : "No error details";
                        listener.onError("Server error: " + response.code() + "\n" + errorBody);
                        return;
                    }
                    JSONObject jsonResponse = new JSONObject(response.body().string());
                    listener.onSuccess(jsonResponse.getString("message"));
                }
            } catch (Exception e) {
                listener.onError("Failed to create favorite set: " + e.getMessage());
            }
        }).start();
    }

    public void editFavoriteSet(String setId, String setName, List<String> shirtFilenames, List<String> pantFilenames, OnFavoriteSetSaveListener listener) {
        new Thread(() -> {
            try {
                String userId = getCurrentUserId();
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("user_id", userId);
                jsonBody.put("set_id", setId); // truyền set_id
                jsonBody.put("set_name", setName);
                JSONArray shirtArray = new JSONArray(shirtFilenames);
                JSONArray pantArray = new JSONArray(pantFilenames);
                jsonBody.put("shirt_images", shirtArray);
                jsonBody.put("pant_images", pantArray);

                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody.toString());
                Request request = new Request.Builder()
                        .url(BASE_URL + "/edit-favorite-set")
                        .post(requestBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body() != null ? response.body().string() : "No error details";
                        listener.onError("Server error: " + response.code() + "\n" + errorBody);
                        return;
                    }
                    JSONObject jsonResponse = new JSONObject(response.body().string());
                    listener.onSuccess(jsonResponse.getString("message"));
                }
            } catch (Exception e) {
                listener.onError("Error: " + e.getMessage());
            }
        }).start();
    }



    public void getFavoriteSets(OnFavoriteSetsLoadedListener listener) {
        new Thread(() -> {
            try {
                String userId = getCurrentUserId();
                HttpUrl url = HttpUrl.parse(BASE_URL + "/get-favorite-sets")
                        .newBuilder()
                        .addQueryParameter("user_id", userId)
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body() != null ? response.body().string() : "No error details";
                        listener.onError("Server error: " + response.code() + "\n" + errorBody);
                        return;
                    }
                    String responseStr = response.body().string();
                    // Sử dụng Gson để parse phản hồi (mảng JSON) thành List<FavoriteSet>
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<FavoriteSet>>(){}.getType();
                    List<FavoriteSet> sets = gson.fromJson(responseStr, listType);
                    if (sets == null) {
                        sets = new ArrayList<>();
                    }
                    listener.onSuccess(sets);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to get favorite sets", e);
                listener.onError("Failed to get favorite sets: " + e.getMessage());
            }
        }).start();
    }
}