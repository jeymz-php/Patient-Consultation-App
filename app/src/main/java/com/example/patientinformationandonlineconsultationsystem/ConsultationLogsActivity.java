package com.example.patientinformationandonlineconsultationsystem;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

public class ConsultationLogsActivity extends AppCompatActivity {

    private RecyclerView recyclerConsultations;
    private ConsultationAdapter adapter;
    private List<Consultation> consultations = new ArrayList<>();

    private static final String API_URL = "http://192.168.100.2/patient-consultation-mobile/get_consultation.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation_logs);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Consultation Logs");
        setSupportActionBar(toolbar);

        // Optional: back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.orange));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(0); // white text/icons
        }

        // RecyclerView setup
        recyclerConsultations = findViewById(R.id.recyclerConsultations);
        recyclerConsultations.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ConsultationAdapter(consultations, new ConsultationAdapter.OnConsultationActionListener() {
            @Override
            public void onDeleteSchedule(Consultation consultation) {
                deleteConsultation(consultation.getId());
            }

            @Override
            public void onJoinFeed(Consultation consultation) {
                Intent intent = new Intent(ConsultationLogsActivity.this, ConsultationFeedActivity.class);
                intent.putExtra("consultationId", consultation.getId());
                startActivity(intent);
            }
        });

        recyclerConsultations.setAdapter(adapter);

        fetchConsultationLogs();
    }

    private void fetchConsultationLogs() {
        int patientId = getSharedPreferences("PatientData", MODE_PRIVATE).getInt("patient_id", 1);
        String url = API_URL + "?patient_id=" + patientId;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            JSONArray data = response.getJSONArray("data");
                            consultations.clear();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                JSONObject doc = obj.getJSONObject("doctor");

                                Doctor doctor = new Doctor(
                                        doc.getInt("id"),
                                        doc.getString("name"),
                                        doc.getString("specialization"),
                                        doc.getString("contact"),
                                        doc.getString("email")
                                );

                                consultations.add(new Consultation(
                                        obj.getInt("consultation_id"),
                                        obj.getString("consultation_date"),
                                        obj.getString("consultation_time"),
                                        obj.getString("status"),
                                        obj.getString("diagnosis"),
                                        obj.getString("treatment"),
                                        doctor
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
                    Toast.makeText(this, "Error fetching consultation logs", Toast.LENGTH_LONG).show();
                });

        queue.add(request);
    }

    private void deleteConsultation(int consultationId) {
        String url = "http://192.168.100.2/patient-consultation-mobile/delete_consultation.php?id=" + consultationId;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                            fetchConsultationLogs(); // refresh list
                        } else {
                            Toast.makeText(this, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error deleting", Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Network error", Toast.LENGTH_LONG).show());

        queue.add(request);
    }
}
