package vn.edu.usth.smartwaro.mycloset;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.mycloset.CategoryAdapter;

public class CategoriesFragment extends Fragment {
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        recyclerView = view.findViewById(R.id.categories_recycler_view);
        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView() {
        List<CategoryItem> categories = Arrays.asList(
                new CategoryItem("Tops", R.drawable.top_category),
                new CategoryItem("Bottoms", R.drawable.trousers_category),
                new CategoryItem("Dresses", R.drawable.skirt_category),
                new CategoryItem("Outerwear", R.drawable.dress_category),
                new CategoryItem("Shoes", R.drawable.heels_category)
        );

        adapter = new CategoryAdapter(requireContext(), categories);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
    }

    public static class CategoryItem {
        public final String name;
        public final int iconResId;

        public CategoryItem(String name, int iconResId) {
            this.name = name;
            this.iconResId = iconResId;
        }
    }
}