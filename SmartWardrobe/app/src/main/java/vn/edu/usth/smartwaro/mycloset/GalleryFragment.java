package vn.edu.usth.smartwaro.mycloset;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
    private ClosetViewModel closetViewModel;
    private FlaskNetwork flaskNetwork;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        recyclerView = view.findViewById(R.id.gallery_recycler_view);
        progressBar = view.findViewById(R.id.gallery_progress_bar);

        closetViewModel = new ViewModelProvider(requireActivity()).get(ClosetViewModel.class);
        flaskNetwork = new FlaskNetwork();

        setupRecyclerView();
        loadImages();

        return view;
    }

    private void setupRecyclerView() {
        adapter = new GalleryAdapter(requireContext());
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        recyclerView.setAdapter(adapter);

        adapter.setOnImageClickListener(image -> {
            if (image != null && image.getOriginalFilename() != null) {
                Toast.makeText(requireContext(),
                        "Selected: " + image.getOriginalFilename(),
                        Toast.LENGTH_SHORT).show();
                // Handle image selection
                handleImageSelection(image);
            }
        });
    }

    private void handleImageSelection(GalleryImage image) {
        // TODO: Implement image selection handling
        Log.d(TAG, "Selected image: " + image.getUrl());
    }

    private void loadImages() {
        progressBar.setVisibility(View.VISIBLE);

        flaskNetwork.getUserImages(requireContext(), new FlaskNetwork.OnImagesLoadedListener() {
            @Override
            public void onSuccess(String[] imageUrls) {
                if (!isAdded()) {
                    return;
                }

                requireActivity().runOnUiThread(() -> {
                    try {
                        List<GalleryImage> galleryImages = new ArrayList<>();
                        for (String url : imageUrls) {
                            if (url != null && !url.isEmpty()) {
                                String filename = extractFilename(url);
                                galleryImages.add(new GalleryImage(
                                        filename,
                                        filename,
                                        "", // TODO: Add proper date handling
                                        url
                                ));
                            }
                        }
                        adapter.setImages(galleryImages);
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing images", e);
                        Toast.makeText(requireContext(),
                                "Error processing images",
                                Toast.LENGTH_SHORT).show();
                    } finally {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }

                requireActivity().runOnUiThread(() -> {
                    Log.e(TAG, "Error loading images: " + message);
                    Toast.makeText(requireContext(),
                            "Error loading images: " + message,
                            Toast.LENGTH_SHORT).show();
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