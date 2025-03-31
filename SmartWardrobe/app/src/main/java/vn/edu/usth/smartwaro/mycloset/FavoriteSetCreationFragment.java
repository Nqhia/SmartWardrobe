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
    // Danh sách lưu ảnh đã chọn
    private List<GalleryImage> selectedShirtImages = new ArrayList<>();
    private List<GalleryImage> selectedPantImages = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_set_creation, container, false);

        // Ánh xạ view từ layout
        editSetName = view.findViewById(R.id.editSetName);
        recyclerViewShirts = view.findViewById(R.id.recyclerViewShirts);
        recyclerViewPants = view.findViewById(R.id.recyclerViewPants);
        btnAddShirt = view.findViewById(R.id.btnAddShirt);
        btnAddPant = view.findViewById(R.id.btnAddPant);
        btnSaveSet = view.findViewById(R.id.btnSaveSet);

        shirtAdapter = new FavoriteSetItemAdapter();
        pantAdapter = new FavoriteSetItemAdapter();


        // Thiết lập RecyclerView cho áo với LinearLayoutManager ngang
        LinearLayoutManager shirtLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewShirts.setLayoutManager(shirtLayoutManager);
        recyclerViewShirts.setAdapter(shirtAdapter);
        // Attach SnapHelper để tạo hiệu ứng slide
        LinearSnapHelper shirtSnapHelper = new LinearSnapHelper();
        shirtSnapHelper.attachToRecyclerView(recyclerViewShirts);

        // Thiết lập RecyclerView cho quần với LinearLayoutManager ngang
        LinearLayoutManager pantLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPants.setLayoutManager(pantLayoutManager);
        recyclerViewPants.setAdapter(pantAdapter);
        // Attach SnapHelper để tạo hiệu ứng slide
        LinearSnapHelper pantSnapHelper = new LinearSnapHelper();
        pantSnapHelper.attachToRecyclerView(recyclerViewPants);

        // Xử lý sự kiện xóa ảnh cho áo
        shirtAdapter.setOnItemRemoveListener((image, position) -> {
            selectedShirtImages.remove(position);
            shirtAdapter.setItems(new ArrayList<>(selectedShirtImages));
            Toast.makeText(getContext(), "Đã xóa ảnh", Toast.LENGTH_SHORT).show();
        });
        // Xử lý sự kiện xóa ảnh cho quần
        pantAdapter.setOnItemRemoveListener((image, position) -> {
            selectedPantImages.remove(position);
            pantAdapter.setItems(new ArrayList<>(selectedPantImages));
            Toast.makeText(getContext(), "Đã xóa ảnh", Toast.LENGTH_SHORT).show();
        });

        // Lắng nghe kết quả chọn ảnh từ GallerySelectionFragment cho áo
        getParentFragmentManager().setFragmentResultListener("shirt_selection", this, (requestKey, bundle) -> {
            GalleryImage selectedImage = bundle.getParcelable(GallerySelectionFragment.BUNDLE_KEY_FILENAME);
            if (selectedImage != null) {
                selectedShirtImages.add(selectedImage);
                shirtAdapter.setItems(new ArrayList<>(selectedShirtImages));
            }
        });
        // Lắng nghe kết quả chọn ảnh từ GallerySelectionFragment cho quần
        getParentFragmentManager().setFragmentResultListener("pant_selection", this, (requestKey, bundle) -> {
            GalleryImage selectedImage = bundle.getParcelable(GallerySelectionFragment.BUNDLE_KEY_FILENAME);
            if (selectedImage != null) {
                selectedPantImages.add(selectedImage);
                pantAdapter.setItems(new ArrayList<>(selectedPantImages));
            }
        });

        btnAddShirt.setOnClickListener(v -> {
            // Mở GallerySelectionFragment với tham số "shirt"
            getParentFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, GallerySelectionFragment.newInstance("shirt"))
                    .addToBackStack(null)
                    .commit();
        });

        btnAddPant.setOnClickListener(v -> {
            // Mở GallerySelectionFragment với tham số "pant"
            getParentFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, GallerySelectionFragment.newInstance("pant"))
                    .addToBackStack(null)
                    .commit();
        });

        btnSaveSet.setOnClickListener(v -> {
            String setName = editSetName.getText().toString().trim();
            if (setName.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập tên set đồ", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedShirtImages.isEmpty() || selectedPantImages.isEmpty()) {
                Toast.makeText(getContext(), "Bạn cần chọn ít nhất 1 áo và 1 quần", Toast.LENGTH_SHORT).show();
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
