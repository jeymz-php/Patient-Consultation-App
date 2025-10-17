package com.activity.communityhealthcare;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class PatientInformationActivity extends AppCompatActivity {

    private static final String TAG = "PatientInfoDebug";

    private TextInputEditText etFirstName, etLastName, etDateOfBirth, etHeight, etWeight;
    private TextInputEditText etContactNumber, etPatientAddress, etCity, etStateProvince;
    private TextInputEditText etEmergencyFirstName, etEmergencyLastName, etEmergencyContactNumber;
    private AutoCompleteTextView autoCompleteSex, autoCompleteMaritalStatus, autoCompleteBarangay;
    private AutoCompleteTextView autoCompleteRelationship, autoCompleteEmail;
    private MaterialButton btnSubmit;

    private boolean isSubmitting = false;

    // Dropdown options
    private final String[] sexOptions = {"Male", "Female", "Other"};
    private final String[] maritalStatusOptions = {"Single", "Married", "Divorced", "Widowed"};
    private final String[] barangayOptions = {"Barangay 100", "Barangay 96", "Barangay 97", "Barangay 98"};
    private final String[] relationshipOptions = {"Spouse", "Parent", "Child", "Sibling", "Relative", "Friend", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting PatientInformationActivity");

        // ✅ Check Data Privacy acceptance
        SharedPreferences prefs = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        boolean dataPrivacyAccepted = prefs.getBoolean("data_privacy_accepted", false);
        Log.d(TAG, "Data privacy accepted: " + dataPrivacyAccepted);

        if (!dataPrivacyAccepted) {
            Toast.makeText(this, "Please accept the Data Privacy policy first", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Data privacy not accepted. Finishing activity...");
            finish();
            return;
        }

        setSystemBars();
        setContentView(R.layout.activity_patient_information);
        setupWindowInsets();

        Log.d(TAG, "Layout loaded successfully");

        initializeViews();
        setupDropdowns();
        setupDatePicker();
        setupEmailAutoComplete();
        setupButtons();

        Log.d(TAG, "Activity setup complete");
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

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content),
                (v, insets) -> WindowInsetsCompat.CONSUMED);
    }

    private void initializeViews() {
        Log.d(TAG, "Initializing input fields...");
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        etContactNumber = findViewById(R.id.etContactNumber);
        etPatientAddress = findViewById(R.id.etPatientAddress);
        etCity = findViewById(R.id.etCity);
        etStateProvince = findViewById(R.id.etStateProvince);
        etEmergencyFirstName = findViewById(R.id.etEmergencyFirstName);
        etEmergencyLastName = findViewById(R.id.etEmergencyLastName);
        etEmergencyContactNumber = findViewById(R.id.etEmergencyContactNumber);
        autoCompleteSex = findViewById(R.id.autoCompleteSex);
        autoCompleteMaritalStatus = findViewById(R.id.autoCompleteMaritalStatus);
        autoCompleteBarangay = findViewById(R.id.autoCompleteBarangay);
        autoCompleteRelationship = findViewById(R.id.autoCompleteRelationship);
        autoCompleteEmail = findViewById(R.id.autoCompleteEmail);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private void setupDropdowns() {
        Log.d(TAG, "Setting up dropdowns...");
        autoCompleteSex.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sexOptions));
        autoCompleteMaritalStatus.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, maritalStatusOptions));
        autoCompleteBarangay.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, barangayOptions));
        autoCompleteRelationship.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, relationshipOptions));
    }

    private void setupDatePicker() {
        Log.d(TAG, "Configuring date picker...");
        etDateOfBirth.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, y, m, d) -> etDateOfBirth.setText((m + 1) + "/" + d + "/" + y),
                year, month, day
        );
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
        Log.d(TAG, "Date picker shown");
    }

    private void setupEmailAutoComplete() {
        autoCompleteEmail.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains("@")) showEmailSuggestions(s.toString());
            }
        });
    }

    private void showEmailSuggestions(String currentText) {
        String[] userPart = currentText.split("@");
        if (userPart.length > 0 && !userPart[0].isEmpty()) {
            String username = userPart[0];
            List<String> suggestions = Arrays.asList(
                    username + "@gmail.com",
                    username + "@yahoo.com",
                    username + "@outlook.com"
            );
            autoCompleteEmail.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, suggestions));
            autoCompleteEmail.showDropDown();
        }
    }

    private void setupButtons() {
        Log.d(TAG, "Setting up submit button...");
        btnSubmit.setOnClickListener(v -> {
            if (isSubmitting) {
                Log.w(TAG, "Prevented duplicate submission");
                return;
            }
            if (validateForm()) {
                isSubmitting = true;
                btnSubmit.setEnabled(false);
                btnSubmit.setText("Submitting...");
                savePatientData();
                submitToServer();
            } else {
                Log.w(TAG, "Form validation failed");
            }
        });
    }

    private void submitToServer() {
        String url = "http://192.168.100.2/communityhealthcare/app/register_patient.php";
        Log.d(TAG, "Submitting to: " + url);

        try {
            JSONObject data = new JSONObject();
            data.put("first_name", etFirstName.getText().toString().trim());
            data.put("last_name", etLastName.getText().toString().trim());
            data.put("date_of_birth", convertDateToMySQL(etDateOfBirth.getText().toString().trim()));
            data.put("sex", autoCompleteSex.getText().toString().trim());
            data.put("height", etHeight.getText().toString().trim());
            data.put("weight", etWeight.getText().toString().trim());
            data.put("marital_status", autoCompleteMaritalStatus.getText().toString().trim());
            data.put("contact_number", etContactNumber.getText().toString().trim());
            data.put("email", autoCompleteEmail.getText().toString().trim());
            data.put("barangay", autoCompleteBarangay.getText().toString().trim());
            data.put("address", etPatientAddress.getText().toString().trim());
            data.put("city", etCity.getText().toString().trim());
            data.put("state_province", etStateProvince.getText().toString().trim());
            data.put("emergency_first_name", etEmergencyFirstName.getText().toString().trim());
            data.put("emergency_last_name", etEmergencyLastName.getText().toString().trim());
            data.put("emergency_relationship", autoCompleteRelationship.getText().toString().trim());
            data.put("emergency_contact_number", etEmergencyContactNumber.getText().toString().trim());

            Log.d(TAG, "Data to send: " + data.toString());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, data,
                    response -> {
                        Log.d(TAG, "Server response: " + response);
                        isSubmitting = false;
                        btnSubmit.setEnabled(true);
                        btnSubmit.setText("Submit");

                        try {
                            if (response.getString("status").equals("success")) {
                                String trackingNumber = response.getString("tracking_number");
                                Log.d(TAG, "Registration success! Tracking #: " + trackingNumber);

                                SharedPreferences prefs = getSharedPreferences("PatientData", MODE_PRIVATE);
                                prefs.edit().putString("tracking_number", trackingNumber).apply();

                                Intent intent = new Intent(PatientInformationActivity.this, PatientInformationConfirmedActivity.class);
                                intent.putExtra("tracking_number", trackingNumber);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.e(TAG, "Server returned error: " + response.getString("message"));
                                Toast.makeText(this, response.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing server response: ", e);
                            Toast.makeText(this, "Error parsing server response.", Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> {
                        Log.e(TAG, "Network error: ", error);
                        isSubmitting = false;
                        btnSubmit.setEnabled(true);
                        btnSubmit.setText("Submit");
                        Toast.makeText(this, "Failed to connect to server.", Toast.LENGTH_LONG).show();
                    });

            request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                    0, 0, 1f
            ));

            com.android.volley.RequestQueue queue = com.android.volley.toolbox.Volley.newRequestQueue(this);
            queue.add(request);

        } catch (Exception e) {
            Log.e(TAG, "Unexpected error: ", e);
            isSubmitting = false;
            btnSubmit.setEnabled(true);
            btnSubmit.setText("Submit");
            Toast.makeText(this, "Unexpected error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateForm() {
        Log.d(TAG, "Validating form...");
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
        Log.d(TAG, "Form validated successfully");
        return true;
    }

    private void savePatientData() {
        Log.d(TAG, "Saving patient data locally...");
        SharedPreferences prefs = getSharedPreferences("PatientData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

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
        editor.putBoolean("has_patient_data", true);
        editor.apply();
    }

    private String convertDateToMySQL(String dateStr) {
        try {
            java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("M/d/yyyy");
            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            Log.e(TAG, "Date conversion error: ", e);
            return dateStr;
        }
    }
}
