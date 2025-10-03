package com.example.patientinformationandonlineconsultationsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScheduleConsultationActivity extends AppCompatActivity {

    private TextView tvMonthYearTitle, tvDoctorInfo;
    private RecyclerView calendarRecyclerView;
    private DaysAdapter daysAdapter;
    private Calendar calendar;
    private List<Day> days = new ArrayList<>();
    private Button btnNextToTime;

    private int doctorId;  // <<-- add this
    private String doctorName, doctorSpecialization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_consultation);

        // Views
        tvMonthYearTitle = findViewById(R.id.tvMonthYearTitle);
        tvDoctorInfo = findViewById(R.id.tvDoctorInfo);
        btnNextToTime = findViewById(R.id.btnNextToTime);
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);

        ImageButton btnPrevMonth = findViewById(R.id.btnPrevMonth);
        ImageButton btnNextMonth = findViewById(R.id.btnNextMonth);

        // --- Get doctor info from intent ---
        doctorId = getIntent().getIntExtra("doctorId", -1);
        doctorName = getIntent().getStringExtra("doctorName");
        doctorSpecialization = getIntent().getStringExtra("doctorSpecialty");

        if (doctorName != null && doctorSpecialization != null) {
            tvDoctorInfo.setText("👨‍⚕️ " + doctorName + " - " + doctorSpecialization);
        } else {
            tvDoctorInfo.setText("No doctor selected");
        }

        // Calendar setup
        calendar = Calendar.getInstance();
        generateCalendar();

        daysAdapter = new DaysAdapter(days, selectedDay -> {
            // Just select the day, handled in adapter
        });

        calendarRecyclerView.setLayoutManager(new GridLayoutManager(this, 7));
        calendarRecyclerView.setAdapter(daysAdapter);

        // Month navigation buttons
        btnPrevMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            generateCalendar();
            daysAdapter.notifyDataSetChanged();
        });

        btnNextMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1);
            generateCalendar();
            daysAdapter.notifyDataSetChanged();
        });

        // Next button (go to time selection)
        btnNextToTime.setOnClickListener(v -> {
            Day selectedDay = null;
            for (Day d : days) {
                if (d.isSelected()) {
                    selectedDay = d;
                    break;
                }
            }

            if (selectedDay != null) {
                Intent intent = new Intent(ScheduleConsultationActivity.this, TimeSelectionActivity.class);
                intent.putExtra("doctorId", doctorId);
                intent.putExtra("doctorName", doctorName);
                intent.putExtra("doctorSpecialty", doctorSpecialization);
                intent.putExtra("selectedDate",
                        selectedDay.getDay() + "/" + (selectedDay.getMonth() + 1) + "/" + selectedDay.getYear());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please select a date first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateCalendar() {
        days.clear();

        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        tvMonthYearTitle.setText(getMonthName(month) + " " + year);

        Calendar tempCal = Calendar.getInstance();
        tempCal.set(year, month, 1);

        int firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK) - 1; // Sunday = 0
        int daysInMonth = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Fill empty slots before start of month
        for (int i = 0; i < firstDayOfWeek; i++) {
            days.add(new Day(0, month, year)); // empty placeholder
        }

        // Fill actual days
        for (int i = 1; i <= daysInMonth; i++) {
            days.add(new Day(i, month, year));
        }
    }

    private String getMonthName(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }
}
