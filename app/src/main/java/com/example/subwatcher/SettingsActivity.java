package com.example.subwatcher;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 1. Setup the Top Bar with a Back Arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }

        // 2. Initialize UI Components
        Switch switchNotifications = findViewById(R.id.switchNotifications);
        Button btnClearAll = findViewById(R.id.btnClearAll);

        // 3. Handle Notification Toggle
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkNotificationPermission();
            }
        });

        // 4. Handle Data Wipe with Confirmation Dialog
        btnClearAll.setOnClickListener(v -> {
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

    // This makes the back arrow in the top bar actually close the activity
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void deleteAllData() {
        // Corrected SharedPreferences name to match MainActivity
        SharedPreferences sharedPreferences = getSharedPreferences("sub_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clears the "sub_list" key
        editor.apply();

        Toast.makeText(this, "All data deleted", Toast.LENGTH_SHORT).show();

        // Signal back to MainActivity that data has changed
        setResult(RESULT_OK);

        // Auto-close settings to return to the dashboard
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