package vn.edu.usth.smartwaro;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import vn.edu.usth.smartwaro.auth.UpdateProfileActivity;
import vn.edu.usth.smartwaro.auth.ui.LoginActivity;
import vn.edu.usth.smartwaro.chat.UsersActivity;
import vn.edu.usth.smartwaro.mycloset.FavoriteSetListFragment;
import vn.edu.usth.smartwaro.settings.SettingsActivity;
import vn.edu.usth.smartwaro.fragment.WardrobeFragment;
import vn.edu.usth.smartwaro.fragment.MyClosetFragment;
import vn.edu.usth.smartwaro.fragment.ProfileFragment;
import vn.edu.usth.smartwaro.weather.WeatherFragment;

public class SmartWardrobe extends AppCompatActivity {
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String KEY_SELECTED_TAB = "selected_tab";
    private static final String LOGIN_STATUS_KEY = "IsLoggedIn";
    private int selectedTab;
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

        // Initialize bottom navigation views
        View stylistView = findViewById(R.id.stylist);
        View weatherView = findViewById(R.id.weather);
        View closetView = findViewById(R.id.my_closet);
        View favoriteView = findViewById(R.id.favorite);
        View chatView = findViewById(R.id.chatButton);

        ImageView stylistIcon = findViewById(R.id.stylist_icon);
        ImageView weatherIcon = findViewById(R.id.weather_icon);
        ImageView closetIcon = findViewById(R.id.closet_icon);
        ImageView favoriteIcon = findViewById(R.id.favorite_icon);
        ImageView chatIcon = findViewById(R.id.chat_icon);

        // Setup bottom navigation
        setupBottomNavigation(stylistView, weatherView, closetView, favoriteView, chatView,
                stylistIcon, weatherIcon, closetIcon, favoriteIcon, chatIcon);

        // Load saved tab or default
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        selectedTab = prefs.getInt(KEY_SELECTED_TAB, R.id.stylist);

