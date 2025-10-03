package com.example.patientinformationandonlineconsultationsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ConfirmationActivity extends AppCompatActivity {

    private TextView tvDoctorInfo, tvSelectedDate, tvSelectedTime;
    private Button btnConfirm, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        tvDoctorInfo = findViewById(R.id.tvDoctorInfo);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnBack = findViewById(R.id.btnBack);

        // Get data from intent
        Intent intent = getIntent();
        int doctorId = getIntent().getIntExtra("doctorId", -1);
        String doctorName = getIntent().getStringExtra("doctorName");
        String doctorSpecialty = getIntent().getStringExtra("doctorSpecialty");
        String selectedDate = getIntent().getStringExtra("selectedDate");
        String selectedTime = getIntent().getStringExtra("selectedTime");


        TextView tvDoctorInfo = findViewById(R.id.tvDoctorInfo);
        if (doctorName != null && doctorSpecialty != null) {
            tvDoctorInfo.setText("👨‍⚕️ " + doctorName + " (" + doctorSpecialty + ")");
        }

        // Show doctor + schedule info
        if (doctorName != null && doctorSpecialty != null) {
            tvDoctorInfo.setText("👨‍⚕️ " + doctorName + " (" + doctorSpecialty + ")");
        }
        if (selectedDate != null) {
            tvSelectedDate.setText("📅 Date: " + selectedDate);
        }
        if (selectedTime != null) {
            tvSelectedTime.setText("⏰ Time: " + selectedTime);
        }

        // Confirm button
        btnConfirm.setOnClickListener(v -> {
            // Get patient info from SharedPreferences
            SharedPreferences prefs = getSharedPreferences("PatientData", MODE_PRIVATE);

            int patientId = prefs.getInt("patient_id", -1); // <--- load actual patient_id
            if (patientId == -1) {
                Toast.makeText(this, "Patient information not found! Please fill your info first.", Toast.LENGTH_SHORT).show();
                return;
            }

            String firstName = prefs.getString("first_name", "");
            String middleName = prefs.getString("middle_name", "");
            String lastName = prefs.getString("last_name", "");
            String dob = prefs.getString("date_of_birth", "");
            String gender = prefs.getString("gender", "");
            String height = prefs.getString("height", "");
            String weight = prefs.getString("weight", "");
            String civilStatus = prefs.getString("civil_status", "");
            String contact = prefs.getString("contact_number", "");
            String email = prefs.getString("email", "");
            String address = prefs.getString("address", "");
            String takingMedications = prefs.getString("taking_medications", "No");
            String medicationList = prefs.getString("medication_list", "");
            String emergencyName = prefs.getString("emergency_name", "");
            String emergencyRelation = prefs.getString("emergency_relationship", "");
            String emergencyContact = prefs.getString("emergency_contact", "");

            String url = "http://192.168.100.2/patient-consultation-mobile/add_consultation.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        Log.d("ServerResponse", response);

                        try {
                            JSONObject obj = new JSONObject(response);
                            String status = obj.getString("status");
                            String message = obj.has("message") ? obj.getString("message") : "";

                            if (status.equals("success")) {
                                Toast.makeText(ConfirmationActivity.this, "Consultation Confirmed!", Toast.LENGTH_SHORT).show();

                                // Navigate back to Main/Home
                                Intent homeIntent = new Intent(ConfirmationActivity.this, MainActivity.class);
                                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(homeIntent);
                                finish();
                            } else {
                                Toast.makeText(ConfirmationActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ConfirmationActivity.this, "JSON Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        if (error.networkResponse != null) {
                            Log.d("RequestError", "Status code: " + error.networkResponse.statusCode);
                            Log.d("RequestError", "Data: " + new String(error.networkResponse.data));
                        } else {
                            Log.d("RequestError", "No network response", error);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    // Consultation info
                    params.put("doctor_id", String.valueOf(doctorId));
                    params.put("consultation_date", selectedDate);
                    params.put("consultation_time", selectedTime);

                    // Patient info
                    params.put("patient_id", String.valueOf(patientId)); // <--- send patient_id
                    params.put("first_name", firstName);
                    params.put("middle_name", middleName);
                    params.put("last_name", lastName);
                    params.put("date_of_birth", dob);
                    params.put("gender", gender);
                    params.put("height", height);
                    params.put("weight", weight);
                    params.put("civil_status", civilStatus);
                    params.put("contact_number", contact);
                    params.put("email", email);
                    params.put("address", address);
                    params.put("taking_medications", takingMedications);
                    params.put("medication_list", medicationList);
                    params.put("emergency_name", emergencyName);
                    params.put("emergency_relationship", emergencyRelation);
                    params.put("emergency_contact", emergencyContact);

                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(stringRequest);
        });


        // Back button
        btnBack.setOnClickListener(v -> finish());
    }
}
