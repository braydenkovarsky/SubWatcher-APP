package com.example.subwatcher;

import android.content.Intent;
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

        // --- NAVIGATION LOGIC ---
        setupNavigation();

        RecyclerView rvCategories = findViewById(R.id.rvCategories);
        rvCategories.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Subscription> subs = (ArrayList<Subscription>) getIntent().getSerializableExtra("sub_list");
        List<CategorySummary> summaryList = new ArrayList<>();

        if (subs != null) {
            Map<String, Double> categoryMap = new HashMap<>();
            for (Subscription s : subs) {
                String cat = (s.getCategory() == null || s.getCategory().isEmpty()) ? "Other" : s.getCategory();
                categoryMap.put(cat, categoryMap.getOrDefault(cat, 0.0) + s.getPrice());
            }

            for (Map.Entry<String, Double> entry : categoryMap.entrySet()) {
                summaryList.add(new CategorySummary(entry.getKey(), entry.getValue()));
            }
        }

        rvCategories.setAdapter(new CategoryAdapter(summaryList));
    }

    private void setupNavigation() {
        TextView navDashboard = findViewById(R.id.navDashboard);
        TextView navSettings = findViewById(R.id.navSettings);

        navDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            // This flag brings the existing Dashboard to the front instead of creating a new one
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0); // No jumpy animation
        });

        navSettings.setOnClickListener(v -> {
            // Check your Settings class name; usually it's SettingsActivity
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
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

    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
        private List<CategorySummary> data;

        CategoryAdapter(List<CategorySummary> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
                tvName = v.findViewById(R.id.tvCatName);
                tvTotal = v.findViewById(R.id.tvCatTotal);
            }
        }
    }
}