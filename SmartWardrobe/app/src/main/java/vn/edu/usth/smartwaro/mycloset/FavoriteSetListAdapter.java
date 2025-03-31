package vn.edu.usth.smartwaro.mycloset;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import vn.edu.usth.smartwaro.R;
import java.util.List;

public class FavoriteSetListAdapter extends RecyclerView.Adapter<FavoriteSetListAdapter.ViewHolder> {

    public interface OnSetClickListener {
        void onSetClick(FavoriteSet set);
    }

    private List<FavoriteSet> favoriteSets;
    private OnSetClickListener listener;

    public FavoriteSetListAdapter(List<FavoriteSet> favoriteSets, OnSetClickListener listener) {
        this.favoriteSets = favoriteSets;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout chứa TextView tvSetName
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_set, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteSet set = favoriteSets.get(position);
        holder.tvSetName.setText(set.getSetName());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSetClick(set);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteSets.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSetName;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSetName = itemView.findViewById(R.id.tvSetName);
        }
    }
}
