package com.example.patientinformationandonlineconsultationsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class DoctorsListActivity extends AppCompatActivity {

    private RecyclerView recyclerDoctors;
    private DoctorsAdapter adapter;
    private int id;
    private String name;
    private String specialization;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_list);

        recyclerDoctors = findViewById(R.id.recyclerDoctors);
        recyclerDoctors.setLayoutManager(new LinearLayoutManager(this));

        List<Doctor> doctors = new ArrayList<>();
        doctors.add(new Doctor(1, "Dr. Juan Dela Cruz", "Cardiologist", "10 years", "09123456789", "juan@email.com"));
        doctors.add(new Doctor(2, "Dr. Maria Santos", "Dermatologist", "8 years", "09987654321", "maria@email.com"));
        doctors.add(new Doctor(3, "Dr. Jose Rizal", "Neurologist", "15 years", "09876543210", "jose@email.com"));

        adapter = new DoctorsAdapter(doctors, doctor -> {
            Intent intent = new Intent(DoctorsListActivity.this, ScheduleConsultationActivity.class);
            intent.putExtra("doctorId", doctor.getId()); // new
            intent.putExtra("doctorName", doctor.getName());
            intent.putExtra("doctorSpecialty", doctor.getSpecialization());
            startActivity(intent);
        });

        recyclerDoctors.setAdapter(adapter);
    }
}
