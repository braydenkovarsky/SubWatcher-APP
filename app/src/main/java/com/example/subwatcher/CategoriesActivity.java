package com.example.subwatcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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

    private RecyclerView rvCategories;
    private CategoryAdapter adapter;
    private List<CategorySummary> summaryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        // --- NAVIGATION LOGIC ---
        setupNavigation();

        rvCategories = findViewById(R.id.rvCategories);
        rvCategories.setLayoutManager(new LinearLayoutManager(this));

        processIntentData(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        processIntentData(intent);
    }

    private void processIntentData(Intent intent) {
        ArrayList<Subscription> subs = (ArrayList<Subscription>) intent.getSerializableExtra("sub_list");
        summaryList.clear();

        if (subs != null) {
            Map<String, Double> categoryMap = new HashMap<>();
            Map<String, Integer> countMap = new HashMap<>();

            for (Subscription s : subs) {
                String cat = (s.getCategory() == null || s.getCategory().trim().isEmpty()) ? "Other" : s.getCategory();
                double monthly = ("Yearly".equalsIgnoreCase(s.getPlanType())) ? (s.getPrice() / 12.0) : s.getPrice();

                categoryMap.put(cat, categoryMap.getOrDefault(cat, 0.0) + monthly);
                countMap.put(cat, countMap.getOrDefault(cat, 0) + 1);
            }

            for (String catName : categoryMap.keySet()) {
                summaryList.add(new CategorySummary(catName, categoryMap.get(catName), countMap.get(catName)));
            }
        }

        adapter = new CategoryAdapter(summaryList);
        rvCategories.setAdapter(adapter);
    }

    private void setupNavigation() {
        TextView navDashboard = findViewById(R.id.navDashboard);
        TextView navSettings = findViewById(R.id.navSettings);

        // Dashboard Navigation
        navDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        // Settings Navigation (Fixed this part!)
        navSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
    }

    static class CategorySummary {
        String name;
        double total;
        int count;
        CategorySummary(String name, double total, int count) {
            this.name = name;
            this.total = total;
            this.count = count;
        }
    }

    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
        private List<CategorySummary> data;
        private double grandTotal = 0;

        CategoryAdapter(List<CategorySummary> data) {
            this.data = data;
            for (CategorySummary s : data) grandTotal += s.total;
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
            holder.tvTotal.setText(String.format("$%.2f", item.total));

            double percent = (grandTotal > 0) ? (item.total / grandTotal) * 100 : 0;
            holder.tvPercent.setText(String.format("%.1f%% of total spending", percent));
            holder.tvSubCount.setText(item.count + (item.count == 1 ? " Subscription" : " Subscriptions"));
            holder.pbCategory.setProgress((int) percent);
        }

        @Override
        public int getItemCount() { return data.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvTotal, tvPercent, tvSubCount;
            ProgressBar pbCategory;

            ViewHolder(View v) {
                super(v);
                tvName = v.findViewById(R.id.tvCatName);
                tvTotal = v.findViewById(R.id.tvCatTotal);
                tvPercent = v.findViewById(R.id.tvCatPercent);
                tvSubCount = v.findViewById(R.id.tvSubCount);
                pbCategory = v.findViewById(R.id.pbCategory);
            }
        }
    }
}