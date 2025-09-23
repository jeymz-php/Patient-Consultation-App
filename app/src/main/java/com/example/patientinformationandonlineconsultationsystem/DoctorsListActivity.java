package com.example.patientinformationandonlineconsultationsystem;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DoctorsListActivity extends AppCompatActivity {

    private LinearLayout doctorsContainer;
    private EditText etSearch;
    private Doctor[] doctors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_list);

        initializeDoctors();
        initializeViews();
        populateDoctorsList();
        setupSearchFunctionality();
    }

    private void initializeViews() {
        doctorsContainer = findViewById(R.id.doctorsContainer);
        etSearch = findViewById(R.id.etSearch);
    }

    private void setupSearchFunctionality() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDoctors(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void initializeDoctors() {
        doctors = new Doctor[]{
                new Doctor("Dr. Sarah Johnson", "Cardiologist", "10 years", "123-456-7890", "sarah.johnson@hospital.com"),
                new Doctor("Dr. Michael Chen", "Neurologist", "15 years", "123-456-7891", "michael.chen@hospital.com"),
                new Doctor("Dr. Emily Davis", "Pediatrician", "8 years", "123-456-7892", "emily.davis@hospital.com"),
                new Doctor("Dr. Robert Martinez", "Orthopedic Surgeon", "20 years", "123-456-7893", "robert.martinez@hospital.com"),
                new Doctor("Dr. Jennifer Wilson", "Dermatologist", "12 years", "123-456-7894", "jennifer.wilson@hospital.com"),
                new Doctor("Dr. David Brown", "Psychiatrist", "18 years", "123-456-7895", "david.brown@hospital.com")
        };
    }

    private void populateDoctorsList() {
        doctorsContainer.removeAllViews();
        for (Doctor doctor : doctors) {
            addDoctorCard(doctor);
        }
    }

    private void filterDoctors(String query) {
        doctorsContainer.removeAllViews();
        for (Doctor doctor : doctors) {
            if (doctor.getName().toLowerCase().contains(query.toLowerCase()) ||
                    doctor.getSpecialization().toLowerCase().contains(query.toLowerCase())) {
                addDoctorCard(doctor);
            }
        }
    }

    private void addDoctorCard(Doctor doctor) {
        View doctorCard = LayoutInflater.from(this).inflate(R.layout.doctor_card, doctorsContainer, false);

        TextView tvDoctorName = doctorCard.findViewById(R.id.tvDoctorName);
        TextView tvSpecialization = doctorCard.findViewById(R.id.tvSpecialization);
        TextView tvExperience = doctorCard.findViewById(R.id.tvExperience);
        TextView tvContact = doctorCard.findViewById(R.id.tvContact);
        TextView tvEmail = doctorCard.findViewById(R.id.tvEmail);

        tvDoctorName.setText(doctor.getName());
        tvSpecialization.setText(doctor.getSpecialization());
        tvExperience.setText("Experience: " + doctor.getExperience());
        tvContact.setText("Contact: " + doctor.getContact());
        tvEmail.setText("Email: " + doctor.getEmail());

        doctorsContainer.addView(doctorCard);
    }

    private static class Doctor {
        private String name, specialization, experience, contact, email;
        public Doctor(String n, String s, String e, String c, String em) {
            this.name = n; this.specialization = s; this.experience = e; this.contact = c; this.email = em;
        }
        public String getName() { return name; }
        public String getSpecialization() { return specialization; }
        public String getExperience() { return experience; }
        public String getContact() { return contact; }
        public String getEmail() { return email; }
    }
}
