package vn.edu.usth.smartwaro.mycloset;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.mycloset.GalleryImage;
import vn.edu.usth.smartwaro.mycloset.GallerySelectionFragment;
import vn.edu.usth.smartwaro.network.FlaskNetwork;

public class FavoriteSetCreationFragment extends Fragment {

    private EditText editSetName; // EditText mới để nhập tên set đồ
    private RecyclerView recyclerViewShirts, recyclerViewPants;
    private Button btnAddShirt, btnAddPant, btnSaveSet;
    private FavoriteSetItemAdapter shirtAdapter, pantAdapter;
    private List<GalleryImage> selectedShirtImages = new ArrayList<>();
    private List<GalleryImage> selectedPantImages = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_set_creation, container, false);

        editSetName = view.findViewById(R.id.editSetName);
        recyclerViewShirts = view.findViewById(R.id.recyclerViewShirts);
        recyclerViewPants = view.findViewById(R.id.recyclerViewPants);
        btnAddShirt = view.findViewById(R.id.btnAddShirt);
        btnAddPant = view.findViewById(R.id.btnAddPant);
        btnSaveSet = view.findViewById(R.id.btnSaveSet);

        shirtAdapter = new FavoriteSetItemAdapter();
        pantAdapter = new FavoriteSetItemAdapter();

        LinearLayoutManager shirtLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewShirts.setLayoutManager(shirtLayoutManager);
        recyclerViewShirts.setAdapter(shirtAdapter);

        LinearSnapHelper shirtSnapHelper = new LinearSnapHelper();
        shirtSnapHelper.attachToRecyclerView(recyclerViewShirts);

        LinearLayoutManager pantLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPants.setLayoutManager(pantLayoutManager);
        recyclerViewPants.setAdapter(pantAdapter);
        LinearSnapHelper pantSnapHelper = new LinearSnapHelper();
        pantSnapHelper.attachToRecyclerView(recyclerViewPants);

        shirtAdapter.setOnItemRemoveListener((image, position) -> {
            selectedShirtImages.remove(position);
            shirtAdapter.setItems(new ArrayList<>(selectedShirtImages));
            Toast.makeText(getContext(), "Deleted Image", Toast.LENGTH_SHORT).show();
        });
        pantAdapter.setOnItemRemoveListener((image, position) -> {
            selectedPantImages.remove(position);
            pantAdapter.setItems(new ArrayList<>(selectedPantImages));
            Toast.makeText(getContext(), "Deleted Image", Toast.LENGTH_SHORT).show();
        });

        getParentFragmentManager().setFragmentResultListener("shirt_selection", this, (requestKey, bundle) -> {
            GalleryImage selectedImage = bundle.getParcelable(GallerySelectionFragment.BUNDLE_KEY_FILENAME);
            if (selectedImage != null) {
                selectedShirtImages.add(selectedImage);
                shirtAdapter.setItems(new ArrayList<>(selectedShirtImages));
            }
        });
        getParentFragmentManager().setFragmentResultListener("pant_selection", this, (requestKey, bundle) -> {
            GalleryImage selectedImage = bundle.getParcelable(GallerySelectionFragment.BUNDLE_KEY_FILENAME);
            if (selectedImage != null) {
                selectedPantImages.add(selectedImage);
                pantAdapter.setItems(new ArrayList<>(selectedPantImages));
            }
        });

        btnAddShirt.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, GallerySelectionFragment.newInstance("shirt"))
                    .addToBackStack(null)
                    .commit();
        });

        btnAddPant.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, GallerySelectionFragment.newInstance("pant"))
                    .addToBackStack(null)
                    .commit();
        });

        btnSaveSet.setOnClickListener(v -> {
            String setName = editSetName.getText().toString().trim();
            if (setName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a name for the set", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedShirtImages.isEmpty() || selectedPantImages.isEmpty()) {
                Toast.makeText(getContext(), "You should select at least one shirt and one pant", Toast.LENGTH_SHORT).show();
                return;
            }
            List<String> shirtFilenames = new ArrayList<>();
            for (GalleryImage img : selectedShirtImages) {
                shirtFilenames.add(img.getFilename());
            }
            List<String> pantFilenames = new ArrayList<>();
            for (GalleryImage img : selectedPantImages) {
                pantFilenames.add(img.getFilename());
            }
            new FlaskNetwork().createFavoriteSet(setName, shirtFilenames, pantFilenames, new FlaskNetwork.OnFavoriteSetSaveListener() {
                @Override
                public void onSuccess(String message) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Favorite set created successfully: " + message, Toast.LENGTH_SHORT).show()
                    );
                }
                @Override
                public void onError(String message) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Error creating favorite set: " + message, Toast.LENGTH_SHORT).show()
                    );
                }
            });
        });


        return view;
    }
}
