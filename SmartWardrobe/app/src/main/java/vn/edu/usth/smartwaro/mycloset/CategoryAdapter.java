package vn.edu.usth.smartwaro.mycloset;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.edu.usth.smartwaro.R;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private final List<String> categories;
    private String selectedCategory;
    private final OnCategorySelectedListener listener;

    public interface OnCategorySelectedListener {
        void onCategorySelected(String category);
    }

    public CategoryAdapter(List<String> categories, String selectedCategory,
                           OnCategorySelectedListener listener) {
        this.categories = categories;
        this.selectedCategory = selectedCategory;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String category = categories.get(position);
        holder.categoryName.setText(category);
        holder.radioButton.setChecked(category.equals(selectedCategory));

        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> {
            // Update the selected category
            if (!category.equals(selectedCategory)) {
                String previousCategory = selectedCategory;
                selectedCategory = category;

                // Notify listener about the change
                listener.onCategorySelected(category);

                // Update the UI for the previous and current selections
                notifyItemChanged(categories.indexOf(previousCategory));
                notifyItemChanged(position);
            }
        });

        // Optional: Set click listener for RadioButton (if needed)
        holder.radioButton.setOnClickListener(v -> {
            if (!category.equals(selectedCategory)) {
                String previousCategory = selectedCategory;
                selectedCategory = category;

                // Notify listener about the change
                listener.onCategorySelected(category);

                // Update the UI for the previous and current selections
                notifyItemChanged(categories.indexOf(previousCategory));
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton radioButton;
        TextView categoryName;

        ViewHolder(View view) {
            super(view);
            radioButton = view.findViewById(R.id.radioButton);
            categoryName = view.findViewById(R.id.categoryName);
        }
    }
}
