package vn.edu.usth.smartwaro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.smartwaro.auth.RegisterActivity;

public class WelcomeActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String FIRST_LAUNCH_KEY = "is_first_launch";
    private ViewPager2 viewPager;
    private Button btnStart;
    private WelcomeAdapter welcomeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isFirstLaunch()) {
            goToMainActivity();
            return;
        }

        setContentView(R.layout.activity_welcome);

        viewPager = findViewById(R.id.viewPagerWelcome);
        btnStart = findViewById(R.id.btnStart);

        List<WelcomePage> pages = new ArrayList<>();
        pages.add(new WelcomePage(
                R.drawable.welcome_page1,
                "Welcome to SmartWaro",
                "Organize your wardrobe with ease and style."
        ));
        pages.add(new WelcomePage(
                R.drawable.welcome_page2,
                "Smart Outfit Planning",
                "Create and manage your perfect outfits."
        ));
        pages.add(new WelcomePage(
                R.drawable.welcome_page3,
                "Personalized Recommendations",
                "Get outfit suggestions tailored just for you."
        ));

        welcomeAdapter = new WelcomeAdapter(pages);
        viewPager.setAdapter(welcomeAdapter);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFirstLaunchFlag(false);
                Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Welcome Page Model
    public static class WelcomePage {
        private int imageResourceId;
        private String title;
        private String description;

        public WelcomePage(int imageResourceId, String title, String description) {
            this.imageResourceId = imageResourceId;
            this.title = title;
            this.description = description;
        }

        public int getImageResourceId() {
            return imageResourceId;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }
    }

    // Welcome Adapter
    public class WelcomeAdapter extends RecyclerView.Adapter<WelcomeAdapter.WelcomeViewHolder> {
        private List<WelcomePage> pages;

        public WelcomeAdapter(List<WelcomePage> pages) {
            this.pages = pages;
        }

        @NonNull
        @Override
        public WelcomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_welcome_page, parent, false);
            return new WelcomeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull WelcomeViewHolder holder, int position) {
            WelcomePage page = pages.get(position);
            holder.imageView.setImageResource(page.getImageResourceId());
            holder.txtTitle.setText(page.getTitle());
            holder.txtDescription.setText(page.getDescription());
        }

        @Override
        public int getItemCount() {
            return pages.size();
        }

        public class WelcomeViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView txtTitle;
            TextView txtDescription;

            public WelcomeViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imgWelcome);
                txtTitle = itemView.findViewById(R.id.txtWelcomeTitle);
                txtDescription = itemView.findViewById(R.id.txtWelcomeDescription);
            }
        }
    }

    private boolean isFirstLaunch() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(FIRST_LAUNCH_KEY, true);
    }

    private void setFirstLaunchFlag(boolean isFirstLaunch) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(FIRST_LAUNCH_KEY, isFirstLaunch);
        editor.apply();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, SmartWardrobe.class);
        startActivity(intent);
        finish();
    }
}