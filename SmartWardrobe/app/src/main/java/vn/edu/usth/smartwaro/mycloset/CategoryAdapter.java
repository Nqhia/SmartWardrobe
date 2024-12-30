package vn.edu.usth.smartwaro.mycloset;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.edu.usth.smartwaro.R;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<String> categories;
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String category = categories.get(position);
        holder.radioButton.setChecked(category.equals(selectedCategory));
        holder.categoryName.setText(category);

        holder.itemView.setOnClickListener(v -> {
            selectedCategory = category;
            listener.onCategorySelected(category);
            notifyDataSetChanged();
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
