package com.example.subwatcher;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executor;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences settingsPrefs;
    private static final int PICK_FILE_REQUEST = 202;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsPrefs = getSharedPreferences("settings_prefs", Context.MODE_PRIVATE);

        setupNavigation();

        SwitchCompat switchDarkMode = findViewById(R.id.switchDarkMode);
        SwitchCompat switchBiometrics = findViewById(R.id.switchBiometrics);
        TextView btnSetPassword = findViewById(R.id.btnSetPassword);
        TextView btnExport = findViewById(R.id.btnExport);
        TextView btnImport = findViewById(R.id.btnImport);
        TextView btnSupport = findViewById(R.id.btnSupport);
        Button btnClearAll = findViewById(R.id.btnClearAll);

        if (switchDarkMode != null) {
            switchDarkMode.setChecked(settingsPrefs.getBoolean("dark_mode", true));
            switchDarkMode.setOnCheckedChangeListener((v, isChecked) -> {
                settingsPrefs.edit().putBoolean("dark_mode", isChecked).apply();
                AppCompatDelegate.setDefaultNightMode(isChecked ?
                        AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            });
        }

        if (switchBiometrics != null) {
            switchBiometrics.setChecked(settingsPrefs.getBoolean("use_biometrics", false));
            switchBiometrics.setOnCheckedChangeListener((v, isChecked) -> {
                if (v.isPressed()) {
                    if (isChecked) {
                        checkAndEnableBiometrics();
                    } else {
                        settingsPrefs.edit()
                                .putBoolean("use_biometrics", false)
                                .putString("app_password_hash", "")
                                .putString("recovery_key_hash", "")
                                .apply();
                        Toast.makeText(this, "Security Disabled", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (btnSetPassword != null) {
            btnSetPassword.setOnClickListener(v -> showCustomPasswordDialog());
        }

        if (btnExport != null) {
            btnExport.setOnClickListener(v -> exportToCSV());
        }
        if (btnImport != null) {
            btnImport.setOnClickListener(v -> openFilePicker());
        }

        if (btnSupport != null) {
            btnSupport.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:support@subwatcher.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "SubWatcher Feedback");
                try {
                    startActivity(Intent.createChooser(intent, "Send Email"));
                } catch (Exception e) {
                    Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnClearAll != null) {
            btnClearAll.setOnClickListener(v -> showDeleteConfirmation());
        }
    }

    // --- NAVIGATION LOGIC (REORDER TO FRONT FIX) ---
    private void setupNavigation() {
        TextView navDashboard = findViewById(R.id.navDashboard);
        TextView navCategories = findViewById(R.id.navCategories);

        if (navDashboard != null) {
            navDashboard.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        if (navCategories != null) {
            navCategories.setOnClickListener(v -> {
                Intent intent = new Intent(this, CategoriesActivity.class);

                // DATA RECOVERY: Ensure the sub_list is passed from SharedPreferences
                SharedPreferences subPrefs = getSharedPreferences("sub_prefs", MODE_PRIVATE);
                String json = subPrefs.getString("sub_list", null);
                if (json != null) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<Subscription>>() {}.getType();
                    ArrayList<Subscription> list = gson.fromJson(json, type);
                    intent.putExtra("sub_list", list);
                }

                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }
    }

    // --- SECURITY UTILS ---
    private String hashPassword(String password) {
        try {
            String clean = password.replaceAll("[^a-zA-Z0-9]", "").toUpperCase().trim();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(clean.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private void generateRecoveryKey() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < 16; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        String recoveryKey = sb.toString();

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("SubWatcher Recovery Key", recoveryKey);
        clipboard.setPrimaryClip(clip);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_recovery_key, null);

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.CustomDialogTheme)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        TextView tvKey = dialogView.findViewById(R.id.tvRecoveryKeyDisplay);
        Button btnCopy = dialogView.findViewById(R.id.btnCopyAgain);
        Button btnDone = dialogView.findViewById(R.id.btnDone);

        tvKey.setText(recoveryKey);

        btnCopy.setOnClickListener(v -> {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copied to clipboard!", Toast.LENGTH_SHORT).show();
        });

        btnDone.setOnClickListener(v -> {
            String hashedKey = hashPassword(recoveryKey);
            settingsPrefs.edit().putString("recovery_key_hash", hashedKey).apply();
            Toast.makeText(this, "Security Setup Complete", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        dialog.show();
    }

    private void showCustomPasswordDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_set_password, null);

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.CustomDialogTheme)
                .setView(dialogView)
                .create();

        EditText input = dialogView.findViewById(R.id.etPasswordInput);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(v -> {
            String password = input.getText().toString();
            if (password.length() < 4) {
                Toast.makeText(this, "Password must be at least 4 characters", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
                    StringBuilder hexString = new StringBuilder();
                    for (byte b : encodedHash) {
                        String hex = Integer.toHexString(0xff & b);
                        if (hex.length() == 1) hexString.append('0');
                        hexString.append(hex);
                    }

                    settingsPrefs.edit()
                            .putString("app_password_hash", hexString.toString())
                            .putBoolean("use_biometrics", true)
                            .apply();

                    SwitchCompat sw = findViewById(R.id.switchBiometrics);
                    if (sw != null) sw.setChecked(true);

                    dialog.dismiss();
                    generateRecoveryKey();
                } catch (Exception e) {
                    Toast.makeText(this, "Error saving password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        dialog.show();
    }

    private void checkAndEnableBiometrics() {
        BiometricManager biometricManager = BiometricManager.from(this);
        int canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);

        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            showBiometricPromptToEnable();
        } else {
            SwitchCompat switchBiometrics = findViewById(R.id.switchBiometrics);
            if (switchBiometrics != null) switchBiometrics.setChecked(false);

            new AlertDialog.Builder(this)
                    .setTitle("Biometrics Unavailable")
                    .setMessage("Your device doesn't support biometrics. Set a custom password?")
                    .setPositiveButton("Set Password", (dialog, which) -> showCustomPasswordDialog())
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    private void showBiometricPromptToEnable() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(SettingsActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                settingsPrefs.edit().putBoolean("use_biometrics", true).apply();
                Toast.makeText(SettingsActivity.this, "Security Enabled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                SwitchCompat switchBiometrics = findViewById(R.id.switchBiometrics);
                if (switchBiometrics != null) switchBiometrics.setChecked(false);
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Secure SubWatcher")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    // --- DATA MANAGEMENT ---
    private void exportToCSV() {
        SharedPreferences subPrefs = getSharedPreferences("sub_prefs", MODE_PRIVATE);
        String json = subPrefs.getString("sub_list", null);

        if (json == null || json.isEmpty() || json.equals("[]")) {
            Toast.makeText(this, "Nothing to export", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Subscription>>() {}.getType();
            ArrayList<Subscription> list = gson.fromJson(json, type);

            StringBuilder csvData = new StringBuilder();
            csvData.append("Name,Price,Plan,Category\n");
            for (Subscription sub : list) {
                csvData.append(sub.getName()).append(",")
                        .append(sub.getPrice()).append(",")
                        .append(sub.getPlanType()).append(",")
                        .append(sub.getCategory()).append("\n");
            }

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/csv");
            sendIntent.putExtra(Intent.EXTRA_TEXT, csvData.toString());
            startActivity(Intent.createChooser(sendIntent, "Share Backup"));
        } catch (Exception e) {
            Toast.makeText(this, "Export failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Select CSV"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            handleImport(data.getData());
        }
    }

    private void handleImport(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            ArrayList<Subscription> importedList = new ArrayList<>();
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 4) {
                    importedList.add(new Subscription(p[0], Double.parseDouble(p[1]), p[2], p[3]));
                }
            }
            getSharedPreferences("sub_prefs", MODE_PRIVATE).edit()
                    .putString("sub_list", new Gson().toJson(importedList)).apply();

            // Navigate to Dashboard after import
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Import failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Wipe Data?")
                .setMessage("Delete all subscriptions?")
                .setPositiveButton("Wipe", (dialog, which) -> deleteAllData())
                .setNegativeButton("Cancel", null).show();
    }

    private void deleteAllData() {
        getSharedPreferences("sub_prefs", MODE_PRIVATE).edit().clear().apply();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}