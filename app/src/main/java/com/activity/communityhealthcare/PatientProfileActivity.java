package com.activity.communityhealthcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PatientProfileActivity extends AppCompatActivity {

    private static final String TAG = "PatientProfileDebug";

    private TextView txtName, txtBirthdate, txtSex, txtHeight, txtWeight, txtMaritalStatus;
    private TextView txtContact, txtEmail, txtBarangay, txtAddress, txtCity;
    private TextView txtEmergencyName, txtEmergencyRelation, txtEmergencyContact;
    private Button btnEdit, btnScheduleConsultation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);

        Log.d(TAG, "onCreate: Starting PatientProfileActivity");

        setSystemBars();
        initViews();
        loadPatientData();

        btnEdit.setOnClickListener(v -> {
            Log.d(TAG, "Edit button clicked");

            // Check if we have patient data
            SharedPreferences prefs = getSharedPreferences("PatientData", MODE_PRIVATE);
            boolean hasPatientData = prefs.getBoolean("has_patient_data", false);

            if (!hasPatientData) {
                Log.e(TAG, "No patient data found when trying to edit");
                return;
            }

            Intent intent = new Intent(PatientProfileActivity.this, PatientInformationActivity.class);
            intent.putExtra("isEditing", true);

            // Add debug logging
            Log.d(TAG, "Starting PatientInformationActivity in edit mode");

            startActivity(intent);
            // Don't finish() here if you want user to come back to profile after editing
        });

        btnScheduleConsultation.setOnClickListener(v -> {
            Log.d(TAG, "Schedule Consultation button clicked");

            // Navigate to DateSelectionActivity
            Intent intent = new Intent(PatientProfileActivity.this, DateSelectionActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Refreshing patient data");
        loadPatientData(); // Refresh data when returning from edit
    }

    private void setSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.parseColor("#C96A00"));
            window.setNavigationBarColor(Color.parseColor("#F8F9FA"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                View decor = window.getDecorView();
                int flags = decor.getSystemUiVisibility();
                flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                decor.setSystemUiVisibility(flags);
            }
        }
    }

    private void initViews() {
        txtName = findViewById(R.id.txtName);
        txtBirthdate = findViewById(R.id.txtBirthdate);
        txtSex = findViewById(R.id.txtSex);
        txtHeight = findViewById(R.id.txtHeight);
        txtWeight = findViewById(R.id.txtWeight);
        txtMaritalStatus = findViewById(R.id.txtMaritalStatus);
        txtContact = findViewById(R.id.txtContact);
        txtEmail = findViewById(R.id.txtEmail);
        txtBarangay = findViewById(R.id.txtBarangay);
        txtAddress = findViewById(R.id.txtAddress);
        txtCity = findViewById(R.id.txtCity);
        txtEmergencyName = findViewById(R.id.txtEmergencyName);
        txtEmergencyRelation = findViewById(R.id.txtEmergencyRelation);
        txtEmergencyContact = findViewById(R.id.txtEmergencyContact);

        btnEdit = findViewById(R.id.btnEdit);
        btnScheduleConsultation = findViewById(R.id.btnDashboard); // Using the same button ID from XML
    }

    private void loadPatientData() {
        SharedPreferences prefs = getSharedPreferences("PatientData", MODE_PRIVATE);

        String firstName = prefs.getString("first_name", "N/A");
        String lastName = prefs.getString("last_name", "N/A");
        String patientId = prefs.getString("patient_id", "N/A");
        String trackingNumber = prefs.getString("tracking_number", "N/A");

        Log.d(TAG, "Loading patient data - ID: " + patientId + ", Tracking: " + trackingNumber);

        txtName.setText(firstName + " " + lastName);
        txtBirthdate.setText(prefs.getString("date_of_birth", "N/A"));
        txtSex.setText(prefs.getString("sex", "N/A"));
        txtHeight.setText(prefs.getString("height", "N/A") + " cm");
        txtWeight.setText(prefs.getString("weight", "N/A") + " kg");
        txtMaritalStatus.setText(prefs.getString("marital_status", "N/A"));
        txtContact.setText(prefs.getString("contact_number", "N/A"));
        txtEmail.setText(prefs.getString("email", "N/A"));
        txtBarangay.setText(prefs.getString("barangay", "N/A"));
        txtAddress.setText(prefs.getString("address", "N/A"));
        txtCity.setText(prefs.getString("city", "N/A"));
        txtEmergencyName.setText(
                prefs.getString("emergency_first_name", "N/A") + " " +
                        prefs.getString("emergency_last_name", "")
        );
        txtEmergencyRelation.setText(prefs.getString("emergency_relationship", "N/A"));
        txtEmergencyContact.setText(prefs.getString("emergency_contact_number", "N/A"));
    }
}