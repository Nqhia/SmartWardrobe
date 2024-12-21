package vn.edu.usth.smartwaro.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import vn.edu.usth.smartwaro.fragment.ProfileFragment;
import vn.edu.usth.smartwaro.R;

public class SettingsActivity extends AppCompatActivity {
    ImageButton themeButton, profileButton;
    ImageButton backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

       // themeButton = findViewById(R.id.theme_button);
        profileButton = findViewById(R.id.profile_button);
//        backButton = findViewById(R.id.close_setting_button);
//
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                finish();
//            }
//        });


//        themeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                loadThemeFragment();
//            }
//        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadProfileFragment();
            }
        });
    }


//    public void loadThemeFragment() {
//
//        Fragment themeFragment = new ThemeFragment();
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.setting_container, themeFragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
//    }

    public void loadProfileFragment() {


        Fragment profileFragment = new ProfileFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.setting_container, profileFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}