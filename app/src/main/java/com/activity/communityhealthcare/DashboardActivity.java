package com.activity.communityhealthcare;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set status bar color programmatically
        setDarkStatusBar();

        setContentView(R.layout.activity_dashboard);
        checkPatientData();
        setupGetStartedButton();
    }

    private void setDarkStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For Android M and above - set dark status bar icons
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Set the status bar color
            getWindow().setStatusBarColor(Color.parseColor("#C96A00"));
        }
    }

    private void checkPatientData() {
        android.content.SharedPreferences prefs = getSharedPreferences("PatientData", MODE_PRIVATE);
        boolean hasPatientData = prefs.getBoolean("has_patient_data", false);
        // You can use this flag for conditional logic later
    }

    private void setupGetStartedButton() {
        Button btnGetStarted = findViewById(R.id.btnGetStarted);

        btnGetStarted.setOnClickListener(v -> {
            String[] options = {"Schedule a Consultation", "Patient Information", "Consultation Logs"};
            int[] icons = {R.drawable.ic_consultation, R.drawable.ic_patient, R.drawable.ic_schedule};

            OptionAdapter adapter = new OptionAdapter(DashboardActivity.this, options, icons);

            new MaterialAlertDialogBuilder(DashboardActivity.this)
                    .setTitle("Choose an option")
                    .setAdapter(adapter, (dialog, which) -> {
                        if (which == 0) {
                            // Schedule a Consultation - Show tracking number dialog
                            showTrackingNumberDialog();
                        } else if (which == 1) {
                            // Patient Information
                            android.content.SharedPreferences prefs = getSharedPreferences("PatientData", MODE_PRIVATE);
                            boolean hasPatientData = prefs.getBoolean("has_patient_data", false);

                            if (hasPatientData) {
                                // If patient data already exists, go to profile
                                // startActivity(new Intent(DashboardActivity.this, PatientProfileActivity.class));
                            } else {
                                // If no patient data, show data privacy dialog first
                                showDataPrivacyDialog();
                            }
                        } else if (which == 2) {
                            // Consultation Logs
                            // startActivity(new Intent(DashboardActivity.this, ConsultationLogsActivity.class));
                        }
                    })
                    .show();
        });
    }

    private void showTrackingNumberDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_tracking_number, null);

        // Get all tracking number inputs
        EditText etTrack1 = dialogView.findViewById(R.id.etTrack1);
        EditText etTrack2 = dialogView.findViewById(R.id.etTrack2);
        EditText etTrack3 = dialogView.findViewById(R.id.etTrack3);
        EditText etTrack4 = dialogView.findViewById(R.id.etTrack4);
        EditText etTrack5 = dialogView.findViewById(R.id.etTrack5);
        EditText etTrack6 = dialogView.findViewById(R.id.etTrack6);
        EditText etTrack7 = dialogView.findViewById(R.id.etTrack7);
        EditText etTrack8 = dialogView.findViewById(R.id.etTrack8);

        MaterialButton btnConfirmTracking = dialogView.findViewById(R.id.btnConfirmTracking);

        // Initially disable the confirm button
        btnConfirmTracking.setEnabled(false);
        btnConfirmTracking.setAlpha(0.5f);

        // Array of all input fields for easy management
        EditText[] trackingInputs = {
                etTrack1, etTrack2, etTrack3, etTrack4,
                etTrack5, etTrack6, etTrack7, etTrack8
        };

        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // Set smaller modal size for better fit
        dialog.setOnShowListener(dialogInterface -> {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);

                // Set compact modal size
                int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.80);
                int height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

                dialog.getWindow().setLayout(width, height);
            }
        });

        // Add text watchers to all input fields
        for (int i = 0; i < trackingInputs.length; i++) {
            final int currentIndex = i;
            EditText currentInput = trackingInputs[i];

            currentInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Auto-focus to next field when text is entered
                    if (s.length() == 1 && currentIndex < trackingInputs.length - 1) {
                        trackingInputs[currentIndex + 1].requestFocus();
                    }

                    // Check if all fields are filled
                    checkAllFieldsFilled(trackingInputs, btnConfirmTracking);
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        // Confirm button click listener
        btnConfirmTracking.setOnClickListener(v -> {
            // Build the complete tracking number
            StringBuilder trackingNumber = new StringBuilder();
            for (EditText input : trackingInputs) {
                trackingNumber.append(input.getText().toString());
            }

            String finalTrackingNumber = trackingNumber.toString();

            // Validate the format (first two should be letters, rest numbers)
            if (isValidTrackingNumber(finalTrackingNumber)) {
                // Save tracking number and proceed to doctors list
                android.content.SharedPreferences prefs = getSharedPreferences("AppPreferences", MODE_PRIVATE);
                prefs.edit().putString("tracking_number", finalTrackingNumber).apply();

                dialog.dismiss();
                // Proceed to Doctors List Activity
                // startActivity(new Intent(DashboardActivity.this, DoctorsListActivity.class));
                Toast.makeText(this, "Tracking number confirmed: " + finalTrackingNumber, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Invalid tracking number format", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void checkAllFieldsFilled(EditText[] inputs, MaterialButton confirmButton) {
        boolean allFilled = true;
        for (EditText input : inputs) {
            if (input.getText().toString().trim().isEmpty()) {
                allFilled = false;
                break;
            }
        }

        confirmButton.setEnabled(allFilled);
        if (allFilled) {
            confirmButton.setAlpha(1.0f);
            animateButtonPulse(confirmButton);
        } else {
            confirmButton.setAlpha(0.5f);
        }
    }

    private boolean isValidTrackingNumber(String trackingNumber) {
        if (trackingNumber.length() != 8) {
            return false;
        }

        // Check first two characters are letters
        if (!Character.isLetter(trackingNumber.charAt(0)) || !Character.isLetter(trackingNumber.charAt(1))) {
            return false;
        }

        // Check remaining characters are numbers
        for (int i = 2; i < 8; i++) {
            if (!Character.isDigit(trackingNumber.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private void showDataPrivacyDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_data_privacy, null);

        CheckBox checkBoxPrivacy = dialogView.findViewById(R.id.checkBoxPrivacy);
        Button btnContinue = dialogView.findViewById(R.id.btnContinue);

        // Initially disable the continue button
        btnContinue.setEnabled(false);
        btnContinue.setAlpha(0.5f); // Make it look disabled

        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        // Set proper modal size for dialog
        dialog.setOnShowListener(dialogInterface -> {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);

                // Set modal size instead of full screen
                int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85);
                int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.7);

                dialog.getWindow().setLayout(width, height);
            }
        });

        // Checkbox listener to enable/disable continue button
        checkBoxPrivacy.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnContinue.setEnabled(isChecked);
            if (isChecked) {
                btnContinue.setAlpha(1.0f); // Make it fully visible
                animateButtonPulse(btnContinue);
            } else {
                btnContinue.setAlpha(0.5f); // Make it look disabled
            }
        });

        // Continue button click listener
        btnContinue.setOnClickListener(v -> {
            // Save that user has agreed to data privacy
            android.content.SharedPreferences prefs = getSharedPreferences("AppPreferences", MODE_PRIVATE);
            prefs.edit().putBoolean("data_privacy_accepted", true).apply();

            dialog.dismiss();
            // Proceed to Patient Information Activity
            startActivity(new Intent(DashboardActivity.this, PatientInformationActivity.class));
        });

        dialog.show();
    }

    private void animateButtonPulse(Button button) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            button.animate()
                    .scaleX(1.05f)
                    .scaleY(1.05f)
                    .setDuration(200)
                    .withEndAction(() -> button.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(200)
                            .start())
                    .start();
        }
    }
}