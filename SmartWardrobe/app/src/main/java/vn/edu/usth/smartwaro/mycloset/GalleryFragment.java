package vn.edu.usth.smartwaro.mycloset;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.network.FlaskNetwork;

public class GalleryFragment extends Fragment implements GalleryAdapter.OnImageClickListener {
    private static final String TAG = "GalleryFragment";

    private RecyclerView recyclerView;
    private GalleryAdapter adapter;
    private ProgressBar progressBar;
    private FlaskNetwork flaskNetwork;
    private MenuItem deleteMenuItem;
    private List<GalleryImage> galleryImages = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        recyclerView = view.findViewById(R.id.gallery_recycler_view);
        progressBar = view.findViewById(R.id.gallery_progress_bar);

        flaskNetwork = new FlaskNetwork();
        setupRecyclerView();
        loadImages();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.gallery_menu, menu);
        deleteMenuItem = menu.findItem(R.id.action_delete);
        deleteMenuItem.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            showDeleteConfirmationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        adapter = new GalleryAdapter(requireContext());
        adapter.setOnImageClickListener(this);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onImageClick(GalleryImage image) {
        if (galleryImages != null && !galleryImages.isEmpty()) {
            int position = galleryImages.indexOf(image);
            if (position != -1) {
                ImageViewerFragment viewerFragment = ImageViewerFragment.newInstance(
                        new ArrayList<>(galleryImages),
                        position
                );
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, viewerFragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    @Override
    public void onSelectionChanged(int selectedCount) {
        if (deleteMenuItem != null) {
            deleteMenuItem.setVisible(selectedCount > 0);
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Selected Images")
                .setMessage("Are you sure you want to delete the selected images?")
                .setPositiveButton("Delete", (dialog, which) -> deleteSelectedImages())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteSelectedImages() {
        List<GalleryImage> selectedImages = adapter.getSelectedImages();
        String[] filenames = selectedImages.stream()
                .map(GalleryImage::getFilename)
                .toArray(String[]::new);

        flaskNetwork.deleteImages(filenames, new FlaskNetwork.OnDeleteImagesListener() {
            @Override
            public void onSuccess(String[] deletedFiles, String[] failedFiles) {
                requireActivity().runOnUiThread(() -> {
                    adapter.setMultiSelectMode(false);
                    loadImages(); // Reload the gallery
                    if (failedFiles.length > 0) {
                        Toast.makeText(requireContext(),
                                "Some images couldn't be deleted",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String message) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(),
                            "Error deleting images: " + message,
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
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
                        galleryImages.clear();
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