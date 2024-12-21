package vn.edu.usth.smartwaro.mycloset;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.fragment.UploadImageFragment;
import vn.edu.usth.smartwaro.mycloset.ClothingItem;

public class ClosetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_TYPE_CLOTHING = 0;  // Loại item quần áo
    private static final int ITEM_TYPE_ADD_NEW = 1;  // Loại item thêm mới (Add New Button)

    private List<ClothingItem> clothingItems;
    private boolean showAddButton;
    private FragmentActivity fragmentActivity;
    private String selectedCategory;  // Lưu thông tin danh mục (upperBody, lowerBody, footwear)

    public ClosetAdapter(List<ClothingItem> clothingItems, boolean showAddButton, FragmentActivity fragmentActivity, String selectedCategory) {
        this.clothingItems = clothingItems != null ? clothingItems : new ArrayList<>();  // Ensure non-null list
        this.showAddButton = showAddButton;
        this.fragmentActivity = fragmentActivity;
        this.selectedCategory = selectedCategory;  // Lưu thông tin danh mục đã chọn
    }

    @Override
    public int getItemViewType(int position) {
        if (showAddButton && position == clothingItems.size()) {
            return ITEM_TYPE_ADD_NEW;  // Hiển thị item thêm mới (Add New Button)
        } else {
            return ITEM_TYPE_CLOTHING;  // Hiển thị item quần áo bình thường
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CLOTHING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_holder, parent, false);
            return new ClothingViewHolder(view);
        } else if (viewType == ITEM_TYPE_ADD_NEW) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upload_item, parent, false);
            return new AddNewItemViewHolder(view);
        }
        return null; // Trả về null nếu không nhận diện được kiểu viewType
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ClothingViewHolder) {
            ClothingItem clothingItem = clothingItems.get(position);
            ((ClothingViewHolder) holder).bind(clothingItem); // Gắn dữ liệu vào ViewHolder
        } else if (holder instanceof AddNewItemViewHolder) {
            // Nếu là item "Add New", bạn có thể xử lý gì đó ở đây nếu cần
        }
    }

    @Override
    public int getItemCount() {
        return showAddButton ? clothingItems.size() + 1 : clothingItems.size();  // Nếu muốn thêm item "Add New" ở cuối
    }

    // ViewHolder cho item quần áo
    public static class ClothingViewHolder extends RecyclerView.ViewHolder {
        private ImageView clothingImage;

        public ClothingViewHolder(View itemView) {
            super(itemView);
            clothingImage = itemView.findViewById(R.id.imgItem);
        }

        public void bind(ClothingItem clothingItem) {
            if (clothingItem.hasImageUri()) {
                clothingImage.setImageURI(clothingItem.getImageUri());
            } else if (clothingItem.hasImageRes()) {
                clothingImage.setImageResource(clothingItem.getImageRes());
            }
        }
    }

    // ViewHolder cho item thêm mới (Add New Item Button)
    public class AddNewItemViewHolder extends RecyclerView.ViewHolder {
        public AddNewItemViewHolder(View itemView) {
            super(itemView);
            Button addNewItemButton = itemView.findViewById(R.id.upload_new_item_button);
            addNewItemButton.setOnClickListener(v -> navigateToUploadImageFragment());
        }

        private void navigateToUploadImageFragment() {
            UploadImageFragment uploadImageFragment = new UploadImageFragment();
            uploadImageFragment.setSelectedCategory(selectedCategory);  // Gửi danh mục vào fragment UploadImageFragment
            FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            transaction.replace(R.id.fragment_container, uploadImageFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    // Phương thức để cập nhật dữ liệu và notify
    public void updateData(List<ClothingItem> newItems) {
        this.clothingItems.clear();  // Xóa toàn bộ dữ liệu cũ
        this.clothingItems.addAll(newItems);  // Thêm danh sách mới
        notifyDataSetChanged();  // Thông báo RecyclerView để cập nhật UI
    }

    // Phương thức để thêm ảnh vào danh sách và cập nhật RecyclerView
    public void addNewImage(ClothingItem clothingItem) {
        clothingItems.add(clothingItem);
        int indexToInsert = clothingItems.size() - 1;  // Thêm vào vị trí cuối
        notifyItemInserted(indexToInsert);  // Cập nhật RecyclerView
    }
}
