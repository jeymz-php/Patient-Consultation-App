package com.example.patientinformationandonlineconsultationsystem;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TimeSelectionActivity extends AppCompatActivity {
    private GridView timeGrid;
    private Button btnBack, btnConfirm;
    private TextView tvSelectedDate;
    private String doctorName, specialization, selectedDate, selectedTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_selection);

        timeGrid = findViewById(R.id.timeGrid);
        btnBack = findViewById(R.id.btnBack);
        btnConfirm = findViewById(R.id.btnConfirm);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);

        doctorName = getIntent().getStringExtra("doctorName");
        specialization = getIntent().getStringExtra("specialization");
        selectedDate = getIntent().getStringExtra("selectedDate");

        tvSelectedDate.setText("Selected Date: " + selectedDate);

        setupTimeSlots();
        setupClickListeners();
    }

    private void setupTimeSlots() {
        String[] timeSlots = {
                "8:00 AM", "8:30 AM", "9:00 AM",
                "9:30 AM", "10:00 AM", "10:30 AM",
                "11:00 AM", "11:30 AM", "12:00 PM",
                "2:30 PM", "3:00 PM", "3:30 PM"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, timeSlots) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setBackgroundResource(R.drawable.time_slot_background);
                tv.setTextColor(Color.BLACK);
                tv.setPadding(20, 20, 20, 20);
                return tv;
            }
        };
        timeGrid.setAdapter(adapter);

        timeGrid.setOnItemClickListener((parent, view, position, id) -> {
            selectedTime = (String) parent.getItemAtPosition(position);

            // reset all
            for (int i = 0; i < parent.getChildCount(); i++) {
                parent.getChildAt(i).setBackgroundResource(R.drawable.time_slot_background);
                ((TextView) parent.getChildAt(i)).setTextColor(Color.BLACK);
            }

            // highlight selected
            view.setBackgroundColor(Color.parseColor("#E87C00"));
            ((TextView) view).setTextColor(Color.WHITE);
        });
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> {
            if (selectedTime != null) {
                Intent intent = new Intent(this, ConfirmationActivity.class);
                intent.putExtra("doctorName", doctorName);
                intent.putExtra("specialization", specialization);
                intent.putExtra("selectedDate", selectedDate);
                intent.putExtra("selectedTime", selectedTime);
                startActivity(intent);
            }
        });
    }
}
