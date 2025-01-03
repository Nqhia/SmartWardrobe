package vn.edu.usth.smartwaro.mycloset;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.network.FlaskNetwork;

public class CategoriesFragment extends Fragment {
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<String> images = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // Grid layout with 2 columns

        // Load images based on category
        String category = getArguments() != null ? getArguments().getString("category") : "Unknown";
        loadCategoryImagesFromServer(category);

        // Setup adapter
        adapter = new CategoryAdapter(
                images,
                category,
                selectedCategory -> Toast.makeText(getContext(), "Selected: " + selectedCategory, Toast.LENGTH_SHORT).show()
        );
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void loadCategoryImagesFromServer(String category) {
        FlaskNetwork flaskNetwork = new FlaskNetwork();
        flaskNetwork.getUserImages(category, new FlaskNetwork.OnImagesLoadedListener() {
            @Override
            public void onSuccess(String[] imageUrls) {
                // Cập nhật giao diện trên main thread
                getActivity().runOnUiThread(() -> {
                    images.clear();
                    images.addAll(Arrays.asList(imageUrls));

                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(String message) {
                // Hiển thị lỗi trên main thread
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Error loading images: " + message, Toast.LENGTH_LONG).show()
                );
            }
        });
    }

}
