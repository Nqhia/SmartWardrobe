package vn.edu.usth.smartwaro.mycloset;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import java.util.List;

public class ClosetAdapter extends FragmentStateAdapter {
    private List<String> categories;

    public ClosetAdapter(@NonNull Fragment fragment, List<String> categories) {
        super(fragment);
        this.categories = categories;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new GalleryFragment();
        } else {
            CategoriesFragment fragment = new CategoriesFragment();
            Bundle args = new Bundle();
            args.putString("category", categories.get(position - 1));
            fragment.setArguments(args);
            return fragment;
        }
    }

    @Override
    public int getItemCount() {
        return categories.size() + 1;
    }

    public void setCategories(List<String> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged();
    }


}
