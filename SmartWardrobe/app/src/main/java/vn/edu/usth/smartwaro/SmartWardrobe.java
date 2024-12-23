package vn.edu.usth.smartwaro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import vn.edu.usth.smartwaro.auth.LoginActivity;
import vn.edu.usth.smartwaro.settings.SettingsActivity;
import vn.edu.usth.smartwaro.fragment.WardrobeFragment;
import vn.edu.usth.smartwaro.fragment.MyClosetFragment;

public class SmartWardrobe extends AppCompatActivity {
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String KEY_SELECTED_TAB = "selected_tab";

    private int selectedTab;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is logged in
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smartwardrobe);

        // Setup toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // Setup bottom navigation
        setupBottomNavigation();

        if (savedInstanceState == null) {
            switchFragment(R.id.wardrobe); // Default tab
        }

        // Set initial fragment
        switchFragment(selectedTab);
    }

    private void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            selectedTab = item.getItemId();
            switchFragment(selectedTab);
            return true;
        });

        // Set the initially selected tab
        bottomNavigationView.setSelectedItemId(selectedTab);
    }

    private void switchFragment(int itemId) {
        Fragment fragment = null;

        if (itemId == R.id.wardrobe) {
            fragment = new WardrobeFragment();
        } else if (itemId == R.id.my_closet) {
            fragment = new MyClosetFragment();
        }

        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            // Save selected tab
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            editor.putInt(KEY_SELECTED_TAB, itemId);
            editor.apply();
        }
    }

    public void showFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.setting_button) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (itemId == R.id.logout_button) {
            logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();

        // Clear selected tab preference
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.remove(KEY_SELECTED_TAB);
        editor.apply();

        // Redirect to login
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }
}
