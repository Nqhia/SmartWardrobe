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
import vn.edu.usth.smartwaro.mycloset.GalleryImage;
import vn.edu.usth.smartwaro.network.FlaskNetwork;

public class FavoriteSetItemAdapter extends RecyclerView.Adapter<FavoriteSetItemAdapter.ItemViewHolder> {

    public interface OnItemRemoveListener {
        void onItemRemove(GalleryImage image, int position);
    }

    private OnItemRemoveListener removeListener;
    private List<GalleryImage> items = new ArrayList<>();
    private Context context;

    public FavoriteSetItemAdapter() {
        // Empty constructor
    }

    public void setItems(List<GalleryImage> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setOnItemRemoveListener(OnItemRemoveListener listener) {
        this.removeListener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (context == null) context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite_set_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        GalleryImage image = items.get(position);
        String fullUrl = image.getUrl();
        if (!fullUrl.startsWith("http")) {
            fullUrl = FlaskNetwork.BASE_URL + (fullUrl.startsWith("/") ? fullUrl : "/" + fullUrl);
        }
        Glide.with(context)
                .load(fullUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.ic_error_image)
                .into(holder.imageView);

        // Nếu người dùng long press, báo hiệu xóa item
        holder.itemView.setOnLongClickListener(v -> {
            if (removeListener != null) {
                removeListener.onItemRemove(image, position);
            }
            return true;
        });
    }



    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ItemViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgFavoriteItem);
        }
    }
}
