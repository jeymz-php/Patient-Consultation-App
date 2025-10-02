package com.example.patientinformationandonlineconsultationsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PatientProfileActivity extends AppCompatActivity {

    private TextView tvPatientName, tvDob, tvGender, tvHeight, tvWeight;
    private TextView tvCivilStatus, tvContact, tvEmail, tvAddress;
    private TextView tvMedications, tvEmergencyContact;
    private Button btnScheduleConsultation, btnEditProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);

        initializeViews();
        loadPatientData();
        setupButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data when returning to this activity
        loadPatientData();
    }

    private void initializeViews() {
        tvPatientName = findViewById(R.id.tvPatientName);
        tvDob = findViewById(R.id.tvDob);
        tvGender = findViewById(R.id.tvGender);
        tvHeight = findViewById(R.id.tvHeight);
        tvWeight = findViewById(R.id.tvWeight);
        tvCivilStatus = findViewById(R.id.tvCivilStatus);
        tvContact = findViewById(R.id.tvContact);
        tvEmail = findViewById(R.id.tvEmail);
        tvAddress = findViewById(R.id.tvAddress);
        tvMedications = findViewById(R.id.tvMedications);
        tvEmergencyContact = findViewById(R.id.tvEmergencyContact);

        btnScheduleConsultation = findViewById(R.id.btnScheduleConsultation);
        btnEditProfile = findViewById(R.id.btnEditProfile);
    }

    private void loadPatientData() {
        SharedPreferences prefs = getSharedPreferences("PatientData", MODE_PRIVATE);

        // Load and display patient information
        String firstName = prefs.getString("first_name", "");
        String middleName = prefs.getString("middle_name", "");
        String lastName = prefs.getString("last_name", "");
        String fullName = firstName + " " + middleName + " " + lastName;

        tvPatientName.setText(fullName);
        tvDob.setText(prefs.getString("date_of_birth", "N/A"));
        tvGender.setText(prefs.getString("gender", "N/A"));
        tvHeight.setText(prefs.getString("height", "N/A") + " inches");
        tvWeight.setText(prefs.getString("weight", "N/A") + " kg");
        tvCivilStatus.setText(prefs.getString("civil_status", "N/A"));
        tvContact.setText(prefs.getString("contact_number", "N/A"));
        tvEmail.setText(prefs.getString("email", "N/A"));
        tvAddress.setText(prefs.getString("address", "N/A"));

        // Medications
        String takingMeds = prefs.getString("taking_medications", "No");
        String medList = prefs.getString("medication_list", "None");
        if (takingMeds.equals("Yes")) {
            tvMedications.setText("Yes - " + medList);
        } else {
            tvMedications.setText("No current medications");
        }

        // Emergency contact
        String emergencyName = prefs.getString("emergency_name", "");
        String emergencyRelation = prefs.getString("emergency_relationship", "");
        String emergencyNumber = prefs.getString("emergency_contact", "");
        String emergencyInfo = emergencyName + " (" + emergencyRelation + ")\n" + emergencyNumber;
        tvEmergencyContact.setText(emergencyInfo);
    }

    private void setupButtons() {
        btnScheduleConsultation.setOnClickListener(v -> {
            // Navigate to Doctors List for scheduling consultation
            Intent intent = new Intent(PatientProfileActivity.this, DoctorsListActivity.class);
            startActivity(intent);
        });

        btnEditProfile.setOnClickListener(v -> {
            // Go back to edit patient information with edit mode flag
            Intent intent = new Intent(PatientProfileActivity.this, PatientInformationActivity.class);
            intent.putExtra("EDIT_MODE", true);
            startActivity(intent);
        });
    }
}