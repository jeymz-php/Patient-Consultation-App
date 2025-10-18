package com.activity.communityhealthcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

public class ConfirmationSchedConsultActivity extends AppCompatActivity {

    private TextView tvDoctorName, tvDoctorSpecialty, tvDate, tvTime;
    private MaterialButton btnConfirm, btnBack;

    private String doctorName, doctorSpecialty, selectedDate, selectedTime, selectedDateDisplay;
    private String patientId, trackingNumber;

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
        tvDoctorName = findViewById(R.id.tvDoctorName);
        tvDoctorSpecialty = findViewById(R.id.tvDoctorSpecialty);
        tvDate = findViewById(R.id.tvSelectedDate);
        tvTime = findViewById(R.id.tvSelectedTime);
        btnConfirm = findViewById(R.id.btnConfirmSchedule);
        btnBack = findViewById(R.id.btnBack);
    }

    private void loadScheduleData() {
        SharedPreferences prefs = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        doctorName = prefs.getString("selected_doctor_name", "Dr. Maria Santos");
        doctorSpecialty = prefs.getString("selected_doctor_specialty", "General Medicine");
        selectedDate = prefs.getString("selected_date", ""); // yyyy-MM-dd format
        selectedDateDisplay = prefs.getString("selected_date_display", "Today");
        selectedTime = prefs.getString("selected_time", "8:00 AM");

        tvDoctorName.setText(doctorName);
        tvDoctorSpecialty.setText(doctorSpecialty);
        tvDate.setText(selectedDateDisplay);
        tvTime.setText(selectedTime);
    }

    private void loadPatientData() {
        SharedPreferences patientPrefs = getSharedPreferences("PatientData", MODE_PRIVATE);
        patientId = patientPrefs.getString("patient_id", "");
        trackingNumber = patientPrefs.getString("tracking_number", "");
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            finish(); // go back to time selection
        });

        btnConfirm.setOnClickListener(v -> {
            // Show loading state
            btnConfirm.setEnabled(false);
            btnConfirm.setText("Scheduling...");

            // Save appointment to database via API
            saveAppointmentToDatabase();
        });
    }

    private void saveAppointmentToDatabase() {
        ApiService apiService = new ApiService(this);

        // Convert display date back to database format if needed
        String dbDate = selectedDate; // Already in yyyy-MM-dd format from DateSelectionActivity

        apiService.saveAppointment(patientId, trackingNumber, doctorName, doctorSpecialty, dbDate, selectedTime,
                new ApiService.ApiResponseListener() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        runOnUiThread(() -> {
                            try {
                                if (response.getBoolean("success")) {
                                    String appointmentId = response.getString("appointment_id");
                                    Toast.makeText(ConfirmationSchedConsultActivity.this,
                                            "Consultation scheduled successfully!", Toast.LENGTH_LONG).show();

                                    // Clear the selected data from SharedPreferences
                                    clearSelectionData();

                                    // Proceed to dashboard
                                    Intent intent = new Intent(ConfirmationSchedConsultActivity.this, DashboardActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    String errorMessage = response.getString("message");
                                    Toast.makeText(ConfirmationSchedConsultActivity.this,
                                            "Failed to schedule: " + errorMessage, Toast.LENGTH_LONG).show();
                                    btnConfirm.setEnabled(true);
                                    btnConfirm.setText("Confirm Schedule");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(ConfirmationSchedConsultActivity.this,
                                        "Error processing response", Toast.LENGTH_LONG).show();
                                btnConfirm.setEnabled(true);
                                btnConfirm.setText("Confirm Schedule");
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            Toast.makeText(ConfirmationSchedConsultActivity.this,
                                    "Network error: " + error, Toast.LENGTH_LONG).show();
                            btnConfirm.setEnabled(true);
                            btnConfirm.setText("Confirm Schedule");
                        });
                    }
                });
    }

    private void clearSelectionData() {
        SharedPreferences prefs = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("selected_doctor_id");
        editor.remove("selected_doctor_name");
        editor.remove("selected_doctor_specialty");
        editor.remove("selected_date");
        editor.remove("selected_date_display");
        editor.remove("selected_time");
        editor.apply();
    }
}