        if (savedInstanceState == null) {
            switchFragment(selectedTab);
            updateSelectedState(selectedTab, stylistView, weatherView, closetView, favoriteView, chatView,
                    stylistIcon, weatherIcon, closetIcon, favoriteIcon, chatIcon);
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

    private void setupBottomNavigation(View stylistView, View weatherView, View closetView,
                                       View favoriteView, View chatView, ImageView stylistIcon,
                                       ImageView weatherIcon, ImageView closetIcon, ImageView favoriteIcon,
                                       ImageView chatIcon) {
        stylistView.setOnClickListener(v -> {
            switchFragment(R.id.stylist);
            updateSelectedState(R.id.stylist, stylistView, weatherView, closetView, favoriteView, chatView,
                    stylistIcon, weatherIcon, closetIcon, favoriteIcon, chatIcon);
            saveSelectedTab(R.id.stylist);
        });

        weatherView.setOnClickListener(v -> {
            switchFragment(R.id.weather);
            updateSelectedState(R.id.weather, stylistView, weatherView, closetView, favoriteView, chatView,
                    stylistIcon, weatherIcon, closetIcon, favoriteIcon, chatIcon);
            saveSelectedTab(R.id.weather);
        });

        closetView.setOnClickListener(v -> {
            switchFragment(R.id.my_closet);
            updateSelectedState(R.id.my_closet, stylistView, weatherView, closetView, favoriteView, chatView,
                    stylistIcon, weatherIcon, closetIcon, favoriteIcon, chatIcon);
            saveSelectedTab(R.id.my_closet);
        });

        favoriteView.setOnClickListener(v -> {
            switchFragment(R.id.favorite);
            updateSelectedState(R.id.favorite, stylistView, weatherView, closetView, favoriteView, chatView,
                    stylistIcon, weatherIcon, closetIcon, favoriteIcon, chatIcon);
            saveSelectedTab(R.id.favorite);
        });

        chatView.setOnClickListener(v -> {
            Intent intent = new Intent(this, UsersActivity.class);
            startActivity(intent);
            updateSelectedState(R.id.chatButton, stylistView, weatherView, closetView, favoriteView, chatView,
                    stylistIcon, weatherIcon, closetIcon, favoriteIcon, chatIcon);
            saveSelectedTab(R.id.chatButton);
        });
    }

    private void switchFragment(int itemId) {
        if (itemId == R.id.chatButton) {
            return;
        }

        Fragment fragment = null;

        if (itemId == R.id.my_closet) {
            fragment = new MyClosetFragment();
        } else if (itemId == R.id.stylist) {
            fragment = new WardrobeFragment();
        } else if (itemId == R.id.weather) {
            fragment = new WeatherFragment();
        } else if (itemId == R.id.favorite) {
            fragment = new FavoriteSetListFragment();
        }

        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    private void updateSelectedState(int selectedItemId, View stylistView, View weatherView, View closetView,
                                     View favoriteView, View chatView, ImageView stylistIcon,
                                     ImageView weatherIcon, ImageView closetIcon, ImageView favoriteIcon,
                                     ImageView chatIcon) {
        // Reset all to unselected state
        resetNavigationItem(stylistView, stylistIcon, R.id.stylist_icon);
        resetNavigationItem(weatherView, weatherIcon, R.id.weather_icon);
        resetNavigationItem(closetView, closetIcon, R.id.closet_icon);
        resetNavigationItem(favoriteView, favoriteIcon, R.id.favorite_icon);
        resetNavigationItem(chatView, chatIcon, R.id.chat_icon);

        // Set selected state for the clicked item
        if (selectedItemId == R.id.stylist) {
            applySelectedAnimation(stylistIcon);
            stylistIcon.setBackgroundResource(R.drawable.circle_black_background);
            stylistIcon.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
            setTextColor(stylistView, R.color.nav_selected_color);
        }
        if (selectedItemId == R.id.weather) {
            applySelectedAnimation(weatherIcon);
            weatherIcon.setBackgroundResource(R.drawable.circle_black_background);
            weatherIcon.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
            setTextColor(weatherView, R.color.nav_selected_color);
        }
        if (selectedItemId == R.id.my_closet) {
            applySelectedAnimation(closetIcon);
            closetIcon.setBackgroundResource(R.drawable.circle_black_background);
            closetIcon.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
            setTextColor(closetView, R.color.nav_selected_color);
        }
        if (selectedItemId == R.id.favorite) {
            applySelectedAnimation(favoriteIcon);
            favoriteIcon.setBackgroundResource(R.drawable.circle_black_background);
            favoriteIcon.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
            setTextColor(favoriteView, R.color.nav_selected_color);
        }
        if (selectedItemId == R.id.chatButton) {
            applySelectedAnimation(chatIcon);
            chatIcon.setBackgroundResource(R.drawable.circle_black_background);
            chatIcon.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
            setTextColor(chatView, R.color.nav_selected_color);
        }
    }

    private void applySelectedAnimation(ImageView icon) {
        // Reset scale and position to normal
        icon.setScaleX(1.0f);
        icon.setScaleY(1.0f);
        icon.setElevation(4f);
        icon.setTranslationY(0f);

        // Create animation for scaling, elevation, and translation
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(icon, "scaleX", 1.0f, 1.3f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(icon, "scaleY", 1.0f, 1.3f);
        ObjectAnimator elevation = ObjectAnimator.ofFloat(icon, "elevation", 4f, 16f);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(icon, "translationY", 0f, -30f);

        animatorSet.playTogether(scaleX, scaleY, elevation, translationY);
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.start();
    }

    private void resetNavigationItem(View view, ImageView icon, int iconId) {
        // Reset animation
        icon.setScaleX(1.0f);
        icon.setScaleY(1.0f);
        icon.setElevation(4f);
        icon.setTranslationY(0f);

        icon.setBackground(null); // Remove background
        icon.setColorFilter(ContextCompat.getColor(this, R.color.nav_unselected_color));
        setTextColor(view, R.color.nav_unselected_color);
    }

    private void setTextColor(View view, int colorRes) {
        for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
            View child = ((ViewGroup) view).getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setTextColor(ContextCompat.getColor(this, colorRes));
                break;
            }
        }
    }

    private void saveSelectedTab(int itemId) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt(KEY_SELECTED_TAB, itemId);
        editor.apply();
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
        } else if (itemId == R.id.profile) {
            startActivity(new Intent(this, UpdateProfileActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}