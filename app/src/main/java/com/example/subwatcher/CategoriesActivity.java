package com.example.subwatcher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoriesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        // Link to your activity_categories.xml ID
        RecyclerView rvCategories = findViewById(R.id.rvCategories);
        rvCategories.setLayoutManager(new LinearLayoutManager(this));

        // 1. Receive the list from MainActivity
        ArrayList<Subscription> subs = (ArrayList<Subscription>) getIntent().getSerializableExtra("sub_list");

        List<CategorySummary> summaryList = new ArrayList<>();

        if (subs != null) {
            // 2. Group by category and sum prices
            Map<String, Double> categoryMap = new HashMap<>();
            for (Subscription s : subs) {
                // Ensure category isn't null to avoid crashes
                String cat = (s.getCategory() == null || s.getCategory().isEmpty()) ? "Other" : s.getCategory();
                categoryMap.put(cat, categoryMap.getOrDefault(cat, 0.0) + s.getPrice());
            }

            // 3. Convert map to list for display
            for (Map.Entry<String, Double> entry : categoryMap.entrySet()) {
                summaryList.add(new CategorySummary(entry.getKey(), entry.getValue()));
            }
        }

        // 4. Attach the adapter
        rvCategories.setAdapter(new CategoryAdapter(summaryList));
    }

    // Helper class to hold the category name and total
    static class CategorySummary {
        String name;
        double total;
        CategorySummary(String name, double total) {
            this.name = name;
            this.total = total;
        }
    }

    // The Adapter using your custom item_category.xml layout
    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
        private List<CategorySummary> data;

        CategoryAdapter(List<CategorySummary> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Updated to use your custom layout file
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CategorySummary item = data.get(position);
            holder.tvName.setText(item.name.toUpperCase());
            holder.tvTotal.setText(String.format("$%.2f Total Monthly", item.total));
        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvTotal;
            ViewHolder(View v) {
                super(v);
                // Updated to match the IDs in item_category.xml
                tvName = v.findViewById(R.id.tvCatName);
                tvTotal = v.findViewById(R.id.tvCatTotal);
            }
        }
    }
}