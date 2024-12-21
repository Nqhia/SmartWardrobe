package vn.edu.usth.smartwaro.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.mycloset.ClosetAdapter;

public class MyClosetFragment extends Fragment {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fabAddItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_closet, container, false);

        // Initialize views
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        fabAddItem = view.findViewById(R.id.fabAddItem);

        // Setup ViewPager with adapter
        setupViewPager();

        // Setup FAB
        fabAddItem.setOnClickListener(v -> showUploadDialog());

        return view;
    }

    private void setupViewPager() {
        ClosetAdapter pagerAdapter = new ClosetAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Gallery");
                    break;
                case 1:
                    tab.setText("Categories");
                    break;
            }
        }).attach();
    }

    private void showUploadDialog() {
        UploadImageFragment uploadFragment = new UploadImageFragment();
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, uploadFragment)
                .addToBackStack(null)
                .commit();
    }
}