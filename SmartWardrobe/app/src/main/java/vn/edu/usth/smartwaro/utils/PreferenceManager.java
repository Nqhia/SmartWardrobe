package vn.edu.usth.smartwaro.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.util.Map;


public class PreferenceManager {
    private final SharedPreferences sharedPreferences;
    private static final String TAG = "PreferenceManager";

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
    }

    public void putBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void putString(String key, String value) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            boolean success = editor.commit(); // Sử dụng commit() thay vì apply() để đảm bảo lưu ngay lập tức
            Log.d(TAG, "putString - Key: " + key + ", Value: " + value + ", Success: " + success);

            // Verify immediately
            String savedValue = sharedPreferences.getString(key, null);
            Log.d(TAG, "Verification - Key: " + key + ", Saved Value: " + savedValue);
        } catch (Exception e) {
            Log.e(TAG, "Error in putString - Key: " + key, e);
        }
    }

    public String getString(String key) {
        try {
            String value = sharedPreferences.getString(key, null);
            Log.d(TAG, "getString - Key: " + key + ", Value: " + value);
            return value;
        } catch (Exception e) {
            Log.e(TAG, "Error in getString - Key: " + key, e);
            return null;
        }
    }

    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void putFriendRequestStatus(String friendId, String status) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("friend_request_status_" + friendId, status);
            editor.apply();
        } catch (Exception e) {
            Log.e(TAG, "Error saving friend request status", e);
        }
    }

    public String getFriendRequestStatus(String friendId) {
        return sharedPreferences.getString("friend_request_status_" + friendId, Constants.STATUS_PENDING);
    }

    public void putIsFriend(String friendId, boolean isFriend) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_friend_" + friendId, isFriend);
        editor.apply();
    }

    public boolean isFriend(String friendId) {
        return sharedPreferences.getBoolean("is_friend_" + friendId, false);
    }
}
