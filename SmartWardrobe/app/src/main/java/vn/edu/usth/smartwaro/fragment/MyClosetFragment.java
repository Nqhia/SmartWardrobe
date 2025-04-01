package vn.edu.usth.smartwaro.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.mycloset.FavoriteSetCreationFragment;
import vn.edu.usth.smartwaro.network.FlaskNetwork;
import vn.edu.usth.smartwaro.mycloset.ClosetAdapter;

public class MyClosetFragment extends Fragment {
    private ViewPager2 viewPager;
    private TabLayoutMediator tabLayoutMediator;
    private TabLayout tabLayout;
    private ClosetAdapter pagerAdapter;
    private List<String> categories = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_closet, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        FloatingActionButton fabAddItem = view.findViewById(R.id.fabAddItem);
        FloatingActionButton btnFavourite = view.findViewById(R.id.favourite);

        fetchCategories();

        fabAddItem.setOnClickListener(v -> showUploadDialog());


        btnFavourite.setOnClickListener(v -> {
            // Mở FavoriteFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new FavoriteSetCreationFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void fetchCategories() {
        FlaskNetwork flaskNetwork = new FlaskNetwork();
        flaskNetwork.getCategories(new FlaskNetwork.OnCategoriesLoadedListener() {
            @Override
            public void onSuccess(List<String> newCategories) {
                requireActivity().runOnUiThread(() -> {
                    Log.d("CATEGORY_API", "Categories received: " + newCategories.toString());
                    if (newCategories.isEmpty()) {
                        Log.e("CATEGORY_API", "No categories received!");
                        return;
                    }

                    categories.clear();
                    categories.addAll(newCategories);

                    setupViewPager(); // Cập nhật TabLayout
                });
            }

            @Override
            public void onError(String message) {
                requireActivity().runOnUiThread(() -> {
                    Log.e("API_ERROR", "Failed to fetch categories: " + message);
                });
            }
        });
    }

    private void setupViewPager() {
        // Check if the fragment is attached
        if (!isAdded() || getActivity() == null) {
            Log.w("VIEWPAGER", "Fragment not attached, skipping setup");
            return;
        }

        Log.d("VIEWPAGER", "Setting up ViewPager with categories: " + categories);

        // Always create a new adapter instance to avoid reuse issues
        pagerAdapter = new ClosetAdapter(this, categories);
        viewPager.setAdapter(pagerAdapter); // Line 98: Safe now

        // Detach existing mediator if it exists
        if (tabLayoutMediator != null) {
            tabLayoutMediator.detach();
        }

        // Clear existing tabs
        tabLayout.removeAllTabs();

        // Create and attach the mediator
        tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Gallery");
            } else if (position - 1 < categories.size()) {
                tab.setText(categories.get(position - 1));
            }
        });
        tabLayoutMediator.attach();

        // Set up tab selection listener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab != null) {
                    viewPager.setCurrentItem(tab.getPosition(), true);
                    if (tab.getPosition() > 0 && tab.getPosition() - 1 < categories.size() &&
                            !FlaskNetwork.DEFAULT_CATEGORIES.contains(categories.get(tab.getPosition() - 1))) {
                        tab.view.setOnLongClickListener(v -> {
                            showDeleteCategoryDialog(categories.get(tab.getPosition() - 1));
                            return true;
                        });
                    } else {
                        tab.view.setOnLongClickListener(null);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab != null) {
                    tab.view.setOnLongClickListener(null);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Sync ViewPager2 with TabLayout
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (tabLayout.getTabAt(position) != null) {
                    tabLayout.selectTab(tabLayout.getTabAt(position));
                }
            }
        });
    }

    private void showUploadDialog() {
        UploadImageFragment uploadFragment = new UploadImageFragment();
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, uploadFragment)
                .addToBackStack(null)
                .commit();
    }

    private void showDeleteCategoryDialog(String categoryName) {
        if (!FlaskNetwork.DEFAULT_CATEGORIES.contains(categoryName)) {
            // Create and show a dialog to confirm category deletion
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Category")
                    .setMessage("Are you sure you want to delete the category '" + categoryName + "'?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        removeCategory(categoryName);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    private void removeCategory(String categoryName) {
        FlaskNetwork flaskNetwork = new FlaskNetwork();
        flaskNetwork.removeCategory(categoryName, new FlaskNetwork.OnCategoryOperationListener() {
            @Override
            public void onSuccess(String message) {
                requireActivity().runOnUiThread(() -> {
                    Log.d("CATEGORY_API", "Category removed: " + categoryName);

                    // ✅ Xóa category khỏi danh sách cục bộ trước khi cập nhật UI
                    categories.remove(categoryName);
                    setupViewPager(); // Cập nhật giao diện ngay lập tức
                    fetchCategories(); // Fetch lại từ server để đảm bảo đồng bộ
                });
            }

            @Override
            public void onError(String message) {
                requireActivity().runOnUiThread(() -> {
                    Log.e("API_ERROR", "Failed to remove category: " + message);
                });
            }
        });
    }

}