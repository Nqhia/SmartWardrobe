package vn.edu.usth.smartwaro.mycloset;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.network.FlaskNetwork;

public class CategoriesFragment extends Fragment implements CategoryAdapter.OnImageClickListener {
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private ProgressBar progressBar;
    private MenuItem deleteMenuItem;
    private final List<GalleryImage> categoryImages = new ArrayList<>();
    private String currentCategory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progress_bar);
        setHasOptionsMenu(true);

        currentCategory = getArguments() != null ? getArguments().getString("category") : "Unknown";

        setupRecyclerView();
        loadCategoryImagesFromServer();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.gallery_menu, menu);
        deleteMenuItem = menu.findItem(R.id.action_delete);
        deleteMenuItem.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setupRecyclerView() {
        adapter = new CategoryAdapter(requireContext(), currentCategory);
        adapter.setOnImageClickListener(this);
        int spanCount = getResources().getConfiguration().screenWidthDp / 180;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), Math.max(2, spanCount)));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onImageClick(GalleryImage image) {
        if (!categoryImages.isEmpty()) {
            int position = categoryImages.indexOf(image);
            if (position != -1) {
                ImageViewerFragment viewerFragment = ImageViewerFragment.newInstance(
                        new ArrayList<>(categoryImages), position);
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
        updateDeleteMenu();
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
                    categoryImages.clear();
                    for (String url : imageUrls) {
                        if (url != null && !url.isEmpty()) {
                            String filename = extractFilename(url);
                            categoryImages.add(new GalleryImage(
                                    filename, filename, "", url, currentCategory));
                        }
                    }
                    adapter.setImages(categoryImages);
                    progressBar.setVisibility(View.GONE);
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Error loading images: " + message, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
            }
        });
    }

    private void updateDeleteMenu() {
        if (deleteMenuItem != null) {
            deleteMenuItem.setVisible(adapter.getSelectedImages().size() > 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            showDeleteConfirmationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        String[] filenames = selectedImages.stream().map(GalleryImage::getFilename).toArray(String[]::new);

        FlaskNetwork flaskNetwork = new FlaskNetwork();
        flaskNetwork.deleteImages(filenames, new FlaskNetwork.OnDeleteImagesListener() {
            @Override
            public void onSuccess(String[] deletedFiles, String[] failedFiles) {
                requireActivity().runOnUiThread(() -> {
                    adapter.setMultiSelectMode(false);
                    loadCategoryImagesFromServer();
                    Toast.makeText(requireContext(), "Deleted " + deletedFiles.length + " images", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String message) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Error deleting images: " + message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private String extractFilename(String url) {
        if (url == null || url.isEmpty()) {
            return "unknown_" + System.currentTimeMillis();
        }
        int lastSlashIndex = url.lastIndexOf('/');
        return (lastSlashIndex != -1 && lastSlashIndex < url.length() - 1)
                ? url.substring(lastSlashIndex + 1)
                : "unknown_" + System.currentTimeMillis();
    }
}
