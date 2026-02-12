package com.example.subwatcher;

import java.io.Serializable;

public class Subscription implements Serializable {
    private String name;
    private double price;
    private String planType;
    private String category;

    // Required for Gson and some serialization processes
    public Subscription() {
    }

    // Main constructor used for adding, duplicating, and importing
    public Subscription(String name, double price, String planType, String category) {
        this.name = name;
        this.price = price;
        this.planType = planType;
        this.category = category;
    }

    // Getters
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getPlanType() { return planType; }
    public String getCategory() { return category; }

    // Setters (Useful for editing existing subscriptions)
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setPlanType(String planType) { this.planType = planType; }
    public void setCategory(String category) { this.category = category; }
}