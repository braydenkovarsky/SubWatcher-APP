package com.example.subwatcher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.ViewHolder> {
    private List<Subscription> subscriptions;

    public SubscriptionAdapter(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subscription, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Subscription sub = subscriptions.get(position);

        holder.name.setText(sub.getName());

        // Formatting the price string to match your design: "$22.99 / Monthly"
        String priceDisplay = String.format("$%.2f / %s", sub.getPrice(), sub.getCycle());
        holder.price.setText(priceDisplay);

        // Optional: Logic to change icon based on category could go here
        // holder.logo.setImageResource(R.drawable.some_icon);
    }

    @Override
    public int getItemCount() {
        return subscriptions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        ImageView logo, notificationIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvSubName);
            price = itemView.findViewById(R.id.tvSubPrice);
            logo = itemView.findViewById(R.id.imgLogo);
            notificationIcon = itemView.findViewById(R.id.imgNotification);
        }
    }
}