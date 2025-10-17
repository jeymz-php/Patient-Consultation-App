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

public class ConfirmationSchedConsultActivity extends AppCompatActivity {

    private TextView tvDoctorName, tvDoctorSpecialty, tvDate, tvTime;
    private MaterialButton btnConfirm, btnBack;

    private String doctorName, doctorSpecialty, selectedDate, selectedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSystemBars();
        setContentView(R.layout.activity_confirmation_sched_consult);

        initializeViews();
        loadScheduleData();
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
        selectedDate = prefs.getString("selected_date_display", "Today");
        selectedTime = prefs.getString("selected_time", "8:00 AM");

        tvDoctorName.setText(doctorName);
        tvDoctorSpecialty.setText(doctorSpecialty);
        tvDate.setText(selectedDate);
        tvTime.setText(selectedTime);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            finish(); // go back to time selection
        });

        btnConfirm.setOnClickListener(v -> {
            Toast.makeText(this, "Consultation confirmed!", Toast.LENGTH_LONG).show();

            // Example: Proceed to confirmation summary or dashboard
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
