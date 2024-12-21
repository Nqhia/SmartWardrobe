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

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ImageViewHolder> {
    private Context context;
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
        this.galleryImages = images;
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
        GalleryImage image = galleryImages.get(position);

        // Assuming the URL from your Flask server needs to be prefixed
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
        return galleryImages.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.gallery_image);
        }
    }
}