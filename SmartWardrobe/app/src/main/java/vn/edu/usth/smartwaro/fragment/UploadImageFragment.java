// UploadImageFragment.java
package vn.edu.usth.smartwaro.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.network.FlaskNetwork;

public class UploadImageFragment extends Fragment {

    private Button btnPhoto;
    private Button btnOk;
    private Button btnGallery;
    private ImageView imgCaptured;
    private ProgressBar progressBar;
    private Uri imageUri;
    private String selectedCategory;
    private FlaskNetwork flaskNetwork;

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openCamera();
                } else {
                    Toast.makeText(getContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && imageUri != null) {
                    processImage(imageUri);
                } else {
                    Toast.makeText(getContext(), "Failed to capture image", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<String> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    imageUri = uri;
                    processImage(imageUri);
                } else {
                    Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_upload_image, container, false);

        flaskNetwork = new FlaskNetwork();

        btnPhoto = rootView.findViewById(R.id.btn_photo);
        btnOk = rootView.findViewById(R.id.btnOk);
        btnGallery = rootView.findViewById(R.id.btn_gallery);
        imgCaptured = rootView.findViewById(R.id.imgCaptured);
        progressBar = rootView.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);
        btnOk.setVisibility(View.GONE);

        btnPhoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA)
                    == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA);
            }
        });

        btnGallery.setOnClickListener(v -> {
            galleryActivityResultLauncher.launch("image/*");
        });

        return rootView;
    }

    private void processImage(Uri uri) {
        if (getContext() == null) return;

        // Show processing state
        progressBar.setVisibility(View.VISIBLE);
        btnOk.setVisibility(View.GONE);
        btnPhoto.setEnabled(false);
        btnGallery.setEnabled(false);

        flaskNetwork.removeBackground(
                getContext(),
                uri,
                new FlaskNetwork.OnBackgroundRemovalListener() {
                    @Override
                    public void onProcessing() {
                        requireActivity().runOnUiThread(() -> {
                            progressBar.setVisibility(View.VISIBLE);
                        });
                    }

                    @Override
                    public void onSuccess(File processedImage) {
                        requireActivity().runOnUiThread(() -> {
                            // Update image URI to processed image
                            imageUri = FileProvider.getUriForFile(
                                    getContext(),
                                    "vn.edu.usth.swaro.fileprovider",
                                    processedImage
                            );

                            // Update UI
                            imgCaptured.setImageURI(imageUri);
                            progressBar.setVisibility(View.GONE);
                            btnOk.setVisibility(View.VISIBLE);
                            btnPhoto.setEnabled(true);
                            btnGallery.setEnabled(true);

                            Log.d("UploadImageFragment", "Background removed successfully: " + imageUri);
                        });
                    }

                    @Override
                    public void onError(String message) {
                        requireActivity().runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            btnPhoto.setEnabled(true);
                            btnGallery.setEnabled(true);
                            Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
                        });
                    }
                }
        );
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = createImageFile();
        if (photoFile != null) {
            imageUri = FileProvider.getUriForFile(getContext(), "vn.edu.usth.swaro.fileprovider", photoFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraActivityResultLauncher.launch(cameraIntent);
        }
    }

    private File createImageFile() {
        String imageFileName = "IMG_" + System.currentTimeMillis();
        File storageDir = getContext().getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, imageFileName + ".png");

        try {
            if (image.createNewFile()) {
                return image;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setSelectedCategory(String category) {
        this.selectedCategory = category;
    }
}