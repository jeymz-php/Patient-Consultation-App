package com.example.patientinformationandonlineconsultationsystem;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PatientInformationActivity extends AppCompatActivity {

    // Form fields
    private EditText etFirstName, etMiddleName, etLastName, etDob, etHeight, etWeight;
    private EditText etContactNumber, etEmail, etAddress, etMedicationList;
    private EditText etEmergencyName, etEmergencyContactNumber;
    private Spinner spinnerSex, spinnerMaritalStatus, spinnerRelationship;
    private RadioGroup radioMedications;
    private RadioButton radioYes, radioNo;
    private Button btnSubmit;

    // API URL - Change this to your server URL
    private static final String API_URL = "http://192.168.100.2/patient-consultation-mobile/save_patient.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_information);

        // Set status bar color to match header
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(android.graphics.Color.parseColor("#E87C00"));
        }

        initializeViews();
        setupDatePicker();
        setupMedicationToggle();
        setupSubmitButton();
    }

    private void initializeViews() {
        // Name fields
        etFirstName = findViewById(R.id.etFirstName);
        etMiddleName = findViewById(R.id.etMiddleName);
        etLastName = findViewById(R.id.etLastName);

        // Personal info
        etDob = findViewById(R.id.etDob);
        spinnerSex = findViewById(R.id.spinnerSex);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        spinnerMaritalStatus = findViewById(R.id.spinnerMaritalStatus);

        // Contact info
        etContactNumber = findViewById(R.id.etContactNumber);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);

        // Medications
        radioMedications = findViewById(R.id.radioMedications);
        radioYes = findViewById(R.id.radioYes);
        radioNo = findViewById(R.id.radioNo);
        etMedicationList = findViewById(R.id.etMedicationList);

        // Emergency contact
        etEmergencyName = findViewById(R.id.etEmergencyName);
        spinnerRelationship = findViewById(R.id.spinnerRelationship);
        etEmergencyContactNumber = findViewById(R.id.etEmergencyContactNumber);

        // Submit button
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private void setupDatePicker() {
        etDob.setFocusable(false);
        etDob.setClickable(true);

        etDob.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    PatientInformationActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = String.format("%02d/%02d/%d",
                                selectedMonth + 1, selectedDay, selectedYear);
                        etDob.setText(date);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });
    }

    private void setupMedicationToggle() {
        etMedicationList.setVisibility(View.GONE);

        radioMedications.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioYes) {
                etMedicationList.setVisibility(View.VISIBLE);
            } else {
                etMedicationList.setVisibility(View.GONE);
                etMedicationList.setText("");
            }
        });
    }

    private void setupSubmitButton() {
        btnSubmit.setOnClickListener(v -> {
            if (validateForm()) {
                submitPatientData();
            }
        });
    }

    private boolean validateForm() {
        // Validate First Name
        if (TextUtils.isEmpty(etFirstName.getText().toString().trim())) {
            etFirstName.setError("First name is required");
            etFirstName.requestFocus();
            return false;
        }

        // Validate Last Name
        if (TextUtils.isEmpty(etLastName.getText().toString().trim())) {
            etLastName.setError("Last name is required");
            etLastName.requestFocus();
            return false;
        }

        // Validate Date of Birth
        if (TextUtils.isEmpty(etDob.getText().toString().trim())) {
            etDob.setError("Date of birth is required");
            etDob.requestFocus();
            return false;
        }

        // Validate Height
        if (TextUtils.isEmpty(etHeight.getText().toString().trim())) {
            etHeight.setError("Height is required");
            etHeight.requestFocus();
            return false;
        }

        // Validate Weight
        if (TextUtils.isEmpty(etWeight.getText().toString().trim())) {
            etWeight.setError("Weight is required");
            etWeight.requestFocus();
            return false;
        }

        // Validate Contact Number
        String contact = etContactNumber.getText().toString().trim();
        if (TextUtils.isEmpty(contact)) {
            etContactNumber.setError("Contact number is required");
            etContactNumber.requestFocus();
            return false;
        }
        if (contact.length() != 11) {
            etContactNumber.setError("Contact number must be 11 digits");
            etContactNumber.requestFocus();
            return false;
        }

        // Validate Email
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
            etEmail.requestFocus();
            return false;
        }

        // Validate Address
        if (TextUtils.isEmpty(etAddress.getText().toString().trim())) {
            etAddress.setError("Address is required");
            etAddress.requestFocus();
            return false;
        }

        // Validate Emergency Contact Name
        if (TextUtils.isEmpty(etEmergencyName.getText().toString().trim())) {
            etEmergencyName.setError("Emergency contact name is required");
            etEmergencyName.requestFocus();
            return false;
        }

        // Validate Emergency Contact Number
        String emergencyContact = etEmergencyContactNumber.getText().toString().trim();
        if (TextUtils.isEmpty(emergencyContact)) {
            etEmergencyContactNumber.setError("Emergency contact number is required");
            etEmergencyContactNumber.requestFocus();
            return false;
        }
        if (emergencyContact.length() != 11) {
            etEmergencyContactNumber.setError("Emergency contact must be 11 digits");
            etEmergencyContactNumber.requestFocus();
            return false;
        }

        return true;
    }

    private void submitPatientData() {
        // Show loading
        btnSubmit.setEnabled(false);
        btnSubmit.setText("Submitting...");

        // Save data locally first
        savePatientDataLocally();

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_URL,
                response -> {
                    // Success
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Submit");

                    Toast.makeText(PatientInformationActivity.this,
                            "Patient information saved successfully!", Toast.LENGTH_LONG).show();

                    // Navigate to Patient Profile View
                    Intent intent = new Intent(PatientInformationActivity.this, PatientProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    // Error
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Submit");

                    Toast.makeText(PatientInformationActivity.this,
                            "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                // Personal Information
                params.put("first_name", etFirstName.getText().toString().trim());
                params.put("middle_name", etMiddleName.getText().toString().trim());
                params.put("last_name", etLastName.getText().toString().trim());
                params.put("date_of_birth", etDob.getText().toString().trim());
                params.put("gender", spinnerSex.getSelectedItem().toString());
                params.put("height", etHeight.getText().toString().trim());
                params.put("weight", etWeight.getText().toString().trim());
                params.put("civil_status", spinnerMaritalStatus.getSelectedItem().toString());

                // Contact Information
                params.put("contact_number", etContactNumber.getText().toString().trim());
                params.put("email", etEmail.getText().toString().trim());
                params.put("address", etAddress.getText().toString().trim());

                // Medications
                int selectedMedId = radioMedications.getCheckedRadioButtonId();
                String takingMedications = selectedMedId == R.id.radioYes ? "Yes" : "No";
                params.put("taking_medications", takingMedications);
                params.put("medication_list", etMedicationList.getText().toString().trim());

                // Emergency Contact
                params.put("emergency_name", etEmergencyName.getText().toString().trim());
                params.put("emergency_relationship", spinnerRelationship.getSelectedItem().toString());
                params.put("emergency_contact", etEmergencyContactNumber.getText().toString().trim());

                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void savePatientDataLocally() {
        SharedPreferences prefs = getSharedPreferences("PatientData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Save all patient information
        editor.putString("first_name", etFirstName.getText().toString().trim());
        editor.putString("middle_name", etMiddleName.getText().toString().trim());
        editor.putString("last_name", etLastName.getText().toString().trim());
        editor.putString("date_of_birth", etDob.getText().toString().trim());
        editor.putString("gender", spinnerSex.getSelectedItem().toString());
        editor.putString("height", etHeight.getText().toString().trim());
        editor.putString("weight", etWeight.getText().toString().trim());
        editor.putString("civil_status", spinnerMaritalStatus.getSelectedItem().toString());
        editor.putString("contact_number", etContactNumber.getText().toString().trim());
        editor.putString("email", etEmail.getText().toString().trim());
        editor.putString("address", etAddress.getText().toString().trim());

        // Medications
        int selectedMedId = radioMedications.getCheckedRadioButtonId();
        String takingMedications = selectedMedId == R.id.radioYes ? "Yes" : "No";
        editor.putString("taking_medications", takingMedications);
        editor.putString("medication_list", etMedicationList.getText().toString().trim());

        // Emergency contact
        editor.putString("emergency_name", etEmergencyName.getText().toString().trim());
        editor.putString("emergency_relationship", spinnerRelationship.getSelectedItem().toString());
        editor.putString("emergency_contact", etEmergencyContactNumber.getText().toString().trim());

        // Mark that patient data exists
        editor.putBoolean("has_patient_data", true);

        editor.apply();
    }
}