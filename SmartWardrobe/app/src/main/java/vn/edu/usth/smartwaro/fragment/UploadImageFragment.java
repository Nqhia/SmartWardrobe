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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.io.IOException;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.mycloset.ClothingItem;
import vn.edu.usth.smartwaro.mycloset.ClosetViewModel;

public class UploadImageFragment extends Fragment {

    private Button btnPhoto;
    private Button btnOk;
    private Button btnGallery;
    private ImageView imgCaptured;
    private Uri imageUri;
    private ClosetViewModel closetViewModel;
    private String selectedCategory;  // Danh mục đã chọn

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
                    imgCaptured.setImageURI(imageUri);
                    btnOk.setVisibility(View.VISIBLE);
                    Log.d("UploadImageFragment", "Image captured with URI: " + imageUri);
                } else {
                    Toast.makeText(getContext(), "Failed to capture image", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<String> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    imageUri = uri;
                    imgCaptured.setImageURI(imageUri);
                    btnOk.setVisibility(View.VISIBLE);
                    Log.d("UploadImageFragment", "Image selected from gallery with URI: " + imageUri);
                } else {
                    Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_upload_image, container, false);

        closetViewModel = new ViewModelProvider(requireActivity()).get(ClosetViewModel.class);

        btnPhoto = rootView.findViewById(R.id.btn_photo);
        btnOk = rootView.findViewById(R.id.btnOk);
        btnGallery = rootView.findViewById(R.id.btn_gallery);
        imgCaptured = rootView.findViewById(R.id.imgCaptured);

        btnPhoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA);
            }
        });

        btnGallery.setOnClickListener(v -> {
            galleryActivityResultLauncher.launch("img/");
        });

        btnOk.setOnClickListener(v -> {
            ClothingItem newClothingItem = new ClothingItem("New Clothing Item", imageUri);
            if (selectedCategory != null) {
                switch (selectedCategory) {
                    case "upperBody":
                        closetViewModel.addUpperBodyItem(newClothingItem); // Thêm vào upperBody
                        break;
                    case "lowerBody":
                        closetViewModel.addLowerBodyItem(newClothingItem); // Thêm vào lowerBody
                        break;
                    case "footwear":
                        closetViewModel.addFootwearItem(newClothingItem); // Thêm vào footwear
                        break;
                    default:
                        break;
                }
            }
            getActivity().getSupportFragmentManager().popBackStack(); // Quay lại fragment trước
        });

        return rootView;
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

    // Thiết lập danh mục được chọn từ ClosetAdapter
    public void setSelectedCategory(String category) {
        this.selectedCategory = category;
    }
}
