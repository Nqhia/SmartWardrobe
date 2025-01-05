package vn.edu.usth.smartwaro.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.auth.ui.LoginActivity;
import vn.edu.usth.smartwaro.utils.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {
    ImageButton themeButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        themeButton = findViewById(R.id.theme_button);

        logoutButton = findViewById(R.id.logoutButton);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    private void logoutUser() {
        try {
            FirebaseAuth.getInstance().signOut();

            PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
            preferenceManager.clear();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Logout failed", Toast.LENGTH_SHORT).show();
        }
    }
}
