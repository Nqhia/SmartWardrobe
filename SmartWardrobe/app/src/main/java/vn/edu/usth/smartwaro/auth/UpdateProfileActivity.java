package vn.edu.usth.smartwaro.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Map;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.auth.utils.ValidationUtils;
import vn.edu.usth.smartwaro.auth.viewmodel.UpdateProfileViewModel;
import vn.edu.usth.smartwaro.databinding.ActivityUpdateProfileBinding;

public class UpdateProfileActivity extends AppCompatActivity {
    private ActivityUpdateProfileBinding binding;
    private UpdateProfileViewModel viewModel;
    private static final int REQUEST_CODE_PICK_IMAGE = 1001;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(UpdateProfileViewModel.class);

        setupViews();
        observeViewModel();
        viewModel.loadUserProfile();
    }

    private void setupViews() {
        binding.saveButton.setOnClickListener(v -> updateProfile());
    }

    private void observeViewModel() {
        viewModel.getProfileData().observe(this, result -> {
            switch (result.getStatus()) {
                case LOADING:
                    showLoading(true);
                    break;
                case SUCCESS:
                    showLoading(false);
                    if (result.getData() != null) {
                        binding.usernameEditText.setText(result.getData().getName());
                        binding.emailEditText.setText(result.getData().getEmail());

                        // Load avatar
                        if (result.getData().getImage() != null && !result.getData().getImage().isEmpty()) {
                            Glide.with(this)
                                    .load(result.getData().getImage())
                                    .placeholder(R.drawable.placeholder_avatar)
                                    .into(binding.avatarImageView);
                        }
                    }
                    break;
                case ERROR:
                    showLoading(false);
                    showToast(result.getError());
                    break;
            }
        });

        viewModel.getUpdateResult().observe(this, result -> {
            switch (result.getStatus()) {
                case LOADING:
                    showLoading(true);
                    break;
                case SUCCESS:
                    showLoading(false);
                    showToast("Profile updated successfully");
                    finish();
                    break;
                case ERROR:
                    showLoading(false);
                    showToast(result.getError());
                    break;
            }
        });
    }

    private void updateProfile() {
        String newUsername = binding.usernameEditText.getText().toString().trim();

        if (!ValidationUtils.isValidUsername(newUsername)) {
            showToast("Invalid username. Must be 3-20 characters.");
            return;
        }

        String userId = viewModel.getCurrentUserId();
        if (userId != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", newUsername);
            viewModel.updateProfile(userId, updates);
        }
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.saveButton.setEnabled(!isLoading);
        binding.changeAvatarButton.setEnabled(!isLoading);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
