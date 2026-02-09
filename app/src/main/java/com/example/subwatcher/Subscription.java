package com.example.subwatcher;

public class Subscription {
    private String name;
    private double price;
    private String cycle;
    private String category;

    public Subscription(String name, double price, String cycle, String category) {
        this.name = name;
        this.price = price;
        this.cycle = cycle;
        this.category = category;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getCycle() { return cycle; }
    public String getCategory() { return category; }
}