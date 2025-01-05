package vn.edu.usth.smartwaro.mycloset;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.network.FlaskNetwork;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ImageViewHolder> {
    private Context context;
    private List<GalleryImage> categoryImages;
    private OnImageClickListener listener;
    private String currentCategory;

    public interface OnImageClickListener {
        void onImageClick(GalleryImage image);
    }

    public CategoryAdapter(Context context, String category) {
        this.context = context;
        this.categoryImages = new ArrayList<>();
        this.currentCategory = category;
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        this.listener = listener;
    }

    public void setImages(List<GalleryImage> images) {
        this.categoryImages = images;
        notifyDataSetChanged();
    }

    public void filterByCategory(List<GalleryImage> allImages, String category) {
        this.currentCategory = category;
        this.categoryImages.clear();
        for (GalleryImage image : allImages) {
            if (image.getCategory().equals(category)) {
                this.categoryImages.add(image);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gallery_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        GalleryImage image = categoryImages.get(position);

        String fullUrl = FlaskNetwork.BASE_URL + (image.getUrl().startsWith("/") ? image.getUrl() : "/" + image.getUrl());

        Glide.with(context)
                .load(fullUrl)
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onImageClick(image);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryImages.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.gallery_image);
        }
    }


}
