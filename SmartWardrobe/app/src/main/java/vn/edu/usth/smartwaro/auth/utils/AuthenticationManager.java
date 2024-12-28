package vn.edu.usth.smartwaro.auth.utils;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import vn.edu.usth.smartwaro.network.FirebaseService;

public class AuthenticationManager {
    private static AuthenticationManager instance;
    private final FirebaseAuth auth;

    private AuthenticationManager() {
        this.auth = FirebaseService.getInstance().getAuth();
    }

    public static synchronized AuthenticationManager getInstance() {
        if (instance == null) {
            instance = new AuthenticationManager();
        }
        return instance;
    }

    public Task<AuthResult> signIn(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> signUp(String email, String password) {
        return auth.createUserWithEmailAndPassword(email, password);
    }

    public Task<Void> resetPassword(String email) {
        return auth.sendPasswordResetEmail(email);
    }

    public void signOut() {
        auth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }
}
