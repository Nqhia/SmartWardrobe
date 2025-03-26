package vn.edu.usth.smartwaro.wardrobe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.network.FlaskNetwork;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private String[] imageUrls;
    private OnImageClickListener onImageClickListener;

    public interface OnImageClickListener {
        void onImageClick(String imageUrl);
    }

    public ImageAdapter(String[] imageUrls, OnImageClickListener listener) {
        this.imageUrls = imageUrls;
        this.onImageClickListener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls[position];
        Picasso.get()
                .load(FlaskNetwork.BASE_URL + "/" + imageUrl)
                .resize(80, 80)
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(v -> onImageClickListener.onImageClick(imageUrl));
    }

    @Override
    public int getItemCount() {
        return imageUrls.length;
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}