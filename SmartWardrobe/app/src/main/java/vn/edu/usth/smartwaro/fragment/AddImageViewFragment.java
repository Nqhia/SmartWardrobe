package vn.edu.usth.smartwaro.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.mycloset.ClothingItem;
import vn.edu.usth.smartwaro.mycloset.ClosetViewModel;
import vn.edu.usth.smartwaro.network.FlaskNetwork;

public class AddImageViewFragment extends Fragment {

    private static final String ARG_IMAGE_URI = "image_uri";
    private ImageView imgCaptured;
    private Button btnOk, btnCancel;
    private ProgressBar progressBar;
    private Uri imageUri;
    private ClosetViewModel closetViewModel;
    private FlaskNetwork flaskNetwork;

    public static AddImageViewFragment newInstance(Uri imageUri) {
        AddImageViewFragment fragment = new AddImageViewFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_IMAGE_URI, imageUri);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image_view_add, container, false);

        // Initialize views
        imgCaptured = rootView.findViewById(R.id.imgCaptured);
        btnOk = rootView.findViewById(R.id.btnOk);
        btnCancel = rootView.findViewById(R.id.btnCancel);
        progressBar = rootView.findViewById(R.id.progressBar);

        // Initialize ViewModel and FlaskNetwork
        closetViewModel = new ViewModelProvider(requireActivity()).get(ClosetViewModel.class);
        flaskNetwork = new FlaskNetwork();

        // Load image URI passed as argument
        if (getArguments() != null) {
            imageUri = getArguments().getParcelable(ARG_IMAGE_URI);
            imgCaptured.setImageURI(imageUri);
        }

        // Handle OK button click
        btnOk.setOnClickListener(v -> {
            if (imageUri != null) {
                progressBar.setVisibility(View.VISIBLE);

                // Sử dụng FlaskNetwork để lưu ảnh lên server
                flaskNetwork.saveImageToServer(requireContext(), imageUri, new FlaskNetwork.OnImageSaveListener() {
                    @Override
                    public void onProcessing() {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Uploading image to server...", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onSuccess(String message) {
                        requireActivity().runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                            // Sau khi lưu thành công, thêm ảnh vào ClosetViewModel
                            ClothingItem newClothingItem = new ClothingItem("Uploaded Item", imageUri);
                            closetViewModel.addItem(newClothingItem);

                            // Quay lại MyClosetFragment
                            requireActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, new MyClosetFragment())
                                    .commit();
                        });
                    }

                    @Override
                    public void onError(String message) {
                        requireActivity().runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            } else {
                Toast.makeText(getContext(), "No image to upload", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle Cancel button click
        btnCancel.setOnClickListener(v -> {
            // Navigate back to MyClosetFragment without saving
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new MyClosetFragment())
                    .commit();
        });

        return rootView;
    }
}
