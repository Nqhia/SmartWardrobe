package vn.edu.usth.smartwaro.auth.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import vn.edu.usth.smartwaro.utils.Constants;
import vn.edu.usth.smartwaro.auth.AuthCallback;
import vn.edu.usth.smartwaro.auth.model.UserModel;

public class AuthRepository {
    private final FirebaseFirestore firestore;
    private final FirebaseAuth firebaseAuth;

    public AuthRepository() {
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void signIn(String email, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // After successful auth, get user data from Firestore
                    getUserData(Objects.requireNonNull(authResult.getUser()).getUid(), callback);

                    // Update last login time
                    updateLastLogin(authResult.getUser().getUid());
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void register(String username, String email, String password, AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        // Create user model
                        UserModel newUser = new UserModel();
                        newUser.setId(firebaseUser.getUid());
                        newUser.setName(username);
                        newUser.setEmail(email);
                        newUser.setCreatedAt(new Timestamp(new Date()));
                        newUser.setLastLogin(new Timestamp(new Date()));
                        newUser.setDefaultImage(true);

                        // Save to Firestore
                        firestore.collection(Constants.KEY_COLLECTION_USERS)
                                .document(firebaseUser.getUid())
                                .set(newUser)
                                .addOnSuccessListener(aVoid -> callback.onSuccess(newUser))
                                .addOnFailureListener(e -> {
                                    // Delete auth user if Firestore save fails
                                    firebaseUser.delete();
                                    callback.onError("Failed to create user profile: " + e.getMessage());
                                });
                    }
                })
                .addOnFailureListener(e -> callback.onError("Registration failed: " + e.getMessage()));
    }

    private void getUserData(String userId, AuthCallback callback) {
        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        UserModel user = document.toObject(UserModel.class);
                        callback.onSuccess(user);
                    } else {
                        callback.onError("User data not found");
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    private void updateLastLogin(String userId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("lastLogin", new Timestamp(new Date()));

        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .document(userId)
                .update(updates);
    }

    public void logout() {
        firebaseAuth.signOut();
    }

    public boolean isUserLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public String getCurrentUserId() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public void getCurrentUser(AuthCallback callback) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            getUserData(user.getUid(), callback);
        } else {
            callback.onError("No user logged in");
        }
    }
}
