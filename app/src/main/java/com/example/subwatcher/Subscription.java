package com.example.subwatcher;

import java.io.Serializable;

public class Subscription implements Serializable {
    private String name;
    private double price;
    private String planType; // Changed from 'cycle' to 'planType'
    private String category;

    public Subscription(String name, double price, String planType, String category) {
        this.name = name;
        this.price = price;
        this.planType = planType;
        this.category = category;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }

    // THIS IS THE METHOD MAINACTIVITY IS LOOKING FOR
    public String getPlanType() {
        return (planType != null) ? planType : "Monthly";
    }

    // Setters (Added so you can edit subscriptions later)
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setPlanType(String planType) { this.planType = planType; }
    public void setCategory(String category) { this.category = category; }
}