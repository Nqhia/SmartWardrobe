package vn.edu.usth.smartwaro.mycloset;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.network.FlaskNetwork;


public class FavoriteFragment extends Fragment implements GalleryAdapter.OnImageClickListener {
    private static final String TAG = "FavoriteFragment";

    private RecyclerView recyclerView;
    private GalleryAdapter adapter;
    private ProgressBar progressBar;
    private FlaskNetwork flaskNetwork;
    private List<GalleryImage> allImages = new ArrayList<>();
    private List<GalleryImage> favouriteImages = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        recyclerView = view.findViewById(R.id.favouriteRecyclerView);
        progressBar = new ProgressBar(requireContext());

        flaskNetwork = new FlaskNetwork();
        setupRecyclerView();
        loadAllImages();

        return view;
    }

    private void setupRecyclerView() {
        adapter = new GalleryAdapter(requireContext());
        adapter.setOnImageClickListener(this);

        int spanCount = getResources().getConfiguration().screenWidthDp / 180;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), Math.max(2, spanCount)));
        recyclerView.setAdapter(adapter);
    }

    private void loadAllImages() {
        flaskNetwork.getUserImages(null, new FlaskNetwork.OnImagesLoadedListener() {
            @Override
            public void onSuccess(String[] imageUrls) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    try {
                        allImages.clear();
                        for (String url : imageUrls) {
                        }
                        favouriteImages.clear();
                        for (GalleryImage img : allImages) {
                            if (img.isFavorite()) {
                                favouriteImages.add(img);
                            }
                        }
                        adapter.setImages(favouriteImages);
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing images", e);
                        Toast.makeText(requireContext(), "Error processing images", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    Log.e(TAG, "Error loading images: " + message);
                    Toast.makeText(requireContext(), "Error loading images: " + message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onImageClick(GalleryImage image) {
        Toast.makeText(requireContext(), "Clicked: " + image.getFilename(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSelectionChanged(int selectedCount) {
    }

}