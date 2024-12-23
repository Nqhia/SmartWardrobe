package vn.edu.usth.smartwaro.mycloset;

import android.os.Bundle;
import android.util.Log;
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

public class GalleryFragment extends Fragment {

    private static final String TAG = "GalleryFragment";

    private RecyclerView recyclerView;
    private GalleryAdapter adapter;
    private ProgressBar progressBar;
    private FlaskNetwork flaskNetwork;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);

        // Initialize views
        recyclerView = rootView.findViewById(R.id.gallery_recycler_view);
        progressBar = rootView.findViewById(R.id.gallery_progress_bar);

        // Initialize FlaskNetwork
        flaskNetwork = new FlaskNetwork();

        // Setup RecyclerView
        setupRecyclerView();

        // Load images from server
        loadImagesFromServer();

        return rootView;
    }

    private void setupRecyclerView() {
        adapter = new GalleryAdapter(requireContext());
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        recyclerView.setAdapter(adapter);

        adapter.setOnImageClickListener(image -> {
            Toast.makeText(requireContext(), "Selected: " + image.getOriginalFilename(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Selected image URL: " + image.getUrl());
        });
    }

    private void loadImagesFromServer() {
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "Fetching images from server...");

        flaskNetwork.getUserImages(requireContext(), new FlaskNetwork.OnImagesLoadedListener() {
            @Override
            public void onSuccess(String[] imageUrls) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    Log.d(TAG, "Images loaded: " + imageUrls.length);
                    List<GalleryImage> galleryImages = new ArrayList<>();
                    for (String url : imageUrls) {
                        if (url != null && !url.isEmpty()) {
                            Log.d(TAG, "Adding image URL: " + url);
                            String filename = extractFilename(url);
                            galleryImages.add(new GalleryImage(filename, filename, "", url));
                        }
                    }

                    adapter.setImages(galleryImages);
                    progressBar.setVisibility(View.GONE);
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    Log.e(TAG, "Error loading images: " + message);
                    Toast.makeText(requireContext(), "Error loading images: " + message, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
            }
        });
    }


    private String extractFilename(String url) {
        try {
            if (url == null || url.isEmpty()) {
                return "unknown_" + System.currentTimeMillis();
            }
            int lastSlashIndex = url.lastIndexOf('/');
            if (lastSlashIndex != -1 && lastSlashIndex < url.length() - 1) {
                return url.substring(lastSlashIndex + 1);
            }
            return "unknown_" + System.currentTimeMillis();
        } catch (Exception e) {
            Log.e(TAG, "Error extracting filename from URL: " + url, e);
            return "unknown_" + System.currentTimeMillis();
        }
    }
}
