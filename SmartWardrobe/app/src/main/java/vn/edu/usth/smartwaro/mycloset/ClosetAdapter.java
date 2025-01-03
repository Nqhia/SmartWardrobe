package vn.edu.usth.smartwaro.mycloset;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ClosetAdapter extends FragmentStateAdapter {
    public ClosetAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new GalleryFragment(); // Tab "Gallery"
        } else {
            CategoriesFragment fragment = new CategoriesFragment();
            fragment.setArguments(getCategoryBundle(position));
            return fragment;
        }
    }

    @Override
    public int getItemCount() {
        return 5; // 1 "Gallery" + 4 Categories
    }

    private Bundle getCategoryBundle(int position) {
        Bundle args = new Bundle();
        switch (position) {
            case 1:
                args.putString("category", "Short Sleeves");
                break;
            case 2:
                args.putString("category", "Long Sleeves");
                break;
            case 3:
                args.putString("category", "Short Leggings");
                break;
            case 4:
                args.putString("category", "Long Leggings");
                break;
        }
        return args;
    }
}
