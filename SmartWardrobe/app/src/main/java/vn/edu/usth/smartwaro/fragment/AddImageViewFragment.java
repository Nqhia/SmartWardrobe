package vn.edu.usth.smartwaro.fragment;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.mycloset.CategoryAdapter;
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
    private static final String DEFAULT_CATEGORY = "uncategorized";
    private String category = DEFAULT_CATEGORY;
    private ImageButton btnCategoryMenu;
    private List<String> categories = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flaskNetwork = new FlaskNetwork();
        categories = new ArrayList<>();
    }
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

        btnCategoryMenu = rootView.findViewById(R.id.categoryButton);
        loadCategories();
        btnCategoryMenu.setOnClickListener(v -> showCategoryDialog());


        closetViewModel = new ViewModelProvider(requireActivity()).get(ClosetViewModel.class);
        // Load image URI passed as argument
        if (getArguments() != null) {
            imageUri = getArguments().getParcelable(ARG_IMAGE_URI);
            imgCaptured.setImageURI(imageUri);
        }

        // Handle OK button click
        btnOk.setOnClickListener(v -> {
            if (imageUri != null) {
                progressBar.setVisibility(View.VISIBLE);

                // Updated to use category parameter
                flaskNetwork.saveImageToServer(
                        requireContext(),
                        imageUri,
                        category, // Pass the category
                        new FlaskNetwork.OnImageSaveListener() {
                            @Override
                            public void onProcessing() {
                                requireActivity().runOnUiThread(() -> {
                                    progressBar.setVisibility(View.VISIBLE);
                                    Toast.makeText(getContext(), "Processing image...", Toast.LENGTH_SHORT).show();
                                });
                            }

                            @Override
                            public void onSuccess(String message) {
                                requireActivity().runOnUiThread(() -> {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                                    // Create ClothingItem with category
                                    ClothingItem newClothingItem = new ClothingItem(
                                            "Uploaded Item",
                                            imageUri,
                                            category // Add category to ClothingItem
                                    );
                                    closetViewModel.addItem(newClothingItem);

                                    // Navigate back
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
                                    Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_LONG).show();
                                });
                            }
                        });
            } else {
                Toast.makeText(getContext(), "No image to upload", Toast.LENGTH_SHORT).show();
            }
        });
        btnCancel.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new MyClosetFragment())
                    .commit();
        });

        return rootView;
    }
    private void loadCategories() {
        flaskNetwork.getCategories(new FlaskNetwork.OnCategoriesLoadedListener() {
            @Override
            public void onSuccess(List<String> loadedCategories) {
                requireActivity().runOnUiThread(() -> {
                    categories = loadedCategories;
                });
            }

            @Override
            public void onError(String message) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    private void showCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_category_selection, null);

        RecyclerView recyclerView = dialogView.findViewById(R.id.categoryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        CategoryAdapter adapter = new CategoryAdapter(
                new ArrayList<>(categories),
                category,
                selectedCategory -> {
                    category = selectedCategory;
                    Toast.makeText(getContext(),
                            "Selected category: " + category,
                            Toast.LENGTH_SHORT).show();
                }
        );
        recyclerView.setAdapter(adapter);

        Button btnAddNew = dialogView.findViewById(R.id.btnAddNewCategory);
        btnAddNew.setOnClickListener(v -> showAddCategoryDialog());

        builder.setView(dialogView);
        builder.setTitle("Select Category");
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        final EditText input = new EditText(requireContext());
        input.setHint("Enter new category name");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String newCategory = input.getText().toString().trim();
            if (!newCategory.isEmpty()) {
                flaskNetwork.addCategory(newCategory, new FlaskNetwork.OnCategoryOperationListener() {
                    @Override
                    public void onSuccess(String message) {
                        requireActivity().runOnUiThread(() -> {
                            categories.add(newCategory);
                            category = newCategory;
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onError(String message) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}