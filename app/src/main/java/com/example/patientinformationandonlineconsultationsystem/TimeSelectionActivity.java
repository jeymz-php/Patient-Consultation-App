package com.example.patientinformationandonlineconsultationsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class TimeSelectionActivity extends AppCompatActivity {

    private GridView gridView;
    private TimeAdapter timeAdapter;
    private List<String> timeSlots;
    private String selectedTime = null;
    private Button btnNextToConfirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_selection);

        gridView = findViewById(R.id.gridView);
        btnNextToConfirmation = findViewById(R.id.btnNextToConfirmation);

        // Sample time slots
        timeSlots = new ArrayList<>();
        timeSlots.add("09:00 AM");
        timeSlots.add("09:30 AM");
        timeSlots.add("10:00 AM");
        timeSlots.add("10:30 AM");
        timeSlots.add("11:00 AM");
        timeSlots.add("11:30 AM");
        timeSlots.add("01:00 PM");
        timeSlots.add("01:30 PM");
        timeSlots.add("02:00 PM");
        timeSlots.add("02:30 PM");
        timeSlots.add("03:00 PM");
        timeSlots.add("03:30 PM");
        timeSlots.add("04:00 PM");

        timeAdapter = new TimeAdapter(this, timeSlots);
        gridView.setAdapter(timeAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                timeAdapter.setSelected(position);
                selectedTime = timeAdapter.getSelectedTime();
                Toast.makeText(TimeSelectionActivity.this, "Selected: " + selectedTime, Toast.LENGTH_SHORT).show();
            }
        });

        // Handle Next button → go to ConfirmationActivity
        btnNextToConfirmation.setOnClickListener(v -> {
            if (selectedTime != null) {
                Intent intent = new Intent(TimeSelectionActivity.this, ConfirmationActivity.class);

                // Pass doctor info + selected date from previous activity
                intent.putExtra("doctorName", getIntent().getStringExtra("doctorName"));
                intent.putExtra("specialization", getIntent().getStringExtra("specialization"));
                intent.putExtra("selectedDate", getIntent().getStringExtra("selectedDate"));

                // Pass selected time from this activity
                intent.putExtra("selectedTime", selectedTime);

                startActivity(intent);
            } else {
                Toast.makeText(TimeSelectionActivity.this, "Please select a time slot", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
