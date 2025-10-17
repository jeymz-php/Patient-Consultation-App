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
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class DoctorsListActivity extends AppCompatActivity {

    private RecyclerView rvDoctors;
    private EditText etSearchDoctor;
    private TextView tvTrackingNumber;
    private DoctorsAdapter doctorsAdapter;
    private List<Doctor> doctorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set system bars
        setSystemBars();

        setContentView(R.layout.activity_doctors_list);

        // Handle window insets
        setupWindowInsets();

        initializeViews();
        setupDoctorsList();
        setupSearchFunctionality();
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

    private void initializeViews() {
        rvDoctors = findViewById(R.id.rvDoctors);
        etSearchDoctor = findViewById(R.id.etSearchDoctor);
        tvTrackingNumber = findViewById(R.id.tvTrackingNumber);

        // Display tracking number
        SharedPreferences prefs = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        String trackingNumber = prefs.getString("tracking_number", "N/A");
        tvTrackingNumber.setText(trackingNumber);
    }

    private void setupDoctorsList() {
        // Initialize doctor list
        doctorList = getSampleDoctors();

        // Setup RecyclerView
        rvDoctors.setLayoutManager(new LinearLayoutManager(this));
        doctorsAdapter = new DoctorsAdapter(doctorList, new DoctorsAdapter.OnDoctorClickListener() {
            @Override
            public void onDoctorClick(Doctor doctor) {
                // Handle doctor selection
                onDoctorSelected(doctor);
            }
        });
        rvDoctors.setAdapter(doctorsAdapter);
    }

    private void setupSearchFunctionality() {
        etSearchDoctor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDoctors(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterDoctors(String query) {
        List<Doctor> filteredList = new ArrayList<>();

        if (query.isEmpty()) {
            filteredList.addAll(doctorList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Doctor doctor : doctorList) {
                if (doctor.getName().toLowerCase().contains(lowerCaseQuery) ||
                        doctor.getSpecialty().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(doctor);
                }
            }
        }

        doctorsAdapter.filterList(filteredList);
    }

    private void onDoctorSelected(Doctor doctor) {
        // Save selected doctor and proceed to date selection
        SharedPreferences prefs = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("selected_doctor_id", doctor.getId());
        editor.putString("selected_doctor_name", doctor.getName());
        editor.putString("selected_doctor_specialty", doctor.getSpecialty());
        editor.apply();

        // Proceed to date selection
        startActivity(new Intent(DoctorsListActivity.this, DateSelectionActivity.class));
    }

    private List<Doctor> getSampleDoctors() {
        List<Doctor> doctors = new ArrayList<>();

        doctors.add(new Doctor(
                "1",
                "Dr. Maria Santos",
                "General Medicine",
                "Available",
                R.drawable.ic_doctor_avatar
        ));

        doctors.add(new Doctor(
                "2",
                "Dr. Juan Dela Cruz",
                "Cardiology",
                "Available",
                R.drawable.ic_doctor_avatar
        ));

        doctors.add(new Doctor(
                "3",
                "Dr. Ana Reyes",
                "Pediatrics",
                "Busy",
                R.drawable.ic_doctor_avatar
        ));

        doctors.add(new Doctor(
                "4",
                "Dr. Roberto Garcia",
                "Orthopedics",
                "Available",
                R.drawable.ic_doctor_avatar
        ));

        doctors.add(new Doctor(
                "5",
                "Dr. Sofia Martinez",
                "Dermatology",
                "Available",
                R.drawable.ic_doctor_avatar
        ));

        doctors.add(new Doctor(
                "6",
                "Dr. Michael Tan",
                "Internal Medicine",
                "Available",
                R.drawable.ic_doctor_avatar
        ));

        doctors.add(new Doctor(
                "7",
                "Dr. Lisa Lim",
                "Obstetrics & Gynecology",
                "Busy",
                R.drawable.ic_doctor_avatar
        ));

        doctors.add(new Doctor(
                "8",
                "Dr. James Wilson",
                "Surgery",
                "Available",
                R.drawable.ic_doctor_avatar
        ));

        return doctors;
    }
}