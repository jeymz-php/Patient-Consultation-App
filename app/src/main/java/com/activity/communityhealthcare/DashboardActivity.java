package com.activity.communityhealthcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private MaterialButton btnConfirmTracking; // Make it a class variable to access in multiple methods

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
                                // ✅ Go directly to the Patient Profile screen
                                Intent intent = new Intent(DashboardActivity.this, PatientProfileActivity.class);
                                startActivity(intent);
                            } else {
                                // ❗ Only show data privacy modal if no data saved yet
                                showDataPrivacyDialog();
                            }
                        } else if (which == 2) {
                            // Consultation Logs - FIXED: Now it's enabled
                            Intent intent = new Intent(DashboardActivity.this, ConsultationLogsActivity.class);
                            startActivity(intent);
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

        btnConfirmTracking = dialogView.findViewById(R.id.btnConfirmTracking);
        TextView txtNoTrackingNumber = dialogView.findViewById(R.id.txtNoTrackingNumber);

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

        // Add text watchers and key listeners to all input fields
        for (int i = 0; i < trackingInputs.length; i++) {
            final int currentIndex = i;
            EditText currentInput = trackingInputs[i];

            currentInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Handle backspace when deleting the only character in a field
                    if (count == 1 && after == 0 && s.length() == 1) {
                        // Character is being deleted, move to previous field
                        if (currentIndex > 0) {
                            currentInput.post(() -> {
                                trackingInputs[currentIndex - 1].requestFocus();
                                trackingInputs[currentIndex - 1].setSelection(
                                        trackingInputs[currentIndex - 1].getText().length()
                                );
                            });
                        }
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Auto-focus to next field when text is entered
                    if (s.length() == 1 && currentIndex < trackingInputs.length - 1) {
                        trackingInputs[currentIndex + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Check if all fields are filled
                    checkAllFieldsFilled(trackingInputs, btnConfirmTracking);
                }
            });

            // Additional key listener for hardware keyboard backspace
            currentInput.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                        EditText currentField = (EditText) v;

                        // If current field is empty and backspace is pressed, move focus to previous field
                        if (currentField.getText().length() == 0 && currentIndex > 0) {
                            trackingInputs[currentIndex - 1].requestFocus();
                            trackingInputs[currentIndex - 1].setSelection(
                                    trackingInputs[currentIndex - 1].getText().length()
                            );
                            return true; // Consume the event
                        }
                    }
                    return false;
                }
            });
        }

        // Confirm button click listener - UPDATED WITH DATABASE VALIDATION
        btnConfirmTracking.setOnClickListener(v -> {
            // Build the complete tracking number
            StringBuilder trackingNumber = new StringBuilder();
            for (EditText input : trackingInputs) {
                trackingNumber.append(input.getText().toString());
            }

            String finalTrackingNumber = trackingNumber.toString();

            // Validate the format (first two should be letters, rest numbers)
            if (isValidTrackingNumber(finalTrackingNumber)) {
                // Check if tracking number exists in database
                validateTrackingNumberWithDatabase(finalTrackingNumber, dialog);
            } else {
                Toast.makeText(this, "Invalid tracking number format. Format: AB123456", Toast.LENGTH_LONG).show();
            }
        });

        // NEW: Handle "Don't have tracking number?" click
        txtNoTrackingNumber.setOnClickListener(v -> {
            dialog.dismiss();
            showDataPrivacyDialog(); // Show data privacy modal first
        });

        dialog.show();
    }

    // NEW METHOD: Validate tracking number with API
    private void validateTrackingNumberWithDatabase(String trackingNumber, androidx.appcompat.app.AlertDialog dialog) {
        // Show loading state
        btnConfirmTracking.setEnabled(false);
        btnConfirmTracking.setText("Checking...");

        ApiService apiService = new ApiService(this);
        apiService.validateTrackingNumber(trackingNumber, new ApiService.ApiResponseListener() {
            @Override
            public void onSuccess(JSONObject response) {
                runOnUiThread(() -> {
                    try {
                        Log.d("API_DEBUG", "Full API Response: " + response.toString(2)); // Pretty print JSON
                        if ((response.has("success") && response.getBoolean("success")) ||
                                (response.has("exists") && response.getBoolean("exists"))) {

                            JSONObject patientDataJson = response.has("patient_data")
                                    ? response.getJSONObject("patient_data")
                                    : response.getJSONObject("patient");

                            // Debug the patient data JSON
                            Log.d("API_DEBUG", "Patient Data JSON: " + patientDataJson.toString(2));

                            // Check what patient_id field exists
                            if (patientDataJson.has("patient_id")) {
                                Log.d("API_DEBUG", "patient_id found: " + patientDataJson.get("patient_id"));
                            } else {
                                Log.d("API_DEBUG", "patient_id NOT found in response");
                                // Check for alternative field names
                                String[] possibleIdFields = {"id", "user_id", "patientId", "resident_id"};
                                for (String field : possibleIdFields) {
                                    if (patientDataJson.has(field)) {
                                        Log.d("API_DEBUG", "Found alternative ID field '" + field + "': " + patientDataJson.get(field));
                                    }
                                }
                            }

                            PatientData patientData = parsePatientDataFromJson(patientDataJson);
                            savePatientDataToSharedPreferences(patientData);

                            SharedPreferences prefs = getSharedPreferences("AppPreferences", MODE_PRIVATE);
                            prefs.edit().putString("tracking_number", trackingNumber).apply();

                            dialog.dismiss();
                            Toast.makeText(DashboardActivity.this, "Tracking number verified! Welcome " + patientData.getFirstName(), Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(DashboardActivity.this, PatientProfileActivity.class);
                            startActivity(intent);

                        } else {
                            String message = response.has("message") ? response.getString("message") : "Tracking number not found";
                            Toast.makeText(DashboardActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e("DashboardActivity", "JSON parsing error", e);
                        Toast.makeText(DashboardActivity.this, "Error parsing response", Toast.LENGTH_LONG).show();
                        btnConfirmTracking.setEnabled(true);
                        btnConfirmTracking.setText("Confirm Tracking Number");
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Log.e("DashboardActivity", "API Error: " + error);

                    // More specific error messages
                    if (error.contains("JSONException") || error.contains("ParseError")) {
                        Toast.makeText(DashboardActivity.this, "Server response error. Please try again.", Toast.LENGTH_LONG).show();
                    } else if (error.contains("Timeout") || error.contains("NoConnection")) {
                        Toast.makeText(DashboardActivity.this, "Network connection failed. Please check your internet.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(DashboardActivity.this, "Failed to verify tracking number: " + error, Toast.LENGTH_LONG).show();
                    }

                    // Reset button state
                    btnConfirmTracking.setEnabled(true);
                    btnConfirmTracking.setText("Confirm Tracking Number");
                });
            }
        });
    }

    // Helper method to parse JSON to PatientData
    private PatientData parsePatientDataFromJson(JSONObject json) throws JSONException {
        PatientData patientData = new PatientData();

        // Try different possible field names for patient_id
        if (json.has("patient_id")) {
            patientData.setPatientId(json.optInt("patient_id", 0));
        } else if (json.has("id")) {
            patientData.setPatientId(json.optInt("id", 0));
        } else if (json.has("user_id")) {
            patientData.setPatientId(json.optInt("user_id", 0));
        } else {
            patientData.setPatientId(json.optInt("patient_id", 0)); // default to 0 if not found
        }

        // Log what patient_id we found
        Log.d("PARSE_DEBUG", "Final patient_id: " + patientData.getPatientId());

        patientData.setFirstName(json.optString("first_name", ""));
        patientData.setLastName(json.optString("last_name", ""));
        patientData.setDateOfBirth(json.optString("date_of_birth", ""));
        patientData.setGender(json.optString("gender", ""));
        patientData.setContactNumber(json.optString("contact_number", ""));
        patientData.setEmail(json.optString("email", ""));
        patientData.setAddress(json.optString("address", ""));
        patientData.setCivilStatus(json.optString("civil_status", ""));
        patientData.setBarangay(json.optString("barangay_name", ""));
        patientData.setTrackingNumber(json.optString("tracking_number", ""));
        patientData.setResidentId(json.optInt("resident_id", 0));

        patientData.setEmergencyFirstName(json.optString("emergency_first_name", ""));
        patientData.setEmergencyLastName(json.optString("emergency_last_name", ""));
        patientData.setEmergencyRelationship(json.optString("emergency_relationship", ""));
        patientData.setEmergencyContactNumber(json.optString("emergency_contact_number", ""));

        return patientData;
    }

    // NEW METHOD: Save patient data to SharedPreferences
    private void savePatientDataToSharedPreferences(PatientData patientData) {
        android.content.SharedPreferences prefs = getSharedPreferences("PatientData", MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = prefs.edit();

        // Save basic patient information
        editor.putString("first_name", patientData.getFirstName());
        editor.putString("last_name", patientData.getLastName());
        editor.putString("date_of_birth", patientData.getDateOfBirth());
        editor.putString("sex", patientData.getGender());
        editor.putString("contact_number", patientData.getContactNumber());
        editor.putString("email", patientData.getEmail());
        editor.putString("address", patientData.getAddress());
        editor.putString("marital_status", patientData.getCivilStatus());
        editor.putString("barangay", patientData.getBarangay());

        // Save emergency contact information
        editor.putString("emergency_first_name", patientData.getEmergencyFirstName());
        editor.putString("emergency_last_name", patientData.getEmergencyLastName());
        editor.putString("emergency_relationship", patientData.getEmergencyRelationship());
        editor.putString("emergency_contact_number", patientData.getEmergencyContactNumber());

        // Save IDs and tracking number
        editor.putString("patient_id", String.valueOf(patientData.getPatientId()));
        editor.putString("resident_id", String.valueOf(patientData.getResidentId()));
        editor.putString("tracking_number", patientData.getTrackingNumber());

        // Mark that patient data exists
        editor.putBoolean("has_patient_data", true);

        editor.apply();
    }

    // Update the debug method to use API
    private void showAvailableTrackingNumbersForDebug() {
        ApiService apiService = new ApiService(this);
        apiService.getAllTrackingNumbers(new ApiService.ApiResponseListener() {
            @Override
            public void onSuccess(JSONObject response) {
                runOnUiThread(() -> {
                    try {
                        if (response.has("success") && response.getBoolean("success")) {
                            org.json.JSONArray trackingNumbers = response.getJSONArray("tracking_numbers");
                            List<String> numbers = new ArrayList<>();
                            for (int i = 0; i < trackingNumbers.length(); i++) {
                                numbers.add(trackingNumbers.getString(i));
                            }
                            String availableNumbers = "Available: " + String.join(", ", numbers);
                            Toast.makeText(DashboardActivity.this, availableNumbers, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e("DashboardActivity", "Error parsing tracking numbers", e);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Log.e("DashboardActivity", "API Error: " + error);

                    // More specific error messages for debug method too
                    if (error.contains("JSONException") || error.contains("ParseError")) {
                        Toast.makeText(DashboardActivity.this, "Server response error. Please try again.", Toast.LENGTH_LONG).show();
                    } else if (error.contains("Timeout") || error.contains("NoConnection")) {
                        Toast.makeText(DashboardActivity.this, "Network connection failed. Please check your internet.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(DashboardActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
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