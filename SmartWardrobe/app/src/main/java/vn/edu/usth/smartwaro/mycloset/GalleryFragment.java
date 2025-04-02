package vn.edu.usth.smartwaro.mycloset;

import static vn.edu.usth.smartwaro.network.FlaskNetwork.BASE_URL;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.chat.ShareUsersFragment;
import vn.edu.usth.smartwaro.network.FlaskNetwork;
import vn.edu.usth.smartwaro.mycloset.GalleryImage;

public class GalleryFragment extends Fragment implements GalleryAdapter.OnImageClickListener {
    private static final String TAG = "GalleryFragment";

    private RecyclerView recyclerView;
    private GalleryAdapter adapter;
    private ProgressBar progressBar;
    private FlaskNetwork flaskNetwork;
    private MenuItem deleteMenuItem;
    private List<GalleryImage> galleryImages = new ArrayList<>();
    private String currentCategory = null;
    private MenuItem shareMenuItem;
    private MenuItem favoriteMenuItem;


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
    public void onSelectionChanged(int selectedCount) {
        if (deleteMenuItem != null) {
            deleteMenuItem.setVisible(selectedCount > 0);
        }
        if (shareMenuItem != null) {
            shareMenuItem.setVisible(selectedCount > 0);
        }
        if (favoriteMenuItem != null){
            favoriteMenuItem.setVisible(selectedCount > 0);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.gallery_menu, menu);
        deleteMenuItem = menu.findItem(R.id.action_delete);
        shareMenuItem = menu.findItem(R.id.action_share);

        if (deleteMenuItem != null) {
            deleteMenuItem.setVisible(false);
        }

        if (shareMenuItem != null) {
            shareMenuItem.setVisible(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }


    private void setupRecyclerView() {
        adapter = new GalleryAdapter(requireContext());
        adapter.setOnImageClickListener(this);
        int spanCount = getResources().getConfiguration().screenWidthDp / 180;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), Math.max(2, spanCount)));
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

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            showDeleteConfirmationDialog();
            return true;
        }else if(item.getItemId() == R.id.action_share){
            shareSelectedImage();
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

        flaskNetwork.getUserImages(currentCategory, new FlaskNetwork.OnImagesLoadedListener() {
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
                                        "",
                                        url,
                                        currentCategory
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


    private String bitmapToString(Bitmap bitmap) {
        // Resize the bitmap to a smaller resolution
        int newWidth = 512; // Adjust based on your needs
        int newHeight = (bitmap.getHeight() * newWidth) / bitmap.getWidth();
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos); // JPEG format & 75% quality
        byte[] imageBytes = baos.toByteArray();

        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private String formatImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }
        if (url.startsWith("http")) {
            return url;
        }
        return BASE_URL + url;
    }
    private void shareSelectedImage() {
        List<GalleryImage> selectedImages = adapter.getSelectedImages();

        if (selectedImages.isEmpty()) {
            Toast.makeText(requireContext(), "No image selected for sharing!", Toast.LENGTH_SHORT).show();
            return;
        }

        GalleryImage imageToShare = selectedImages.get(0);
        String imageUrl = formatImageUrl(imageToShare.getUrl());
        progressBar.setVisibility(View.VISIBLE);

        Log.d(TAG, "Attempting to load image for sharing: " + imageUrl);

        // Try forcing Glide to always load the fresh image
        Glide.with(requireActivity())  // Use requireActivity() instead of requireContext()
                .asBitmap()
                .load(imageUrl)
                .skipMemoryCache(true)  // Skip memory cache
                .error(R.drawable.ic_error_image)  // Error fallback image
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, Transition<? super Bitmap> transition) {
                        progressBar.setVisibility(View.GONE);

                        // Convert bitmap to Base64 string
                        String imageString = bitmapToString(bitmap);

                        // Debugging: Log Base64 string length
                        Log.d(TAG, "Encoded Image String Length: " + imageString.length());

                        // Create bundle and pass data
                        Bundle bundle = new Bundle();
                        bundle.putString("modelImage", imageString);
                        bundle.putString("imageFilename", imageToShare.getFilename());

                        // Initialize ShareUsersFragment
                        ShareUsersFragment shareUsersFragment = new ShareUsersFragment();
                        shareUsersFragment.setArguments(bundle);

                        // Perform fragment transaction
                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, shareUsersFragment)
                                .addToBackStack(null)
                                .commit();

                        adapter.setMultiSelectMode(false);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireContext(),
                                "Failed to load image for sharing. Please try again.",
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to load image for sharing: " + imageUrl);
                    }
                });
    }

}