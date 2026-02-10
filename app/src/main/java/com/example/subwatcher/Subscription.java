package com.example.subwatcher;

import java.io.Serializable;

public class Subscription implements Serializable {
    private String name;
    private double price;
    private String planType;
    private String category;

    public Subscription(String name, double price, String planType, String category) {
        this.name = name;
        this.price = price;
        this.planType = planType;
        this.category = category;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getPlanType() { return planType; }
    public String getCategory() { return category; }
}