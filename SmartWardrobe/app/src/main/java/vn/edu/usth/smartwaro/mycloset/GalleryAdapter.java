package vn.edu.usth.smartwaro.mycloset;

import android.content.Context;
import android.util.Log;
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

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ImageViewHolder> {
    private static final String TAG = "GalleryAdapter";
    private final Context context;
    private List<GalleryImage> galleryImages;
    private OnImageClickListener listener;

    public interface OnImageClickListener {
        void onImageClick(GalleryImage image);
    }

    public GalleryAdapter(Context context) {
        this.context = context;
        this.galleryImages = new ArrayList<>();
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        this.listener = listener;
    }

    public void setImages(List<GalleryImage> images) {
        if (images == null) {
            Log.w(TAG, "setImages: Received null image list");
            this.galleryImages = new ArrayList<>();
        } else {
            this.galleryImages = images;
        }
        Log.d(TAG, "setImages: Loaded " + galleryImages.size() + " images.");
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
        if (position >= galleryImages.size()) {
            Log.w(TAG, "onBindViewHolder: Invalid position " + position);
            return;
        }

        GalleryImage image = galleryImages.get(position);

        // Build full URL for the image using FlaskNetwork
        String fullUrl = FlaskNetwork.BASE_URL + (image.getUrl().startsWith("/") ? image.getUrl() : "/" + image.getUrl());
        Log.d(TAG, "onBindViewHolder: Loading image from URL: " + fullUrl);

        // Use Glide to load image
        Glide.with(context)
                .load(fullUrl)
                .centerCrop()
                .placeholder(R.drawable.placeholder_image) // Placeholder image when loading
                .error(R.drawable.ic_launcher_foreground) // Error image if load fails
                .into(holder.imageView);

        // Set click listener for image
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onImageClick(image);
            }
        });
    }

    @Override
    public int getItemCount() {
        return galleryImages != null ? galleryImages.size() : 0;
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.gallery_image);
        }
    }
}
