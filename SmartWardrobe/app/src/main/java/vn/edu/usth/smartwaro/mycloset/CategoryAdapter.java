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
    private boolean isMultiSelectMode;

    public interface OnImageClickListener {
        void onImageClick(GalleryImage image);
        void onSelectionChanged(int selectedCount);
    }
    public void setMultiSelectMode(boolean enabled) {
        isMultiSelectMode = enabled;
        for (GalleryImage image : categoryImages) {
            image.setSelected(false);
        }
        notifyDataSetChanged();
    }

    public List<GalleryImage> getSelectedImages() {
        List<GalleryImage> selectedImage = new ArrayList<>();
        for (GalleryImage image : categoryImages) {
            if (image.isSelected()) {
                selectedImage.add(image);
            }
        }
        return selectedImage;
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

        holder.itemView.setAlpha(image.isSelected() ? 0.5f : 1.0f);
        holder.checkOverlay.setVisibility(image.isSelected() ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            if (isMultiSelectMode) {
                image.setSelected(!image.isSelected());
                notifyItemChanged(position);
                if (listener != null) {
                    listener.onSelectionChanged(getSelectedImages().size());
                }
            } else if (listener != null) {
                listener.onImageClick(image);
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (!isMultiSelectMode) {
                isMultiSelectMode = true;
                image.setSelected(true);
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onSelectionChanged(1);
                }
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return categoryImages.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        View checkOverlay;
        ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.gallery_image);
            checkOverlay= itemView.findViewById(R.id.check_overlay);
        }
    }
}
