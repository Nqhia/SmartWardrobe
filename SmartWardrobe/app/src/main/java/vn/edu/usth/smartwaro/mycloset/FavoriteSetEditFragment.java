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
import vn.edu.usth.smartwaro.network.FlaskNetwork;

public class FavoriteSetEditFragment extends Fragment {

    private EditText editSetName;
    private RecyclerView recyclerViewShirts, recyclerViewPants;
    private Button btnAddShirt, btnAddPant, btnUpdateSet; // đổi tên btnSaveSet thành btnUpdateSet nếu cần
    private FavoriteSetItemAdapter shirtAdapter, pantAdapter;
    private ArrayList<GalleryImage> shirtImages;
    private ArrayList<GalleryImage> pantImages;
    private String setName;
    private String setId; // Biến lưu setId

    // Factory method, bây giờ cũng nhận setId
    public static FavoriteSetEditFragment newInstance(String setId, String setName,
                                                      ArrayList<GalleryImage> shirtImages,
                                                      ArrayList<GalleryImage> pantImages) {
        FavoriteSetEditFragment fragment = new FavoriteSetEditFragment();
        Bundle args = new Bundle();
        args.putString("set_id", setId);  // Thêm setId vào arguments
        args.putString("set_name", setName);
        args.putParcelableArrayList("shirt_images", shirtImages);
        args.putParcelableArrayList("pant_images", pantImages);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite_set_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Ánh xạ view
        editSetName = view.findViewById(R.id.editSetName);
        recyclerViewShirts = view.findViewById(R.id.recyclerViewShirts);
        recyclerViewPants = view.findViewById(R.id.recyclerViewPants);
        btnAddShirt = view.findViewById(R.id.btnAddShirt);
        btnAddPant = view.findViewById(R.id.btnAddPant);
        btnUpdateSet = view.findViewById(R.id.btnSaveSet); // Giả sử id của nút cập nhật vẫn là btnSaveSet

        // Lấy dữ liệu từ arguments
        if (getArguments() != null) {
            setId = getArguments().getString("set_id"); // Lấy setId từ arguments
            setName = getArguments().getString("set_name");
            shirtImages = getArguments().getParcelableArrayList("shirt_images");
            pantImages = getArguments().getParcelableArrayList("pant_images");
        }
        if (setName == null) {
            setName = "";
        }
        editSetName.setText(setName);

        // Cài đặt RecyclerView cho áo
        recyclerViewShirts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        shirtAdapter = new FavoriteSetItemAdapter();
        recyclerViewShirts.setAdapter(shirtAdapter);
        new LinearSnapHelper().attachToRecyclerView(recyclerViewShirts);

        // Cài đặt RecyclerView cho quần
        recyclerViewPants.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        pantAdapter = new FavoriteSetItemAdapter();
        recyclerViewPants.setAdapter(pantAdapter);
        new LinearSnapHelper().attachToRecyclerView(recyclerViewPants);

        // Nếu danh sách null thì khởi tạo
        if (shirtImages == null) {
            shirtImages = new ArrayList<>();
        }
        if (pantImages == null) {
            pantImages = new ArrayList<>();
        }
        // Đổ dữ liệu vào adapter
        shirtAdapter.setItems(shirtImages);
        pantAdapter.setItems(pantImages);

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

        // Lắng nghe kết quả chọn ảnh từ GallerySelectionFragment cho áo
        getParentFragmentManager().setFragmentResultListener("shirt_selection", this, (requestKey, bundle) -> {
            GalleryImage selectedImage = bundle.getParcelable(GallerySelectionFragment.BUNDLE_KEY_FILENAME);
            if (selectedImage != null) {
                // Kiểm tra xem ảnh đã có trong danh sách chưa (dựa vào filename)
                boolean exists = false;
                for (GalleryImage img : shirtImages) {
                    if (img.getFilename().equals(selectedImage.getFilename())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    shirtImages.add(selectedImage);
                    shirtAdapter.setItems(new ArrayList<>(shirtImages));
                }
            }
        });

        // Lắng nghe kết quả chọn ảnh từ GallerySelectionFragment cho quần
        getParentFragmentManager().setFragmentResultListener("pant_selection", this, (requestKey, bundle) -> {
            GalleryImage selectedImage = bundle.getParcelable(GallerySelectionFragment.BUNDLE_KEY_FILENAME);
            if (selectedImage != null) {
                // Kiểm tra xem ảnh đã có trong danh sách chưa (dựa vào filename)
                boolean exists = false;
                for (GalleryImage img : pantImages) {
                    if (img.getFilename().equals(selectedImage.getFilename())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    pantImages.add(selectedImage);
                    pantAdapter.setItems(new ArrayList<>(pantImages));
                }
            }
        });

        // Xử lý nút cập nhật set đồ
        btnUpdateSet.setOnClickListener(v -> {
            String newSetName = editSetName.getText().toString().trim();
            if (newSetName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a name for the set", Toast.LENGTH_SHORT).show();
                return;
            }
            if (shirtImages.isEmpty() || pantImages.isEmpty()) {
                Toast.makeText(getContext(), "You should select at least one shirt and one pant", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra setId không được null
            if (setId == null || setId.isEmpty()) {
                Toast.makeText(getContext(), "Error: Invalid ID", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> shirtFilenames = new ArrayList<>();
            for (GalleryImage img : shirtImages) {
                shirtFilenames.add(img.getFilename());
            }
            List<String> pantFilenames = new ArrayList<>();
            for (GalleryImage img : pantImages) {
                pantFilenames.add(img.getFilename());
            }

            new FlaskNetwork().editFavoriteSet(setId, newSetName, shirtFilenames, pantFilenames, new FlaskNetwork.OnFavoriteSetSaveListener() {
                @Override
                public void onSuccess(String message) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Favorite set updated successfully: " + message, Toast.LENGTH_SHORT).show();
                            // Quay lại màn hình danh sách
                            getActivity().getSupportFragmentManager().popBackStack();
                        });
                    }
                }
                @Override
                public void onError(String message) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Error updating favorite set: " + message, Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        });

        // Xử lý sự kiện xóa ảnh nếu cần
        shirtAdapter.setOnItemRemoveListener((image, position) -> {
            shirtImages.remove(position);
            shirtAdapter.setItems(new ArrayList<>(shirtImages));
            Toast.makeText(getContext(), "Deleted Image", Toast.LENGTH_SHORT).show();
        });
        pantAdapter.setOnItemRemoveListener((image, position) -> {
            pantImages.remove(position);
            pantAdapter.setItems(new ArrayList<>(pantImages));
            Toast.makeText(getContext(), "Deleted Image", Toast.LENGTH_SHORT).show();
        });
    }
}
