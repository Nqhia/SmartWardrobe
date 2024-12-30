package vn.edu.usth.smartwaro.chat;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import vn.edu.usth.smartwaro.databinding.ActivityUsersBinding;
import vn.edu.usth.smartwaro.utils.PreferenceManager;

public class UsersActivity extends AppCompatActivity {

    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
//        getUsers();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }
}
