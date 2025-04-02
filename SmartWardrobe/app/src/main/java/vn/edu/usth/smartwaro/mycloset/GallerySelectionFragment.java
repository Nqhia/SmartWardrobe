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

public class GallerySelectionFragment extends Fragment implements GalleryAdapter.OnImageClickListener {
    public static final String ARG_TYPE = "type";
    public static final String REQUEST_KEY_SELECTION = "gallery_selection";
    public static final String BUNDLE_KEY_FILENAME = "selected_filename";

    private RecyclerView recyclerView;
    private GalleryAdapter adapter;
    private ProgressBar progressBar;
    private String type;
    private List<GalleryImage> allImages = new ArrayList<>();
    private List<GalleryImage> filteredImages = new ArrayList<>();
    private FlaskNetwork flaskNetwork;
    private static final String TAG = "GallerySelectionFrag";

    public static GallerySelectionFragment newInstance(String type) {
        GallerySelectionFragment fragment = new GallerySelectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerViewGallerySelection);
        progressBar = view.findViewById(R.id.progressBarGallerySelection);

        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE, "shirt");
        }
        flaskNetwork = new FlaskNetwork();
        setupRecyclerView();
        loadImages();

        Toast.makeText(getContext(), "GallerySelectionFragment (" + type + ") opened", Toast.LENGTH_SHORT).show();
    }

    private void setupRecyclerView() {
        adapter = new GalleryAdapter(requireContext());
        adapter.setOnImageClickListener(this);
        int spanCount = getResources().getConfiguration().screenWidthDp / 180;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), Math.max(2, spanCount)));
        recyclerView.setAdapter(adapter);
    }

    private void loadImages() {
        progressBar.setVisibility(View.VISIBLE);
        flaskNetwork.getUserImages(null, new FlaskNetwork.OnImagesLoadedListener() {
            @Override
            public void onSuccess(String[] imageUrls) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    try {
                        allImages.clear();
                        for (String url : imageUrls) {
                            if (url != null && !url.isEmpty()) {
                                String filename = extractFilename(url);
                                String dummyCategory = "uncategorized";
                                if (filename.toLowerCase().contains("shirt")) {
                                    dummyCategory = "long sleeves";
                                } else if (filename.toLowerCase().contains("pant")) {
                                    dummyCategory = "long leggings";
                                }
                                allImages.add(new GalleryImage(filename, filename, "", url, dummyCategory));
                            }
                        }
                        filteredImages.clear();
                        for (GalleryImage img : allImages) {
                            String cat = img.getCategory().toLowerCase();
                            if (type.equals("shirt") && cat.contains("sleeve")) {
                                filteredImages.add(img);
                            } else if (type.equals("pant") && (cat.contains("legging") || cat.contains("pant") || cat.contains("trouser") || cat.contains("short"))) {
                                filteredImages.add(img);
                            }
                        }
                        if (filteredImages.isEmpty()) {
                            filteredImages.addAll(allImages);
                        }
                        adapter.setImages(filteredImages);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error processing images", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error processing images", e);
                    } finally {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error loading images: " + message, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
            }
        });
    }

    private String extractFilename(String url) {
        if (url == null || url.isEmpty()) return "unknown_" + System.currentTimeMillis();
        int lastSlash = url.lastIndexOf('/');
        if (lastSlash != -1 && lastSlash < url.length() - 1) {
            return url.substring(lastSlash + 1);
        }
        return "unknown_" + System.currentTimeMillis();
    }

    @Override
    public void onImageClick(GalleryImage image) {
        String resultKey = type.equals("shirt") ? "shirt_selection" : "pant_selection";
        Bundle result = new Bundle();
        result.putParcelable(BUNDLE_KEY_FILENAME, image);
        getParentFragmentManager().setFragmentResult(resultKey, result);
        getParentFragmentManager().popBackStack();
    }



    @Override
    public void onSelectionChanged(int selectedCount) {
    }
}
