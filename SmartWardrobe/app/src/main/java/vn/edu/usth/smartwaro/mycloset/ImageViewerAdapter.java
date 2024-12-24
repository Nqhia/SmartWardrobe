package vn.edu.usth.smartwaro.mycloset;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.List;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.network.FlaskNetwork;

public class ImageViewerAdapter extends RecyclerView.Adapter<ImageViewerAdapter.ViewerViewHolder> {
    private Context context;
    private List<GalleryImage> images;

    public ImageViewerAdapter(Context context, List<GalleryImage> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_view, parent, false);
        return new ViewerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewerViewHolder holder, int position) {
        GalleryImage image = images.get(position);
        String fullUrl = FlaskNetwork.BASE_URL + (image.getUrl().startsWith("/") ? image.getUrl() : "/" + image.getUrl());

        Glide.with(context)
                .load(fullUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.placeholder_image)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return images != null ? images.size() : 0;
    }

    static class ViewerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ViewerViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}