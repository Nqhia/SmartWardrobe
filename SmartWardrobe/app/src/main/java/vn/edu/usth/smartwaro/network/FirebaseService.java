package vn.edu.usth.smartwaro.network;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseService {
    private static FirebaseService instance;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseDatabase database;
    private final FirebaseStorage storage;
    private final DatabaseReference databaseReference;
    private final StorageReference storageReference;

    public static final String USERS_PATH = "users";
    public static final String DEVICES_PATH = "devices";
    public static final String NOTIFICATIONS_PATH = "notifications";
    public static final String USER_SETTINGS_PATH = "user_settings";

    private FirebaseService() {
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        databaseReference = database.getReference();
        storageReference = storage.getReference();
    }

    public static synchronized FirebaseService getInstance() {
        if (instance == null) {
            instance = new FirebaseService();
        }
        return instance;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public StorageReference getStorageReference() {
        return storageReference;
    }

    public DatabaseReference getDatabaseReference(String path) {
        return database.getReference(path);
    }

    public StorageReference getStorageReference(String path) {
        return storage.getReference(path);
    }

    public boolean isUserLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public String getCurrentUserId() {
        return firebaseAuth.getCurrentUser() != null ? firebaseAuth.getCurrentUser().getUid() : null;
    }

    public void signOut() {
        firebaseAuth.signOut();
    }

    public static void clearInstance() {
        instance = null;
    }

    public DatabaseReference getDatabase(String path) {
        if (path == null || path.trim().isEmpty()) {
            return databaseReference;
        }
        return database.getReference(path);
    }
    public FirebaseAuth getAuth() {
        return firebaseAuth;
    }

}
