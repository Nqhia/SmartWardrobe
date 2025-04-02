package vn.edu.usth.smartwaro.wardrobe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.network.FlaskNetwork;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<FlaskNetwork.Category> categoryList;
    private OnImageClickListener onImageClickListener;

    public interface OnImageClickListener {
        void onImageClick(String category, String imageUrl);
    }

    public CategoryAdapter(List<FlaskNetwork.Category> categoryList, OnImageClickListener listener) {
        this.categoryList = categoryList;
        this.onImageClickListener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_wadrobe, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        FlaskNetwork.Category category = categoryList.get(position);
        holder.categoryNameTextView.setText(category.name);

        // Thiết lập RecyclerView con để hiển thị danh sách hình ảnh
        ImageAdapter imageAdapter = new ImageAdapter(category.images, imageUrl -> {
            // Khi người dùng click vào một hình ảnh
            onImageClickListener.onImageClick(category.name, imageUrl);
        });
        holder.imageRecyclerView.setLayoutManager(new LinearLayoutManager(
                holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        holder.imageRecyclerView.setAdapter(imageAdapter);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTextView;
        RecyclerView imageRecyclerView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.categoryNameTextView);
            imageRecyclerView = itemView.findViewById(R.id.imageRecyclerView);
        }
    }
}