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
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PatientInformationActivity extends AppCompatActivity {

    private static final String TAG = "PatientInfoDebug";

    private TextInputEditText etFirstName, etLastName, etDateOfBirth, etHeight, etWeight;
    private TextInputEditText etContactNumber, etPatientAddress, etCity, etStateProvince;
    private TextInputEditText etEmergencyFirstName, etEmergencyLastName, etEmergencyContactNumber;
    private AutoCompleteTextView autoCompleteSex, autoCompleteMaritalStatus, autoCompleteBarangay;
    private AutoCompleteTextView autoCompleteRelationship, autoCompleteEmail;
    private MaterialButton btnSubmit;

    private boolean isSubmitting = false;
    private boolean isEditing = false;
    private String patientId, trackingNumber;
    private boolean isActivityReady = false;

    // Dropdown options
    private final String[] sexOptions = {"Male", "Female", "Other"};
    private final String[] maritalStatusOptions = {"Single", "Married", "Divorced", "Widowed"};
    private final String[] relationshipOptions = {"Spouse", "Parent", "Child", "Sibling", "Relative", "Friend", "Other"};

    // Dynamic barangay data
    private List<Barangay> barangayList = new ArrayList<>();
    private Map<String, Integer> barangayMap = new HashMap<>(); // Map barangay name to ID

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

        // Load barangays from API first
        loadBarangaysFromAPI();

        // Check if we're in edit mode FIRST (before setting up email autocomplete)
        checkEditMode();

        // Setup email autocomplete AFTER loading existing data
        setupEmailAutoComplete();

        setupButtons();

        // Mark activity as ready for UI operations
        isActivityReady = true;

        Log.d(TAG, "Activity setup complete");
    }

    // Barangay data class
    private static class Barangay {
        int id;
        String name;

        Barangay(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private void loadBarangaysFromAPI() {
        String url = "https://communityhealthcare.bsitfoura.com/api/getBarangay.php";
        Log.d(TAG, "Loading barangays from: " + url);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d(TAG, "Barangays API response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("success")) {
                            JSONArray barangaysArray = jsonResponse.getJSONArray("barangays");
                            barangayList.clear();
                            barangayMap.clear();

                            for (int i = 0; i < barangaysArray.length(); i++) {
                                JSONObject barangayObj = barangaysArray.getJSONObject(i);
                                int id = barangayObj.getInt("barangay_id");
                                String name = barangayObj.getString("barangay_name");

                                barangayList.add(new Barangay(id, name));
                                barangayMap.put(name, id);
                            }

                            // Update the dropdown adapter
                            runOnUiThread(() -> {
                                ArrayAdapter<Barangay> adapter = new ArrayAdapter<>(
                                        PatientInformationActivity.this,
                                        android.R.layout.simple_dropdown_item_1line,
                                        barangayList
                                );
                                autoCompleteBarangay.setAdapter(adapter);
                                Log.d(TAG, "Loaded " + barangayList.size() + " barangays");
                            });

                        } else {
                            Log.e(TAG, "Failed to load barangays: " + jsonResponse.getString("message"));
                            showDefaultBarangays();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing barangays JSON: ", e);
                        showDefaultBarangays();
                    }
                },
                error -> {
                    Log.e(TAG, "Network error loading barangays: ", error);
                    showDefaultBarangays();
                });

        com.android.volley.RequestQueue queue = com.android.volley.toolbox.Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void showDefaultBarangays() {
        // Fallback to default barangays if API fails
        runOnUiThread(() -> {
            List<Barangay> defaultBarangays = Arrays.asList(
                    new Barangay(1, "Barangay 100"),
                    new Barangay(2, "Barangay 96"),
                    new Barangay(3, "Barangay 98"),
                    new Barangay(4, "Barangay 97")
            );

            barangayList.clear();
            barangayList.addAll(defaultBarangays);

            for (Barangay barangay : defaultBarangays) {
                barangayMap.put(barangay.name, barangay.id);
            }

            ArrayAdapter<Barangay> adapter = new ArrayAdapter<>(
                    PatientInformationActivity.this,
                    android.R.layout.simple_dropdown_item_1line,
                    barangayList
            );
            autoCompleteBarangay.setAdapter(adapter);
            Log.d(TAG, "Using default barangays due to API failure");
        });
    }

    private void checkEditMode() {
        Intent intent = getIntent();
        isEditing = intent.getBooleanExtra("isEditing", false);

        Log.d(TAG, "Edit mode: " + isEditing);

        if (isEditing) {
            Log.d(TAG, "Edit mode: Loading existing patient data");
            try {
                loadExistingPatientData();
                setupEditModeRestrictions(); // NEW: Set up field restrictions for edit mode
                btnSubmit.setText("Update Information");
                Log.d(TAG, "Edit mode setup completed successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error loading patient data for editing: ", e);
                Toast.makeText(this, "Error loading patient data", Toast.LENGTH_SHORT).show();
                finish(); // Close activity if data loading fails
            }
        } else {
            Log.d(TAG, "New patient mode: Registration form");
            btnSubmit.setText("Submit Information");
        }
    }

    // NEW METHOD: Set up field restrictions for edit mode
    private void setupEditModeRestrictions() {
        if (!isEditing) return;

        Log.d(TAG, "Setting up edit mode restrictions");

        // Make non-editable fields read-only and change their appearance
        setFieldReadOnly(etFirstName);
        setFieldReadOnly(etLastName);
        setFieldReadOnly(etDateOfBirth);
        setFieldReadOnly(autoCompleteSex);
        setFieldReadOnly(autoCompleteMaritalStatus);
        setFieldReadOnly(autoCompleteEmail);
        setFieldReadOnly(autoCompleteBarangay);
        setFieldReadOnly(etCity);
        setFieldReadOnly(etStateProvince);

        Log.d(TAG, "Edit mode restrictions applied");
    }

    // NEW METHOD: Helper method to make fields read-only
    private void setFieldReadOnly(TextInputEditText field) {
        field.setFocusable(false);
        field.setClickable(false);
        field.setFocusableInTouchMode(false);
        field.setBackgroundColor(Color.parseColor("#F5F5F5")); // Light gray background
    }

    // NEW METHOD: Helper method to make AutoCompleteTextView read-only
    private void setFieldReadOnly(AutoCompleteTextView field) {
        field.setFocusable(false);
        field.setClickable(false);
        field.setFocusableInTouchMode(false);
        field.setBackgroundColor(Color.parseColor("#F5F5F5")); // Light gray background
        field.setKeyListener(null); // Disable keyboard input
    }

    private void loadExistingPatientData() {
        SharedPreferences prefs = getSharedPreferences("PatientData", MODE_PRIVATE);

        // Load patient IDs with better error handling
        patientId = prefs.getString("patient_id", "");
        trackingNumber = prefs.getString("tracking_number", "");

        Log.d(TAG, "Loading patient data - ID: " + patientId + ", Tracking: " + trackingNumber);

        if (patientId.isEmpty() || trackingNumber.isEmpty()) {
            Log.e(TAG, "Missing patient ID or tracking number in edit mode");
            Toast.makeText(this, "Patient data not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // TEMPORARILY disable email text watcher to prevent auto-complete during data loading
        if (autoCompleteEmail != null) {
            autoCompleteEmail.removeTextChangedListener(getEmailTextWatcher());
        }

        // Load basic information
        etFirstName.setText(prefs.getString("first_name", ""));
        etLastName.setText(prefs.getString("last_name", ""));
        etDateOfBirth.setText(prefs.getString("date_of_birth", ""));

        String sex = prefs.getString("sex", "");
        if (!sex.isEmpty()) {
            autoCompleteSex.setText(sex, false);
        }

        etHeight.setText(prefs.getString("height", ""));
        etWeight.setText(prefs.getString("weight", ""));

        String maritalStatus = prefs.getString("marital_status", "");
        if (!maritalStatus.isEmpty()) {
            autoCompleteMaritalStatus.setText(maritalStatus, false);
        }

        etContactNumber.setText(prefs.getString("contact_number", ""));

        String email = prefs.getString("email", "");
        if (!email.isEmpty()) {
            autoCompleteEmail.setText(email, false);
        }

        String barangay = prefs.getString("barangay", "");
        if (!barangay.isEmpty()) {
            autoCompleteBarangay.setText(barangay, false);
        }

        etPatientAddress.setText(prefs.getString("address", ""));
        etCity.setText(prefs.getString("city", ""));
        etStateProvince.setText(prefs.getString("state_province", ""));

        // Load emergency contact information
        etEmergencyFirstName.setText(prefs.getString("emergency_first_name", ""));
        etEmergencyLastName.setText(prefs.getString("emergency_last_name", ""));

        String relationship = prefs.getString("emergency_relationship", "");
        if (!relationship.isEmpty()) {
            autoCompleteRelationship.setText(relationship, false);
        }

        etEmergencyContactNumber.setText(prefs.getString("emergency_contact_number", ""));

        // Re-enable email text watcher after data is loaded
        if (autoCompleteEmail != null) {
            autoCompleteEmail.addTextChangedListener(getEmailTextWatcher());
        }

        Log.d(TAG, "Loaded existing patient data for editing successfully");
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
        autoCompleteRelationship.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, relationshipOptions));

        // Barangay dropdown will be set after loading from API
        autoCompleteBarangay.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>()));
    }

    private void setupDatePicker() {
        Log.d(TAG, "Configuring date picker...");
        // Only enable date picker in new patient mode
        if (!isEditing) {
            etDateOfBirth.setOnClickListener(v -> showDatePickerDialog());
        }
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

    // NEW: Store text watcher as instance variable for easy removal
    private TextWatcher emailTextWatcher;

    private TextWatcher getEmailTextWatcher() {
        if (emailTextWatcher == null) {
            emailTextWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void afterTextChanged(Editable s) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Only show suggestions if activity is ready and user is actively typing
                    // Disable email suggestions in edit mode
                    if (!isEditing && isActivityReady && count > 0 && s.toString().contains("@")) {
                        showEmailSuggestions(s.toString());
                    }
                }
            };
        }
        return emailTextWatcher;
    }

    private void setupEmailAutoComplete() {
        autoCompleteEmail.addTextChangedListener(getEmailTextWatcher());
    }

    private void showEmailSuggestions(String currentText) {
        // Double-check that activity is ready before showing dropdown
        if (!isActivityReady || isFinishing() || isDestroyed()) {
            return;
        }

        try {
            String[] userPart = currentText.split("@");
            if (userPart.length > 0 && !userPart[0].isEmpty()) {
                String username = userPart[0];
                List<String> suggestions = Arrays.asList(
                        username + "@gmail.com",
                        username + "@yahoo.com",
                        username + "@outlook.com"
                );
                autoCompleteEmail.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, suggestions));

                // Only show dropdown if activity window is ready
                if (getWindow() != null && getWindow().getDecorView().getWindowToken() != null) {
                    autoCompleteEmail.showDropDown();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing email suggestions: ", e);
            // Don't crash the app if showing suggestions fails
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
                btnSubmit.setText(isEditing ? "Updating..." : "Submitting...");

                if (isEditing) {
                    updatePatientData(); // Call update instead of submit
                } else {
                    savePatientData();
                    submitToServer();
                }
            } else {
                Log.w(TAG, "Form validation failed");
            }
        });
    }

    // Generate tracking number: First Initial + Last Initial + DD + MM + YY
    private String generateTrackingNumber() {
        try {
            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String dateOfBirth = etDateOfBirth.getText().toString().trim();

            // Get first initial (uppercase)
            String firstInitial = firstName.isEmpty() ? "X" :
                    firstName.substring(0, 1).toUpperCase();

            // Get last initial (uppercase)
            String lastInitial = lastName.isEmpty() ? "X" :
                    lastName.substring(0, 1).toUpperCase();

            // Parse date components
            String day = "01";
            String month = "01";
            String year = "00";

            if (!dateOfBirth.isEmpty()) {
                try {
                    // Handle both MM/dd/yyyy and yyyy-MM-dd formats
                    if (dateOfBirth.contains("/")) {
                        // Format: MM/dd/yyyy
                        String[] dateParts = dateOfBirth.split("/");
                        if (dateParts.length >= 3) {
                            month = String.format("%02d", Integer.parseInt(dateParts[0]));
                            day = String.format("%02d", Integer.parseInt(dateParts[1]));
                            year = dateParts[2].length() >= 2 ?
                                    dateParts[2].substring(dateParts[2].length() - 2) : "00";
                        }
                    } else if (dateOfBirth.contains("-")) {
                        // Format: yyyy-MM-dd
                        String[] dateParts = dateOfBirth.split("-");
                        if (dateParts.length >= 3) {
                            year = dateParts[0].length() >= 2 ?
                                    dateParts[0].substring(dateParts[0].length() - 2) : "00";
                            month = String.format("%02d", Integer.parseInt(dateParts[1]));
                            day = String.format("%02d", Integer.parseInt(dateParts[2]));
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing date for tracking number: " + dateOfBirth, e);
                    // Use defaults if parsing fails
                }
            }

            String trackingNumber = firstInitial + lastInitial + day + month + year;
            Log.d(TAG, "Generated tracking number: " + trackingNumber +
                    " from First: " + firstName + ", Last: " + lastName + ", DOB: " + dateOfBirth);

            return trackingNumber;

        } catch (Exception e) {
            Log.e(TAG, "Error generating tracking number, using fallback", e);
            // Fallback: use random number if generation fails
            Random random = new Random();
            int randomNum = random.nextInt(900000) + 100000;
            return "TRK" + randomNum;
        }
    }

    // Get barangay ID based on selected barangay name
    private int getBarangayId(String barangayName) {
        Integer barangayId = barangayMap.get(barangayName);
        if (barangayId != null) {
            return barangayId;
        }
        Log.e(TAG, "Barangay not found in map: " + barangayName);

        // Try to find by object in the list
        for (Barangay barangay : barangayList) {
            if (barangay.name.equals(barangayName)) {
                return barangay.id;
            }
        }

        return 1; // Default barangay ID
    }

    // Update patient data method
    private void updatePatientData() {
        String url = "https://communityhealthcare.bsitfoura.com/api/update_patient.php";
        Log.d(TAG, "Updating patient data: " + url);

        try {
            String barangayName = autoCompleteBarangay.getText().toString().trim();
            int barangayId = getBarangayId(barangayName);

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        Log.d(TAG, "Update response: " + response);
                        isSubmitting = false;
                        btnSubmit.setEnabled(true);
                        btnSubmit.setText("Update Information");

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getBoolean("success")) {
                                // Update local SharedPreferences
                                savePatientData();

                                Log.d(TAG, "Patient information updated successfully!");
                                Toast.makeText(PatientInformationActivity.this, "Information updated successfully!", Toast.LENGTH_LONG).show();

                                // Go back to profile
                                Intent intent = new Intent(PatientInformationActivity.this, PatientProfileActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                finish();
                            } else {
                                String errorMessage = jsonResponse.getString("message");
                                Log.e(TAG, "Update failed: " + errorMessage);
                                Toast.makeText(PatientInformationActivity.this, "Update failed: " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing update response: ", e);
                            Toast.makeText(PatientInformationActivity.this, "Error updating information", Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> {
                        Log.e(TAG, "Network error during update: ", error);
                        isSubmitting = false;
                        btnSubmit.setEnabled(true);
                        btnSubmit.setText("Update Information");
                        Toast.makeText(PatientInformationActivity.this, "Network error during update", Toast.LENGTH_LONG).show();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    // Add patient identification
                    params.put("patient_id", patientId);
                    params.put("tracking_number", trackingNumber);

                    // Add ALL patient data
                    params.put("first_name", etFirstName.getText().toString().trim());
                    params.put("last_name", etLastName.getText().toString().trim());
                    params.put("date_of_birth", convertDateToMySQL(etDateOfBirth.getText().toString().trim()));
                    params.put("gender", autoCompleteSex.getText().toString().trim());
                    params.put("height", etHeight.getText().toString().trim());
                    params.put("weight", etWeight.getText().toString().trim());
                    params.put("civil_status", autoCompleteMaritalStatus.getText().toString().trim());
                    params.put("contact_number", etContactNumber.getText().toString().trim());
                    params.put("email", autoCompleteEmail.getText().toString().trim());
                    params.put("barangay_id", String.valueOf(barangayId));
                    params.put("address", etPatientAddress.getText().toString().trim());
                    params.put("city", etCity.getText().toString().trim());
                    params.put("state_province", etStateProvince.getText().toString().trim());
                    params.put("emergency_fname", etEmergencyFirstName.getText().toString().trim());
                    params.put("emergency_lname", etEmergencyLastName.getText().toString().trim());
                    params.put("emergency_relationship", autoCompleteRelationship.getText().toString().trim());
                    params.put("emergency_connum", etEmergencyContactNumber.getText().toString().trim());
                    params.put("medication_list", "");

                    Log.d(TAG, "Update params: " + params.toString());
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/x-www-form-urlencoded");
                    return headers;
                }
            };

            request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                    30000,
                    com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            com.android.volley.RequestQueue queue = com.android.volley.toolbox.Volley.newRequestQueue(this);
            queue.add(request);

        } catch (Exception e) {
            Log.e(TAG, "Unexpected error during update: ", e);
            isSubmitting = false;
            btnSubmit.setEnabled(true);
            btnSubmit.setText("Update Information");
            Toast.makeText(this, "Unexpected error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void submitToServer() {
        String url = "https://communityhealthcare.bsitfoura.com/api/addPatient.php";
        Log.d(TAG, "Submitting to: " + url);

        try {
            String trackingNum = generateTrackingNumber();
            String barangayName = autoCompleteBarangay.getText().toString().trim();
            int barangayId = getBarangayId(barangayName);

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        Log.d(TAG, "Server response: " + response);
                        isSubmitting = false;
                        btnSubmit.setEnabled(true);
                        btnSubmit.setText("Submit");

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getString("status").equals("success")) {
                                String trackingNumber = jsonResponse.getString("tracking_number");
                                Log.d(TAG, "Registration success! Tracking #: " + trackingNumber);

                                SharedPreferences prefs = getSharedPreferences("PatientData", MODE_PRIVATE);
                                prefs.edit().putString("tracking_number", trackingNumber).apply();

                                Intent intent = new Intent(PatientInformationActivity.this, PatientInformationConfirmedActivity.class);
                                intent.putExtra("tracking_number", trackingNumber);
                                startActivity(intent);
                                finish();
                            } else {
                                String errorMessage = jsonResponse.getString("message");
                                Log.e(TAG, "Server returned error: " + errorMessage);
                                Toast.makeText(PatientInformationActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing server response: ", e);
                            Toast.makeText(PatientInformationActivity.this, "Error parsing server response.", Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> {
                        Log.e(TAG, "Network error: ", error);
                        isSubmitting = false;
                        btnSubmit.setEnabled(true);
                        btnSubmit.setText("Submit");
                        Toast.makeText(PatientInformationActivity.this, "Failed to connect to server.", Toast.LENGTH_LONG).show();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("first_name", etFirstName.getText().toString().trim());
                    params.put("last_name", etLastName.getText().toString().trim());
                    params.put("date_of_birth", convertDateToMySQL(etDateOfBirth.getText().toString().trim()));
                    params.put("gender", autoCompleteSex.getText().toString().trim());
                    params.put("height", etHeight.getText().toString().trim());
                    params.put("weight", etWeight.getText().toString().trim());
                    params.put("civil_status", autoCompleteMaritalStatus.getText().toString().trim());
                    params.put("contact_number", etContactNumber.getText().toString().trim());
                    params.put("email", autoCompleteEmail.getText().toString().trim());
                    params.put("barangay_id", String.valueOf(barangayId));
                    params.put("address", etPatientAddress.getText().toString().trim());
                    params.put("city", etCity.getText().toString().trim());
                    params.put("state_province", etStateProvince.getText().toString().trim());
                    params.put("emergency_fname", etEmergencyFirstName.getText().toString().trim());
                    params.put("emergency_lname", etEmergencyLastName.getText().toString().trim());
                    params.put("emergency_relationship", autoCompleteRelationship.getText().toString().trim());
                    params.put("emergency_connum", etEmergencyContactNumber.getText().toString().trim());
                    params.put("tracking_number", trackingNum);
                    params.put("medication_list", "");

                    // Log the parameters being sent
                    Log.d(TAG, "Form params: " + params.toString());
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/x-www-form-urlencoded");
                    return headers;
                }
            };

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

        // Add debug logs for each field
        Log.d(TAG, "First Name: " + etFirstName.getText().toString().trim());
        Log.d(TAG, "Last Name: " + etLastName.getText().toString().trim());
        Log.d(TAG, "Email: " + autoCompleteEmail.getText().toString().trim());
        Log.d(TAG, "Barangay: " + autoCompleteBarangay.getText().toString().trim());

        // In edit mode, only validate editable fields
        if (isEditing) {
            // Only validate editable fields in edit mode
            if (etHeight.getText().toString().trim().isEmpty()) {
                etHeight.setError("Height is required");
                return false;
            }
            if (etWeight.getText().toString().trim().isEmpty()) {
                etWeight.setError("Weight is required");
                return false;
            }
            if (etContactNumber.getText().toString().trim().isEmpty()) {
                etContactNumber.setError("Contact number is required");
                return false;
            }
            if (etPatientAddress.getText().toString().trim().isEmpty()) {
                etPatientAddress.setError("Address is required");
                return false;
            }
            if (etEmergencyFirstName.getText().toString().trim().isEmpty()) {
                etEmergencyFirstName.setError("Emergency first name is required");
                return false;
            }
            if (etEmergencyLastName.getText().toString().trim().isEmpty()) {
                etEmergencyLastName.setError("Emergency last name is required");
                return false;
            }
            if (autoCompleteRelationship.getText().toString().trim().isEmpty()) {
                autoCompleteRelationship.setError("Emergency relationship is required");
                return false;
            }
            if (etEmergencyContactNumber.getText().toString().trim().isEmpty()) {
                etEmergencyContactNumber.setError("Emergency contact number is required");
                return false;
            }
        } else {
            // Full validation for new patient registration
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
            if (etHeight.getText().toString().trim().isEmpty()) {
                etHeight.setError("Height is required");
                return false;
            }
            if (etWeight.getText().toString().trim().isEmpty()) {
                etWeight.setError("Weight is required");
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
            if (autoCompleteBarangay.getText().toString().trim().isEmpty()) {
                autoCompleteBarangay.setError("Barangay is required");
                return false;
            }
            if (etPatientAddress.getText().toString().trim().isEmpty()) {
                etPatientAddress.setError("Address is required");
                return false;
            }
            if (etEmergencyFirstName.getText().toString().trim().isEmpty()) {
                etEmergencyFirstName.setError("Emergency first name is required");
                return false;
            }
            if (etEmergencyLastName.getText().toString().trim().isEmpty()) {
                etEmergencyLastName.setError("Emergency last name is required");
                return false;
            }
            if (autoCompleteRelationship.getText().toString().trim().isEmpty()) {
                autoCompleteRelationship.setError("Emergency relationship is required");
                return false;
            }
            if (etEmergencyContactNumber.getText().toString().trim().isEmpty()) {
                etEmergencyContactNumber.setError("Emergency contact number is required");
                return false;
            }
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

        // Also save patient_id and tracking_number if we're in edit mode
        if (isEditing) {
            editor.putString("patient_id", patientId);
            editor.putString("tracking_number", trackingNumber);
        }

        editor.apply();
    }

    private String convertDateToMySQL(String dateStr) {
        try {
            // If date is already in MySQL format (yyyy-MM-dd), return as is
            if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return dateStr;
            }

            // If date is in display format (M/d/yyyy), convert to MySQL format
            if (dateStr.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
                java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("M/d/yyyy");
                java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
                java.util.Date date = inputFormat.parse(dateStr);
                return outputFormat.format(date);
            }

            // Return original if format doesn't match expected patterns
            Log.w(TAG, "Unexpected date format: " + dateStr);
            return dateStr;
        } catch (Exception e) {
            Log.e(TAG, "Date conversion error for: " + dateStr, e);
            return dateStr;
        }
    }
}