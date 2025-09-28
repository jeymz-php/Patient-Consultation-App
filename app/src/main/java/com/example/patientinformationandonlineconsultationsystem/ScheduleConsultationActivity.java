package com.example.patientinformationandonlineconsultationsystem;

import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
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
    private Button btnPrevMonth, btnNextMonth, btnNextToTime;

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

        // Example doctor info (replace with real data)
        tvDoctorInfo.setText("Dr. Juan Dela Cruz - Cardiology");

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
            // TODO: open time selection activity
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
