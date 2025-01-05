package vn.edu.usth.smartwaro.mycloset;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.network.FlaskNetwork;

public class CategoriesFragment extends Fragment {
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private ProgressBar progressBar;
    private final List<GalleryImage> categoryImages = new ArrayList<>();
    private String currentCategory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progress_bar); // Add this to your layout

        // Get category from arguments
        currentCategory = getArguments() != null ? getArguments().getString("category") : "Unknown";

        setupRecyclerView();
        loadCategoryImagesFromServer();

        return view;
    }

    private void setupRecyclerView() {
        // Initialize adapter with current category
        adapter = new CategoryAdapter(requireContext(), currentCategory);

        // Calculate span count based on screen width
        int spanCount = getResources().getConfiguration().screenWidthDp / 180;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), Math.max(2, spanCount)));

        // Set click listener
        adapter.setOnImageClickListener(image -> {
            // Handle image click - open image viewer
            int position = categoryImages.indexOf(image);
            if (position != -1) {
                ImageViewerFragment viewerFragment = ImageViewerFragment.newInstance(
                        new ArrayList<>(categoryImages),
                        position
                );
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, viewerFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void loadCategoryImagesFromServer() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        FlaskNetwork flaskNetwork = new FlaskNetwork();
        flaskNetwork.getUserImages(currentCategory, new FlaskNetwork.OnImagesLoadedListener() {
            @Override
            public void onSuccess(String[] imageUrls) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    try {
                        categoryImages.clear();
                        for (String url : imageUrls) {
                            if (url != null && !url.isEmpty()) {
                                String filename = extractFilename(url);
                                categoryImages.add(new GalleryImage(
                                        filename,
                                        filename,
                                        "",
                                        url,
                                        currentCategory
                                ));
                            }
                        }
                        adapter.setImages(categoryImages);
                    } catch (Exception e) {
                        Toast.makeText(requireContext(),
                                "Error processing images",
                                Toast.LENGTH_SHORT).show();
                    } finally {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(),
                            "Error loading images: " + message,
                            Toast.LENGTH_SHORT).show();
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private String extractFilename(String url) {
        if (url == null || url.isEmpty()) {
            return "unknown_" + System.currentTimeMillis();
        }
        int lastSlashIndex = url.lastIndexOf('/');
        if (lastSlashIndex != -1 && lastSlashIndex < url.length() - 1) {
            return url.substring(lastSlashIndex + 1);
        }
        return "unknown_" + System.currentTimeMillis();
    }
}
