package vn.edu.usth.smartwaro.auth.utils;

import android.util.Patterns;

public class ValidationUtils {
    public static boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isValidUsername(String username) {
        return username != null && username.length() >= 3;
    }

    public static String getEmailError(String email) {
        if (email == null || email.isEmpty()) {
            return "Email is required";
        }
        if (!isValidEmail(email)) {
            return "Invalid email format";
        }
        return null;
    }

    public static String getPasswordError(String password) {
        if (password == null || password.isEmpty()) {
            return "Password is required";
        }
        if (!isValidPassword(password)) {
            return "Password must be at least 6 characters";
        }
        return null;
    }

    public static String getUsernameError(String username) {
        if (username == null || username.isEmpty()) {
            return "Username is required";
        }
        if (!isValidUsername(username)) {
            return "Username must be at least 3 characters";
        }
        return null;
    }
}
