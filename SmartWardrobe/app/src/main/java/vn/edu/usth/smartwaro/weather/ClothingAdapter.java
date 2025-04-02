package vn.edu.usth.smartwaro.weather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.network.FlaskNetwork;

public class ClothingAdapter extends RecyclerView.Adapter<ClothingAdapter.ClothingViewHolder> {
    private List<String> imageUrls;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String imageUrl, int position);
    }

    public ClothingAdapter() {
        this.imageUrls = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClothingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_clothing, parent, false);
        return new ClothingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClothingViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);

        String fullImageUrl = FlaskNetwork.BASE_URL + imageUrl;

        Glide.with(holder.itemView.getContext())
                .load(fullImageUrl)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.placeholder_clothing)
                        .error(R.drawable.error_clothing)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop())
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(imageUrl, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public void updateItems(String[] newUrls) {
        imageUrls.clear();
        if (newUrls != null) {
            imageUrls.addAll(Arrays.asList(newUrls));
        }
        notifyDataSetChanged();
    }

    public void addItem(String imageUrl) {
        imageUrls.add(imageUrl);
        notifyItemInserted(imageUrls.size() - 1);
    }

    public void removeItem(int position) {
        if (position >= 0 && position < imageUrls.size()) {
            imageUrls.remove(position);
            notifyItemRemoved(position);
        }
    }

    public String getItem(int position) {
        if (position >= 0 && position < imageUrls.size()) {
            return imageUrls.get(position);
        }
        return null;
    }

    static class ClothingViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ClothingViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.clothingImageView);
        }
    }
}