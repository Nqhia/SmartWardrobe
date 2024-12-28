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
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import vn.edu.usth.smartwaro.auth.ui.LoginActivity;
import vn.edu.usth.smartwaro.chat.ChatActivity;
import vn.edu.usth.smartwaro.utils.PreferenceManager;
import vn.edu.usth.smartwaro.settings.SettingsActivity;
import vn.edu.usth.smartwaro.fragment.WardrobeFragment;
import vn.edu.usth.smartwaro.fragment.MyClosetFragment;
import vn.edu.usth.smartwaro.fragment.ProfileFragment;
import vn.edu.usth.smartwaro.fragment.CalendarFragment;

public class SmartWardrobe extends AppCompatActivity {
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String KEY_SELECTED_TAB = "selected_tab";
    private static final String LOGIN_STATUS_KEY = "IsLoggedIn";
    private int selectedTab;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        // Check if user is logged in
        if (firebaseAuth.getCurrentUser() == null) {
            redirectToLogin();
            return;  // Important: stop the execution of onCreate
        }

        setContentView(R.layout.activity_smartwardrobe);

        // Setup auth state listener
        setupAuthStateListener();

        // Setup toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // Setup bottom navigation
        setupBottomNavigation();

        // Load saved tab or default
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        selectedTab = prefs.getInt(KEY_SELECTED_TAB, R.id.stylist);

        if (savedInstanceState == null) {
            switchFragment(selectedTab);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private void setupAuthStateListener() {
        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                redirectToLogin();
            }
        };
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            switchFragment(itemId);

            // Save selected tab
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            editor.putInt(KEY_SELECTED_TAB, itemId);
            editor.apply();

            return true;
        });
    }

    private void switchFragment(int itemId) {
        if (itemId == R.id.social) {
            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
            return;
        }

        Fragment fragment = null;

        if (itemId == R.id.stylist) {
            fragment = new WardrobeFragment();
        } else if (itemId == R.id.my_closet) {
            fragment = new MyClosetFragment();
        } else if (itemId == R.id.profile) {
            fragment = new ProfileFragment();
        } else if (itemId == R.id.calendar) {
            fragment = new CalendarFragment();
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
        }

        return super.onOptionsItemSelected(item);
    }



}