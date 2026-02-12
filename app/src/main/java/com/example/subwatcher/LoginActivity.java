package com.example.subwatcher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences settingsPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Check security status
        settingsPrefs = getSharedPreferences("settings_prefs", MODE_PRIVATE);
        boolean isSecurityEnabled = settingsPrefs.getBoolean("use_biometrics", false);
        String storedHash = settingsPrefs.getString("app_password_hash", "");

        // 2. Immediate Bypass if disabled
        if (!isSecurityEnabled || storedHash.isEmpty()) {
            goToDashboard();
            return;
        }

        // 3. Setup Locked UI
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_login);

        EditText etPassword = findViewById(R.id.etLoginPassword);
        Button btnSubmit = findViewById(R.id.btnLoginSubmit);
        TextView btnForgot = findViewById(R.id.btnForgotPassword);

        etPassword.setVisibility(View.VISIBLE);
        btnSubmit.setVisibility(View.VISIBLE);

        // 4. Trigger Biometrics
        showBiometricPrompt();

        // 5. Manual Password Entry
        btnSubmit.setOnClickListener(v -> {
            String input = etPassword.getText().toString();
            // Note: We hash the raw password input directly for the app password
            if (hashPasswordSimple(input).equals(storedHash)) {
                goToDashboard();
            } else {
                Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show();
            }
        });

        if (btnForgot != null) {
            btnForgot.setOnClickListener(v -> showRecoveryDialog());
        }
    }

    // Replace your showRecoveryDialog and hashPassword methods with these
    private void showRecoveryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Account Recovery");
        builder.setMessage("Enter your 16-character key.");

        final EditText input = new EditText(this);
        input.setHint("16 characters");
        builder.setView(input);

        builder.setPositiveButton("Reset Security", (dialog, which) -> {
            String userInput = input.getText().toString();

            // Use the EXACT SAME hash function
            String hashedInput = hashPassword(userInput);
            String storedRecoveryHash = settingsPrefs.getString("recovery_key_hash", "");

            if (!storedRecoveryHash.isEmpty() && hashedInput.equals(storedRecoveryHash)) {
                settingsPrefs.edit()
                        .putBoolean("use_biometrics", false)
                        .putString("app_password_hash", "")
                        .putString("recovery_key_hash", "")
                        .apply();

                Toast.makeText(this, "Security Reset Successfully", Toast.LENGTH_SHORT).show();
                goToDashboard();
            } else {
                Toast.makeText(this, "Invalid Recovery Key", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private String hashPassword(String password) {
        try {
            // EXACT SAME STRICT CLEANING as SettingsActivity
            String clean = password.replaceAll("[^a-zA-Z0-9]", "").toUpperCase().trim();

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(clean.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) { return ""; }
    }

    /**
     * Simple hashing for the standard App Password (no cleaning)
     */
    private String hashPasswordSimple(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private void showBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        goToDashboard();
                    }
                });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Unlock SubWatcher")
                .setSubtitle("Use biometrics to continue")
                .setNegativeButtonText("Use Password")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void goToDashboard() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}