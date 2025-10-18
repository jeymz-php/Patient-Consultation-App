package com.activity.communityhealthcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ConsultationLogsActivity extends AppCompatActivity {

    private RecyclerView rvConsultations;
    private EditText etSearchConsultation;
    private TextView tvTrackingNumber;
    private LinearLayout emptyState;
    private ConsultationAdapter consultationAdapter;
    private List<Consultation> consultationList;
    private String patientId, trackingNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set system bars
        setSystemBars();

        setContentView(R.layout.activity_consultation_logs);

        // Handle window insets
        setupWindowInsets();

        // Check if we have patient data
        checkPatientData();
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
            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void checkPatientData() {
        SharedPreferences patientPrefs = getSharedPreferences("PatientData", MODE_PRIVATE);
        boolean hasPatientData = patientPrefs.getBoolean("has_patient_data", false);

        if (hasPatientData) {
            // We already have patient data, load consultation logs directly
            loadPatientData();
            initializeViews();
            loadConsultationLogs();
        } else {
            // No patient data, show tracking number dialog
            showTrackingNumberDialog();
        }
    }

    private void showTrackingNumberDialog() {
        // You can reuse the same tracking number dialog from DashboardActivity
        // For now, let's redirect to Dashboard to handle tracking number input
        Toast.makeText(this, "Please verify your tracking number first", Toast.LENGTH_LONG).show();
        finish();
    }

    private void loadPatientData() {
        SharedPreferences patientPrefs = getSharedPreferences("PatientData", MODE_PRIVATE);
        patientId = patientPrefs.getString("patient_id", "");
        trackingNumber = patientPrefs.getString("tracking_number", "");
    }

    private void initializeViews() {
        rvConsultations = findViewById(R.id.rvConsultations);
        etSearchConsultation = findViewById(R.id.etSearchConsultation);
        tvTrackingNumber = findViewById(R.id.tvTrackingNumber);
        emptyState = findViewById(R.id.emptyState);

        // Display tracking number
        tvTrackingNumber.setText(trackingNumber);

        setupConsultationList();
        setupSearchFunctionality();
    }

    private void setupConsultationList() {
        consultationList = new ArrayList<>();

        // Setup RecyclerView
        rvConsultations.setLayoutManager(new LinearLayoutManager(this));
        consultationAdapter = new ConsultationAdapter(consultationList, new ConsultationAdapter.OnConsultationClickListener() {
            @Override
            public void onConsultationClick(Consultation consultation) {
                // Handle consultation item click
                onConsultationSelected(consultation);
            }
        });
        rvConsultations.setAdapter(consultationAdapter);
    }

    private void setupSearchFunctionality() {
        etSearchConsultation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterConsultations(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadConsultationLogs() {
        // Show loading state
        emptyState.setVisibility(View.GONE);
        rvConsultations.setVisibility(View.GONE);

        ApiService apiService = new ApiService(this);
        apiService.getConsultationLogs(patientId, trackingNumber, new ApiService.ApiResponseListener() {
            @Override
            public void onSuccess(JSONObject response) {
                runOnUiThread(() -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray consultationsArray = response.getJSONArray("consultations");
                            consultationList.clear();

                            if (consultationsArray.length() > 0) {
                                for (int i = 0; i < consultationsArray.length(); i++) {
                                    JSONObject consultationJson = consultationsArray.getJSONObject(i);
                                    Consultation consultation = parseConsultationFromJson(consultationJson);
                                    consultationList.add(consultation);
                                }

                                consultationAdapter.notifyDataSetChanged();
                                showConsultationList();
                            } else {
                                showEmptyState("No consultation records found");
                            }
                        } else {
                            String message = response.getString("message");
                            showEmptyState(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showEmptyState("Error loading consultation logs");
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showEmptyState("Network error: " + error);
                });
            }
        });
    }

    private Consultation parseConsultationFromJson(JSONObject json) throws JSONException {
        Consultation consultation = new Consultation();

        consultation.setAppointmentId(json.getString("appointment_id"));
        consultation.setDoctorName(json.getString("doctor_name"));
        consultation.setDoctorSpecialty(json.getString("doctor_specialty"));
        consultation.setAppointmentDate(json.getString("appointment_date"));
        consultation.setAppointmentTime(json.getString("appointment_time"));
        consultation.setStatus(json.getString("status"));
        consultation.setRemarks(json.getString("remarks"));

        return consultation;
    }

    private void showConsultationList() {
        emptyState.setVisibility(View.GONE);
        rvConsultations.setVisibility(View.VISIBLE);
    }

    private void showEmptyState(String message) {
        rvConsultations.setVisibility(View.GONE);
        emptyState.setVisibility(View.VISIBLE);

        // Update empty state message
        TextView emptyMessage = findViewById(R.id.emptyMessage);
        if (emptyMessage != null) {
            emptyMessage.setText(message);
        }
    }

    private void filterConsultations(String query) {
        List<Consultation> filteredList = new ArrayList<>();

        if (query.isEmpty()) {
            filteredList.addAll(consultationList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Consultation consultation : consultationList) {
                if (consultation.getDoctorName().toLowerCase().contains(lowerCaseQuery) ||
                        consultation.getDoctorSpecialty().toLowerCase().contains(lowerCaseQuery) ||
                        consultation.getAppointmentDate().toLowerCase().contains(lowerCaseQuery) ||
                        consultation.getStatus().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(consultation);
                }
            }
        }

        consultationAdapter.filterList(filteredList);

        // Show empty state if no results
        if (filteredList.isEmpty() && !query.isEmpty()) {
            showEmptyState("No consultations match your search");
        } else if (filteredList.isEmpty()) {
            showEmptyState("No consultation records found");
        } else {
            showConsultationList();
        }
    }

    private void onConsultationSelected(Consultation consultation) {
        // Handle consultation item click
        // You can show details in a dialog or navigate to detail activity
        showConsultationDetails(consultation);
    }

    private void showConsultationDetails(Consultation consultation) {
        // Create a dialog to show consultation details
        new android.app.AlertDialog.Builder(this)
                .setTitle("Consultation Details")
                .setMessage(
                        "Doctor: " + consultation.getDoctorName() + "\n" +
                                "Specialty: " + consultation.getDoctorSpecialty() + "\n" +
                                "Date: " + consultation.getAppointmentDate() + "\n" +
                                "Time: " + consultation.getAppointmentTime() + "\n" +
                                "Status: " + consultation.getStatus() + "\n" +
                                "Remarks: " + consultation.getRemarks()
                )
                .setPositiveButton("OK", null)
                .show();
    }
}