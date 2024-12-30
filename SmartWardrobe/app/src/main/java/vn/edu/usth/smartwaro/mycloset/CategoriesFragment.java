package vn.edu.usth.smartwaro.mycloset;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.network.FlaskNetwork;

public class CategoriesFragment extends Fragment {
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private FlaskNetwork flaskNetwork;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<String> categories;
    private String selectedCategory = "uncategorized"; // Default selection

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        flaskNetwork = new FlaskNetwork();

        recyclerView = view.findViewById(R.id.categories_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        FloatingActionButton fabAddCategory = view.findViewById(R.id.fabAddCategory);

        // Initialize FlaskNetwork


        // Initialize categories list
        categories = new ArrayList<>();

        // Setup RecyclerView
        setupRecyclerView();

        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::loadCategories);

        // Setup FAB
        fabAddCategory.setOnClickListener(v -> showAddCategoryDialog());

        // Load categories
        loadCategories();

        return view;
    }

    private void setupRecyclerView() {
        adapter = new CategoryAdapter(categories, selectedCategory, category -> {
            selectedCategory = category;
            // Handle category selection
            // You can add your logic here for what happens when a category is selected
            Toast.makeText(getContext(), "Selected: " + category, Toast.LENGTH_SHORT).show();
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadCategories() {
        if (!isAdded()) return;

        swipeRefreshLayout.setRefreshing(true);
        flaskNetwork.getCategories(new FlaskNetwork.OnCategoriesLoadedListener() {
            @Override
            public void onSuccess(List<String> loadedCategories) {
                requireActivity().runOnUiThread(() -> {
                    categories.clear();
                    categories.addAll(loadedCategories);
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                });
            }

            @Override
            public void onError(String message) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                });
            }
        });
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add New Category");

        final EditText input = new EditText(requireContext());
        input.setHint("Enter category name");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String categoryName = input.getText().toString().trim();
            if (!categoryName.isEmpty()) {
                addNewCategory(categoryName);
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.show();
    }

    private void addNewCategory(String categoryName) {
        // Validate input
        if (categoryName.isEmpty()) {
            Toast.makeText(getContext(), "Category name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check for existing category
        if (categories.contains(categoryName)) {
            Toast.makeText(getContext(), "Category already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        flaskNetwork.addCategory(categoryName, new FlaskNetwork.OnCategoryOperationListener() {
            @Override
            public void onSuccess(String message) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    loadCategories();
                });
            }

            @Override
            public void onError(String message) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView.setAdapter(null);
        adapter = null;
    }
}
