package com.example.subwatcher;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class CategoriesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Categories");
    }
}