package com.example.patientinformationandonlineconsultationsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DoctorsListActivity extends BaseActivity { // ✅ now extends BaseActivity

    private RecyclerView recyclerDoctors;
    private DoctorsAdapter adapter;
    private List<Doctor> doctors = new ArrayList<>();

    // ⚠️ Make sure this matches your actual XAMPP IP
    private static final String API_URL = "http://192.168.1.12/patient-consultation-mobile/get_doctors.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_list);

        // ✅ Setup sidebar from BaseActivity
        setupDrawer();

        recyclerDoctors = findViewById(R.id.recyclerDoctors);
        recyclerDoctors.setLayoutManager(new LinearLayoutManager(this));

        adapter = new DoctorsAdapter(doctors, doctor -> {
            Intent intent = new Intent(DoctorsListActivity.this, ScheduleConsultationActivity.class);
            intent.putExtra("doctorId", doctor.getId());
            intent.putExtra("doctorName", doctor.getName());
            intent.putExtra("doctorSpecialty", doctor.getSpecialization());
            startActivity(intent);
        });

        recyclerDoctors.setAdapter(adapter);

        fetchDoctorsFromServer();
    }

    private void fetchDoctorsFromServer() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, API_URL, null,
                response -> {
                    try {
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            JSONArray data = response.getJSONArray("doctors");
                            doctors.clear();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                doctors.add(new Doctor(
                                        obj.getInt("doctor_id"),             // ✅ changed
                                        obj.getString("doctor_name"),        // ✅ changed
                                        obj.getString("specialization"),
                                        obj.getString("contact_number"),     // ✅ changed
                                        obj.getString("email")
                                ));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error fetching doctors", Toast.LENGTH_LONG).show();
                });

        queue.add(request);
    }
}
