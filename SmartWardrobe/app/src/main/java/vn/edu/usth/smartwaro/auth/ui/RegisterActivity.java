package vn.edu.usth.smartwaro.auth.ui;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import vn.edu.usth.smartwaro.utils.Constants;
import vn.edu.usth.smartwaro.auth.model.UserModel;
import vn.edu.usth.smartwaro.utils.PreferenceManager;
import vn.edu.usth.smartwaro.auth.utils.ValidationUtils;
import vn.edu.usth.smartwaro.auth.viewmodel.RegisterViewModel;
import vn.edu.usth.smartwaro.SmartWardrobe;
import vn.edu.usth.smartwaro.databinding.ActivityRegisterBinding;

import android.content.Intent;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private RegisterViewModel viewModel;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        setupViews();
        observeViewModel();
    }

    private void setupViews() {
        binding.registerButton.setOnClickListener(v -> {
            String username = binding.username.getText().toString();
            String email = binding.email.getText().toString();
            String password = binding.password.getText().toString();
            String confirmPassword = binding.confirmPassword.getText().toString();

            if (validateInput(username, email, password, confirmPassword)) {
                viewModel.register(username, email, password);
            }
        });

        binding.loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void observeViewModel() {
        viewModel.getRegisterResult().observe(this, result -> {
            switch (result.getStatus()) {
                case LOADING:
                    showLoading(true);
                    break;
                case SUCCESS:
                    showLoading(false);
                    saveUserSession(result.getData());
                    navigateToMain();
                    break;
                case ERROR:
                    showLoading(false);
                    showToast(result.getError());
                    break;
            }
        });
    }

    private boolean validateInput(String username, String email, String password, String confirmPassword) {
        boolean isValid = true;

        // Validate username
        String usernameError = ValidationUtils.getUsernameError(username);
        if (usernameError != null) {
            binding.username.setError(usernameError);
            isValid = false;
        }

        // Validate email
        String emailError = ValidationUtils.getEmailError(email);
        if (emailError != null) {
            binding.email.setError(emailError);
            isValid = false;
        }

        // Validate password
        String passwordError = ValidationUtils.getPasswordError(password);
        if (passwordError != null) {
            binding.password.setError(passwordError);
            isValid = false;
        }

        // Validate confirm password
        if (!password.equals(confirmPassword)) {
            binding.confirmPassword.setError("Passwords do not match");
            isValid = false;
        }

        return isValid;
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.registerButton.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void saveUserSession(UserModel user) {
        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
        preferenceManager.putString(Constants.KEY_USER_ID, user.getId());
        preferenceManager.putString(Constants.KEY_NAME, user.getName());
        preferenceManager.putString(Constants.KEY_EMAIL, user.getEmail());
    }

    private void navigateToMain() {
        Intent intent = new Intent(getApplicationContext(), SmartWardrobe.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
