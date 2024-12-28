package vn.edu.usth.smartwaro.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.auth.utils.AuthenticationManager;
import vn.edu.usth.smartwaro.network.FirebaseService;
import vn.edu.usth.smartwaro.auth.utils.ValidationUtils;
public class UpdateProfileActivity extends AppCompatActivity {
    private TextInputEditText usernameEditText, emailEditText;
    private CircleImageView avatarImageView;
    private Button saveButton, changeAvatarButton;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private static final int REQUEST_CODE_PICK_IMAGE = 1001;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        // Initialize views
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        avatarImageView = findViewById(R.id.avatarImageView);
        saveButton = findViewById(R.id.saveButton);
        changeAvatarButton = findViewById(R.id.changeAvatarButton);
        progressBar = findViewById(R.id.progressBar);

        // Initialize Firebase services
        firebaseAuth = FirebaseService.getInstance().getFirebaseAuth();
        databaseReference = FirebaseService.getInstance().getDatabaseReference();
        storageReference = FirebaseService.getInstance().getStorageReference();

        // Load user data
        loadUserProfile();

        // Set up listeners
        saveButton.setOnClickListener(v -> updateProfile());
        changeAvatarButton.setOnClickListener(v -> openImagePicker());
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            progressBar.setVisibility(View.VISIBLE);
            String userId = currentUser.getUid();

            databaseReference.child("users").child(userId).get()
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Map<String, Object> userData = (Map<String, Object>) task.getResult().getValue();
                            if (userData != null) {
                                // Set username
                                String username = (String) userData.get("username");
                                usernameEditText.setText(username);

                                // Set email
                                emailEditText.setText(currentUser.getEmail());

                                // Load avatar
                                String avatarUrl = (String) userData.get("avatarUrl");
                                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                    Glide.with(this)
                                            .load(avatarUrl)
                                            .placeholder(R.drawable.placeholder_avatar)
                                            .into(avatarImageView);
                                }
                            }
                        } else {
                            Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateProfile() {
        String newUsername = usernameEditText.getText().toString().trim();

        // Validate username
        if (!ValidationUtils.isValidUsername(newUsername)) {
            Toast.makeText(this, "Invalid username. Must be 3-20 characters.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Prepare update map
            Map<String, Object> updates = new HashMap<>();
            updates.put("username", newUsername);

            // Update username in database
            databaseReference.child("users").child(userId)
                    .updateChildren(updates)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            // Optionally, finish the activity or refresh the view
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {

                Glide.with(this)
                        .load(selectedImageUri)
                        .into(avatarImageView);
                uploadAvatar();
            }
        }
    }

    private void uploadAvatar() {
        if (selectedImageUri == null) return;

        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            StorageReference avatarRef = storageReference.child("avatars/" + userId + ".jpg");

            avatarRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Update avatar URL in database
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("avatarUrl", uri.toString());

                            databaseReference.child("users").child(userId)
                                    .updateChildren(updates)
                                    .addOnCompleteListener(task -> {
                                        progressBar.setVisibility(View.GONE);
                                        if (task.isSuccessful()) {
                                            Toast.makeText(this, "Avatar updated successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(this, "Failed to update avatar", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Avatar upload failed", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}