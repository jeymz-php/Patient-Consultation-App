package com.activity.communityhealthcare;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;
import java.util.Arrays;
import java.util.List;

public class PatientInformationActivity extends AppCompatActivity {

    private TextInputEditText etFirstName, etLastName, etDateOfBirth, etHeight, etWeight;
    private TextInputEditText etContactNumber, etPatientAddress, etCity, etStateProvince;
    private TextInputEditText etEmergencyFirstName, etEmergencyLastName, etEmergencyContactNumber;
    private AutoCompleteTextView autoCompleteSex, autoCompleteMaritalStatus, autoCompleteBarangay;
    private AutoCompleteTextView autoCompleteRelationship, autoCompleteEmail;
    private MaterialButton btnSubmit;

    // Dropdown options
    private final String[] sexOptions = {"Male", "Female", "Other"};
    private final String[] maritalStatusOptions = {"Single", "Married", "Divorced", "Widowed"};
    private final String[] barangayOptions = {"Barangay 100", "Barangay 96", "Barangay 97", "Barangay 98"};
    private final String[] relationshipOptions = {"Spouse", "Parent", "Child", "Sibling", "Relative", "Friend", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user has accepted data privacy
        SharedPreferences prefs = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        boolean dataPrivacyAccepted = prefs.getBoolean("data_privacy_accepted", false);

        if (!dataPrivacyAccepted) {
            // If not accepted, go back to dashboard
            Toast.makeText(this, "Please accept the Data Privacy policy first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set system bars (status bar and navigation bar)
        setSystemBars();

        setContentView(R.layout.activity_patient_information);

        // Handle window insets for gesture navigation
        setupWindowInsets();

        initializeViews();
        setupDropdowns();
        setupDatePicker();
        setupEmailAutoComplete();
        setupSubmitButton();
    }

    private void setSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();

            // Set status bar color
            window.setStatusBarColor(Color.parseColor("#C96A00"));

            // Set navigation bar color to match background
            window.setNavigationBarColor(Color.parseColor("#F8F9FA"));

            // For Android M and above - set dark status bar icons
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View decor = window.getDecorView();
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }

            // For Android O and above - set light navigation bar icons
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                View decor = window.getDecorView();
                int flags = decor.getSystemUiVisibility();
                flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                decor.setSystemUiVisibility(flags);
            }
        }
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            // Handle system bars insets
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;

            // The XML already has padding, but you can programmatically adjust if necessary
            // For example, if you need to adjust specific views

            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void initializeViews() {
        // Personal Information
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        etContactNumber = findViewById(R.id.etContactNumber);
        etPatientAddress = findViewById(R.id.etPatientAddress);
        etCity = findViewById(R.id.etCity);
        etStateProvince = findViewById(R.id.etStateProvince);

        // Emergency Contact
        etEmergencyFirstName = findViewById(R.id.etEmergencyFirstName);
        etEmergencyLastName = findViewById(R.id.etEmergencyLastName);
        etEmergencyContactNumber = findViewById(R.id.etEmergencyContactNumber);

        // Dropdowns
        autoCompleteSex = findViewById(R.id.autoCompleteSex);
        autoCompleteMaritalStatus = findViewById(R.id.autoCompleteMaritalStatus);
        autoCompleteBarangay = findViewById(R.id.autoCompleteBarangay);
        autoCompleteRelationship = findViewById(R.id.autoCompleteRelationship);
        autoCompleteEmail = findViewById(R.id.autoCompleteEmail);

        // Button
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    // Rest of your methods remain the same...
    private void setupDropdowns() {
        // Sex dropdown
        ArrayAdapter<String> sexAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sexOptions);
        autoCompleteSex.setAdapter(sexAdapter);

        // Marital Status dropdown
        ArrayAdapter<String> maritalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, maritalStatusOptions);
        autoCompleteMaritalStatus.setAdapter(maritalAdapter);

        // Barangay dropdown
        ArrayAdapter<String> barangayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, barangayOptions);
        autoCompleteBarangay.setAdapter(barangayAdapter);

        // Relationship dropdown
        ArrayAdapter<String> relationshipAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, relationshipOptions);
        autoCompleteRelationship.setAdapter(relationshipAdapter);
    }

    private void setupDatePicker() {
        etDateOfBirth.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = (selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear;
                    etDateOfBirth.setText(selectedDate);
                },
                year, month, day
        );

        // Set maximum date to today
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void setupEmailAutoComplete() {
        autoCompleteEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String currentText = s.toString();
                if (currentText.contains("@")) {
                    showEmailSuggestions(currentText);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void showEmailSuggestions(String currentText) {
        String[] userPart = currentText.split("@");
        if (userPart.length > 0 && userPart[0].length() > 0) {
            String username = userPart[0];
            List<String> suggestions = Arrays.asList(
                    username + "@gmail.com",
                    username + "@yahoo.com",
                    username + "@outlook.com"
            );

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, suggestions);

            // Show dropdown - Now this will work because autoCompleteEmail is AutoCompleteTextView
            autoCompleteEmail.setAdapter(adapter);
            autoCompleteEmail.showDropDown();
        }
    }

    private void setupSubmitButton() {
        btnSubmit.setOnClickListener(v -> {
            if (validateForm()) {
                savePatientData();
                Toast.makeText(this, "Patient information saved successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private boolean validateForm() {
        // Basic validation - you can expand this
        if (etFirstName.getText().toString().trim().isEmpty()) {
            etFirstName.setError("First name is required");
            return false;
        }
        if (etLastName.getText().toString().trim().isEmpty()) {
            etLastName.setError("Last name is required");
            return false;
        }
        if (etDateOfBirth.getText().toString().trim().isEmpty()) {
            etDateOfBirth.setError("Date of birth is required");
            return false;
        }
        if (autoCompleteSex.getText().toString().trim().isEmpty()) {
            autoCompleteSex.setError("Sex is required");
            return false;
        }
        if (etContactNumber.getText().toString().trim().isEmpty()) {
            etContactNumber.setError("Contact number is required");
            return false;
        }
        if (autoCompleteEmail.getText().toString().trim().isEmpty()) {
            autoCompleteEmail.setError("Email is required");
            return false;
        }
        return true;
    }

    private void savePatientData() {
        SharedPreferences prefs = getSharedPreferences("PatientData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Save all patient data
        editor.putString("first_name", etFirstName.getText().toString().trim());
        editor.putString("last_name", etLastName.getText().toString().trim());
        editor.putString("date_of_birth", etDateOfBirth.getText().toString().trim());
        editor.putString("sex", autoCompleteSex.getText().toString().trim());
        editor.putString("height", etHeight.getText().toString().trim());
        editor.putString("weight", etWeight.getText().toString().trim());
        editor.putString("marital_status", autoCompleteMaritalStatus.getText().toString().trim());
        editor.putString("contact_number", etContactNumber.getText().toString().trim());
        editor.putString("email", autoCompleteEmail.getText().toString().trim());
        editor.putString("barangay", autoCompleteBarangay.getText().toString().trim());
        editor.putString("address", etPatientAddress.getText().toString().trim());
        editor.putString("city", etCity.getText().toString().trim());
        editor.putString("state_province", etStateProvince.getText().toString().trim());
        editor.putString("emergency_first_name", etEmergencyFirstName.getText().toString().trim());
        editor.putString("emergency_last_name", etEmergencyLastName.getText().toString().trim());
        editor.putString("emergency_relationship", autoCompleteRelationship.getText().toString().trim());
        editor.putString("emergency_contact_number", etEmergencyContactNumber.getText().toString().trim());

        // Mark that patient data exists
        editor.putBoolean("has_patient_data", true);

        editor.apply();
    }
}