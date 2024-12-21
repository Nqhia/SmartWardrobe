package vn.edu.usth.smartwaro.auth;

import android.text.TextUtils;
import android.util.Patterns;

public class AuthUtils {
    /**
     * Validates email address format
     * @param email Email address to validate
     * @return true if email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Validates password strength
     * @param password Password to validate
     * @return true if password meets requirements, false otherwise
     */
    public static boolean isValidPassword(String password) {
        return password != null &&
                password.length() >= 6 &&
                containsValidCharacters(password);
    }

    /**
     * Checks if passwords match
     * @param password First password
     * @param confirmPassword Password to confirm
     * @return true if passwords match, false otherwise
     */
    public static boolean passwordsMatch(String password, String confirmPassword) {
        return password != null &&
                confirmPassword != null &&
                password.equals(confirmPassword);
    }

    /**
     * Validates username
     * @param username Username to validate
     * @return true if username is valid, false otherwise
     */
    public static boolean isValidUsername(String username) {
        return !TextUtils.isEmpty(username) &&
                username.length() >= 3 &&
                username.length() <= 20;
    }

    /**
     * Checks if password contains a mix of characters
     * @param password Password to check
     * @return true if password contains valid characters, false otherwise
     */
    private static boolean containsValidCharacters(String password) {
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUppercase = true;
            if (Character.isLowerCase(c)) hasLowercase = true;
            if (Character.isDigit(c)) hasDigit = true;
            if (isSpecialCharacter(c)) hasSpecialChar = true;
        }

        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar;
    }

    /**
     * Checks if a character is a special character
     * @param c Character to check
     * @return true if character is special, false otherwise
     */
    private static boolean isSpecialCharacter(char c) {
        String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        return specialChars.contains(String.valueOf(c));
    }
}