package com.example.subwatcher;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 1. Link to the Switch from your XML
        Switch switchNotifications = findViewById(R.id.switchNotifications);
        Button btnClearAll = findViewById(R.id.btnClearAll);

        // 2. Handle the Notification Switch
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkNotificationPermission();
            } else {
                Toast.makeText(this, "Notifications Disabled", Toast.LENGTH_SHORT).show();
            }
        });

        // 3. Handle the Delete Button
        btnClearAll.setOnClickListener(v -> {
            // For now, just a message. Later we can add code to wipe the list.
            Toast.makeText(this, "Data Cleared (Simulated)", Toast.LENGTH_SHORT).show();
        });
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            } else {
                Toast.makeText(this, "Notifications are active!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Notifications enabled!", Toast.LENGTH_SHORT).show();
        }
    }
}