package com.activity.communityhealthcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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
        // Redirect to Dashboard to handle tracking number input
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
                // Handle consultation item click - show modal dialog
                showConsultationModal(consultation);
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

        // Note: getSchedulesMeet.php doesn't use patient_id or tracking_number parameters
        // We'll send empty strings since the PHP doesn't expect these parameters
        apiService.getConsultationLogs("", "", new ApiService.ApiResponseListener() {
            @Override
            public void onSuccess(JSONObject response) {
                runOnUiThread(() -> {
                    try {
                        consultationList.clear();

                        // Handle getSchedulesMeet.php response format
                        if (response.has("hasSchedule")) {
                            boolean hasSchedule = response.getBoolean("hasSchedule");

                            if (hasSchedule) {
                                // Create a Consultation object from the single appointment
                                Consultation consultation = new Consultation();
                                consultation.setAppointmentId("1"); // Default ID since PHP doesn't return appointment_id
                                consultation.setAppointmentDate(response.getString("appointment_date"));
                                consultation.setAppointmentTime(response.getString("appointment_time"));
                                consultation.setStatus("Scheduled"); // Default status
                                consultation.setRemarks(response.optString("remarks", "Upcoming appointment"));

                                consultationList.add(consultation);
                                consultationAdapter.notifyDataSetChanged();
                                showConsultationList();

                                Toast.makeText(ConsultationLogsActivity.this,
                                        "Showing your next upcoming appointment", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = response.optString("message", "No upcoming appointments within 30 minutes");
                                showEmptyState(message);
                            }
                        } else if (response.has("error")) {
                            // Handle error response
                            String errorMessage = response.getString("error");
                            showEmptyState("Error: " + errorMessage);
                        } else {
                            // Unknown response format
                            showEmptyState("Unexpected response format");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showEmptyState("Error parsing response: " + e.getMessage());
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
                if (consultation.getAppointmentDate().toLowerCase().contains(lowerCaseQuery) ||
                        consultation.getAppointmentTime().toLowerCase().contains(lowerCaseQuery) ||
                        consultation.getStatus().toLowerCase().contains(lowerCaseQuery) ||
                        consultation.getRemarks().toLowerCase().contains(lowerCaseQuery)) {
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

    private void showConsultationModal(Consultation consultation) {
        // Inflate the custom dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_consultation_details, null);

        // Initialize views from the dialog
        TextView txtAppointmentDate = dialogView.findViewById(R.id.txtDialogAppointmentDate);
        TextView txtAppointmentTime = dialogView.findViewById(R.id.txtDialogAppointmentTime);
        TextView txtStatus = dialogView.findViewById(R.id.txtDialogStatus);
        TextView txtRemarks = dialogView.findViewById(R.id.txtDialogRemarks);
        Button btnJoin = dialogView.findViewById(R.id.btnJoinConsultation);
        Button btnBack = dialogView.findViewById(R.id.btnBackConsultation);

        // Set consultation data
        txtAppointmentDate.setText(consultation.getAppointmentDate());
        txtAppointmentTime.setText(consultation.getAppointmentTime());
        txtStatus.setText(consultation.getStatus());
        txtRemarks.setText(consultation.getRemarks());

        // Style the status based on its value
        styleStatusTextView(txtStatus, consultation.getStatus());

        // For getSchedulesMeet.php, the appointment is always upcoming/joinable
        btnJoin.setVisibility(View.VISIBLE);

        // Create the dialog
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setCancelable(true);

        androidx.appcompat.app.AlertDialog dialog = dialogBuilder.create();

        // Set button click listeners
        btnJoin.setOnClickListener(v -> {
            joinVideoConference(consultation);
            dialog.dismiss();
        });

        btnBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        // Show the dialog
        dialog.show();

        // Set proper modal size
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            int height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    private void styleStatusTextView(TextView statusTextView, String status) {
        switch (status.toLowerCase()) {
            case "completed":
                statusTextView.setTextColor(Color.parseColor("#10B981")); // Green
                statusTextView.setBackgroundResource(R.drawable.bg_status_completed);
                break;
            case "scheduled":
                statusTextView.setTextColor(Color.parseColor("#F59E0B")); // Amber
                statusTextView.setBackgroundResource(R.drawable.bg_status_scheduled);
                break;
            case "cancelled":
                statusTextView.setTextColor(Color.parseColor("#EF4444")); // Red
                statusTextView.setBackgroundResource(R.drawable.bg_status_cancelled);
                break;
            case "ongoing":
                statusTextView.setTextColor(Color.parseColor("#3B82F6")); // Blue
                statusTextView.setBackgroundResource(R.drawable.bg_status_ongoing);
                break;
            default:
                statusTextView.setTextColor(Color.parseColor("#6B7280")); // Gray
                statusTextView.setBackgroundResource(R.drawable.bg_status_default);
                break;
        }
    }

    private void joinVideoConference(Consultation consultation) {
        // Since getSchedulesMeet.php returns appointments within 30 minutes,
        // we can assume they're joinable
        Toast.makeText(this, "Joining video conference...", Toast.LENGTH_SHORT).show();

        // Proceed to VideoConferenceActivity
        Intent intent = new Intent(ConsultationLogsActivity.this, VideoConferenceActivity.class);
        intent.putExtra("appointment_id", consultation.getAppointmentId());
        intent.putExtra("appointment_date", consultation.getAppointmentDate());
        intent.putExtra("appointment_time", consultation.getAppointmentTime());
        startActivity(intent);
    }
}