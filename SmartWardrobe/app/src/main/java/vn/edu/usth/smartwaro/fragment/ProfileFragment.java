package vn.edu.usth.smartwaro.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.auth.UpdateProfileActivity;
import vn.edu.usth.smartwaro.network.FirebaseService;

public class ProfileFragment extends Fragment {
    private TextView usernameTextView;
    private TextView emailTextView;
    private CircleImageView avatarImageView;
    private Button editProfileButton;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        usernameTextView = view.findViewById(R.id.usernameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        avatarImageView = view.findViewById(R.id.avatarImageView);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        progressBar = view.findViewById(R.id.progressBar);

        // Initialize Firebase services
        firebaseAuth = FirebaseService.getInstance().getFirebaseAuth();
        databaseReference = FirebaseService.getInstance().getDatabaseReference();

        // Set up edit profile button
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UpdateProfileActivity.class);
            startActivity(intent);
        });

        // Load the user profile data
        loadUserProfile();

        return view;
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            progressBar.setVisibility(View.VISIBLE);
            String userId = currentUser.getUid();

            databaseReference.child("users").child(userId).get()
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful() && isAdded()) {
                            Map<String, Object> userData = (Map<String, Object>) task.getResult().getValue();
                            if (userData != null) {
                                // Set username
                                String username = (String) userData.get("username");
                                usernameTextView.setText(username);

                                // Set email
                                emailTextView.setText(currentUser.getEmail());

                                // Load avatar
                                String avatarUrl = (String) userData.get("avatarUrl");
                                if (avatarUrl != null && !avatarUrl.isEmpty() && getContext() != null) {
                                    Glide.with(getContext())
                                            .load(avatarUrl)
                                            .placeholder(R.drawable.placeholder_avatar)
                                            .into(avatarImageView);
                                }
                            }
                        } else if (isAdded()) {
                            Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}