package com.example.patientinformationandonlineconsultationsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScheduleConsultationActivity extends AppCompatActivity {

    private TextView tvMonthYearTitle, tvDoctorInfo;
    private GridView calendarGrid;
    private CalendarAdapter calendarAdapter;
    private Calendar calendar;
    private List<Day> dayList = new ArrayList<>();
    private ImageButton btnPrevMonth, btnNextMonth;
    private Button btnNextToTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_consultation);

        // Views
        tvMonthYearTitle = findViewById(R.id.tvMonthYearTitle);
        calendarGrid = findViewById(R.id.calendarGrid);
        tvDoctorInfo = findViewById(R.id.tvDoctorInfo);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        btnNextToTime = findViewById(R.id.btnNextToTime);

        // --- Get doctor info from intent ---
        String doctorName = getIntent().getStringExtra("doctorName");
        String doctorSpecialty = getIntent().getStringExtra("doctorSpecialty");

        if (doctorName != null && doctorSpecialty != null) {
            tvDoctorInfo.setText(doctorName + " - " + doctorSpecialty);
        } else if (doctorName != null) {
            tvDoctorInfo.setText(doctorName);
        } else {
            tvDoctorInfo.setText("No doctor selected");
        }

        // Calendar setup
        calendar = Calendar.getInstance();
        generateCalendar();

        calendarAdapter = new CalendarAdapter(this, dayList);
        calendarGrid.setAdapter(calendarAdapter);

        // Month navigation buttons
        btnPrevMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            generateCalendar();
            calendarAdapter.updateDays(dayList);
        });

        btnNextMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1);
            generateCalendar();
            calendarAdapter.updateDays(dayList);
        });

        // Next button (to time selection)
        btnNextToTime.setOnClickListener(v -> {
            // Hanapin yung selected date
            Day selectedDay = null;
            for (Day d : dayList) {
                if (d.isSelected()) {
                    selectedDay = d;
                    break;
                }
            }

            if (selectedDay != null) {
                Intent intent = new Intent(ScheduleConsultationActivity.this, TimeSelectionActivity.class);
                intent.putExtra("doctorName", doctorName);
                intent.putExtra("doctorSpecialty", doctorSpecialty);
                intent.putExtra("selectedDate", selectedDay.getDay() + "/" + (selectedDay.getMonth() + 1) + "/" + selectedDay.getYear());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please select a date first", Toast.LENGTH_SHORT).show();
            }
        });

        // Day selection in GridView
        calendarGrid.setOnItemClickListener((parent, view, position, id) -> {
            Day selectedDay = dayList.get(position);
            if (selectedDay.getDay() != 0) {
                for (Day d : dayList) d.setSelected(false);
                selectedDay.setSelected(true);
                calendarAdapter.updateDays(dayList);
            }
        });
    }

    private void generateCalendar() {
        dayList.clear();

        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        tvMonthYearTitle.setText(getMonthName(month) + " " + year);

        Calendar tempCal = Calendar.getInstance();
        tempCal.set(year, month, 1);

        int firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK) - 1; // Sunday = 0
        int daysInMonth = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Empty days for offset
        for (int i = 0; i < firstDayOfWeek; i++) {
            dayList.add(new Day(0, month, year));
        }

        // Add actual days
        for (int i = 1; i <= daysInMonth; i++) {
            dayList.add(new Day(i, month, year));
        }
    }

    private String getMonthName(int month) {
        return new java.text.DateFormatSymbols().getMonths()[month];
    }
}
