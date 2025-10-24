package com.activity.communityhealthcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class ConfirmationSchedConsultActivity extends AppCompatActivity {

    private TextView tvDate, tvTime;
    private MaterialButton btnConfirm, btnBack;

    private String selectedDate, selectedTime, selectedDateDisplay;
    private String trackingNumber;
    private String patientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSystemBars();
        setContentView(R.layout.activity_confirmation_sched_consult);

        initializeViews();
        loadScheduleData();
        loadPatientData();
        setupListeners();
    }

    private void setSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.parseColor("#C96A00"));
            window.setNavigationBarColor(Color.parseColor("#F8F9FA"));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View decor = window.getDecorView();
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                View decor = window.getDecorView();
                int flags = decor.getSystemUiVisibility();
                flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                decor.setSystemUiVisibility(flags);
            }
        }
    }

    private void initializeViews() {
        tvDate = findViewById(R.id.tvSelectedDate);
        tvTime = findViewById(R.id.tvSelectedTime);
        btnConfirm = findViewById(R.id.btnConfirmSchedule);
        btnBack = findViewById(R.id.btnBack);
    }

    private void loadScheduleData() {
        SharedPreferences prefs = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        selectedDate = prefs.getString("selected_date", ""); // yyyy-MM-dd format
        selectedDateDisplay = prefs.getString("selected_date_display", "Today");
        selectedTime = prefs.getString("selected_time", "8:00 AM");

        tvDate.setText(selectedDateDisplay);
        tvTime.setText(selectedTime);
    }

    private void loadPatientData() {
        SharedPreferences patientPrefs = getSharedPreferences("PatientData", MODE_PRIVATE);
        trackingNumber = patientPrefs.getString("tracking_number", "");

        // Debug: Print all PatientData entries
        Map<String, ?> allEntries = patientPrefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("PatientDataDebug", "Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }

        // Get patient_id - ONLY as String since that's how it's stored
        patientId = patientPrefs.getString("patient_id", "");

        // If patient_id is "0", try to use resident_id instead
        if (patientId.isEmpty() || patientId.equals("0")) {
            Log.d("PatientDataDebug", "patient_id is 0, trying resident_id...");

            // Try resident_id as String first
            String residentId = patientPrefs.getString("resident_id", "");
            if (!residentId.isEmpty() && !residentId.equals("0")) {
                patientId = residentId;
                Log.d("PatientDataDebug", "Using resident_id (string): " + patientId);
            } else {
                // If resident_id is stored as int, get it as int and convert to string
                int residentIdInt = patientPrefs.getInt("resident_id", 0);
                if (residentIdInt != 0) {
                    patientId = String.valueOf(residentIdInt);
                    Log.d("PatientDataDebug", "Using resident_id (int): " + patientId);
                } else {
                    Log.e("PatientDataDebug", "No valid patient_id or resident_id found!");
                    patientId = "0"; // Fallback to prevent null
                }
            }
        }

        Log.d("PatientDataDebug", "Final patientId: " + patientId);
        Log.d("PatientDataDebug", "Tracking Number: " + trackingNumber);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> {
            btnConfirm.setEnabled(false);
            btnConfirm.setText("Scheduling...");
            saveAppointmentToDatabase();
        });
    }

    private void saveAppointmentToDatabase() {
        ApiService apiService = new ApiService(this);
        String dbDate = selectedDate;

        // Debug what we're about to send
        Log.d("AppointmentDebug", "Sending appointment with:");
        Log.d("AppointmentDebug", "tracking_number: " + trackingNumber);
        Log.d("AppointmentDebug", "appointment_date: " + dbDate);
        Log.d("AppointmentDebug", "appointment_time: " + selectedTime);

        apiService.saveAppointment(
                trackingNumber,
                dbDate,
                selectedTime,
                new ApiService.ApiResponseListener() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        btnConfirm.setEnabled(true);
                        btnConfirm.setText("Confirm Schedule");

                        try {
                            Log.d("AppointmentDebug", "Response: " + response.toString());
                            if (response.getString("status").equals("success")) {
                                Toast.makeText(ConfirmationSchedConsultActivity.this,
                                        "Appointment successfully saved!", Toast.LENGTH_SHORT).show();
                                clearSelectionData();

                                // ✅ Navigate to DashboardActivity after successful appointment
                                Intent intent = new Intent(ConfirmationSchedConsultActivity.this, DashboardActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                finish(); // Close current activity

                            } else {
                                String errorMsg = response.getString("message");
                                Toast.makeText(ConfirmationSchedConsultActivity.this,
                                        "Error: " + errorMsg, Toast.LENGTH_SHORT).show();
                                Log.e("AppointmentDebug", "Server error: " + errorMsg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ConfirmationSchedConsultActivity.this,
                                    "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        btnConfirm.setEnabled(true);
                        btnConfirm.setText("Confirm Schedule");
                        Toast.makeText(ConfirmationSchedConsultActivity.this,
                                "Network Error: " + error, Toast.LENGTH_SHORT).show();
                        Log.e("AppointmentDebug", "Network error: " + error);
                    }
                }
        );
    }

    private void clearSelectionData() {
        SharedPreferences prefs = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("selected_date");
        editor.remove("selected_date_display");
        editor.remove("selected_time");
        editor.apply();
    }
}
