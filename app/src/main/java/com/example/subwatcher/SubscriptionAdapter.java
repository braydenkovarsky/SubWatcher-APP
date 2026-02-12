package com.example.subwatcher;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.ViewHolder> {

    private List<Subscription> subList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Subscription item, int position);
    }

    public SubscriptionAdapter(List<Subscription> subList, OnItemClickListener listener) {
        this.subList = subList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // We still use simple_list_item_2 for now, but we force the colors below
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Subscription sub = subList.get(position);

        // Force White for Title
        holder.text1.setText(sub.getName());
        holder.text1.setTextColor(Color.WHITE);

        // Force Light Gray/White for Subtitle
        holder.text2.setText(String.format("$%.2f - %s (%s)", sub.getPrice(), sub.getPlanType(), sub.getCategory()));
        holder.text2.setTextColor(Color.parseColor("#B0B0B0"));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(sub, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }
    }
}