package vn.edu.usth.smartwaro.mycloset;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.mycloset.CategoriesFragment.CategoryItem;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private Context context;
    private List<CategoryItem> categories;

    public CategoryAdapter(Context context, List<CategoryItem> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryItem category = categories.get(position);
        holder.iconView.setImageResource(category.iconResId);
        holder.nameView.setText(category.name);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView iconView;
        TextView nameView;

        CategoryViewHolder(View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.category_icon);
            nameView = itemView.findViewById(R.id.category_name);
        }
    }
}