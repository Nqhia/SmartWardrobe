package vn.edu.usth.smartwaro.auth.ui;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import vn.edu.usth.smartwaro.utils.Constants;
import vn.edu.usth.smartwaro.auth.model.UserModel;
import vn.edu.usth.smartwaro.utils.PreferenceManager;
import vn.edu.usth.smartwaro.auth.utils.ValidationUtils;
import vn.edu.usth.smartwaro.auth.viewmodel.LoginViewModel;
import vn.edu.usth.smartwaro.databinding.ActivityLoginBinding;
import vn.edu.usth.smartwaro.SmartWardrobe;

import android.content.Intent;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());

        // Check if user is already logged in
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            navigateToMain();
            return;
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        setupViews();
        observeViewModel();
    }
    private void saveUserSession(UserModel user) {
        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
        preferenceManager.putString(Constants.KEY_USER_ID, user.getId());
        preferenceManager.putString(Constants.KEY_NAME, user.getName());
        preferenceManager.putString(Constants.KEY_EMAIL, user.getEmail());
        Log.d("LoginActivity", "Verifying saved data:");
        Log.d("LoginActivity", "Is signed in: " + preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN));
        Log.d("LoginActivity", "Saved user ID: " + preferenceManager.getString(Constants.KEY_USER_ID));
        Log.d("LoginActivity", "Saved name: " + preferenceManager.getString(Constants.KEY_NAME));
        Log.d("LoginActivity", "Saved email: " + preferenceManager.getString(Constants.KEY_EMAIL));
    }

    private boolean validateInput(String email, String password) {
        boolean isValid = true;

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

        return isValid;
    }

    private void setupViews() {
        binding.loginButton.setOnClickListener(v -> {
            String email = binding.email.getText().toString();
            String password = binding.password.getText().toString();

            if (validateInput(email, password)) {
                viewModel.login(email, password);
            }
        });

        binding.registerButton.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

    }

    private void observeViewModel() {
        viewModel.getLoginResult().observe(this, result -> {
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

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.loginButton.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToMain() {
        Intent intent = new Intent(getApplicationContext(), SmartWardrobe.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

