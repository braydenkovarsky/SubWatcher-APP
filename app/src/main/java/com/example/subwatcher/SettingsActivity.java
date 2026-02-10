package com.example.subwatcher;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat; // Updated for better compatibility
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 1. Setup Bottom Navigation
        setupNavigation();

        // 2. Initialize UI Components
        // Using SwitchCompat to match the Material/AppCompat design
        SwitchCompat switchNotifications = findViewById(R.id.switchNotifications);
        Button btnClearAll = findViewById(R.id.btnClearAll);

        // 3. Handle Notification Toggle
        if (switchNotifications != null) {
            switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    checkNotificationPermission();
                }
            });
        }

        // 4. Handle Data Wipe with Fixed Dialog Reference
        if (btnClearAll != null) {
            btnClearAll.setOnClickListener(v -> {
                // FIXED: Removed the problematic style reference.
                // This will now use your app's default Dark Theme.
                new AlertDialog.Builder(this)
                        .setTitle("Delete Everything?")
                        .setMessage("This will permanently wipe all your subscriptions. This cannot be undone.")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            deleteAllData();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }
    }

    private void setupNavigation() {
        TextView navDashboard = findViewById(R.id.navDashboard);
        TextView navCategories = findViewById(R.id.navCategories);

        navDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        navCategories.setOnClickListener(v -> {
            Intent intent = new Intent(this, CategoriesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
    }

    private void deleteAllData() {
        // SharedPrefs name "sub_prefs" must match your MainActivity's name
        SharedPreferences sharedPreferences = getSharedPreferences("sub_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(this, "All data deleted", Toast.LENGTH_SHORT).show();

        // This clears the activity stack and sends you back to a fresh Dashboard
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }
}