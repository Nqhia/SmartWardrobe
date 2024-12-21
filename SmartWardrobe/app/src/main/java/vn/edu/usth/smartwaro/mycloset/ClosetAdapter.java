package vn.edu.usth.smartwaro.mycloset;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vn.edu.usth.smartwaro.mycloset.GalleryFragment;
import vn.edu.usth.smartwaro.mycloset.CategoriesFragment;

public class ClosetAdapter extends FragmentStateAdapter {

    public ClosetAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new GalleryFragment();
            case 1:
                return new CategoriesFragment();
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Gallery and Categories
    }
}