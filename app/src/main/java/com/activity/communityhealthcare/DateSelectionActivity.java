package com.activity.communityhealthcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DateSelectionActivity extends AppCompatActivity {

    private RecyclerView rvCalendar;
    private TextView tvSelectedDoctor, tvCurrentMonth;
    private ImageButton btnPrevMonth, btnNextMonth;
    private CalendarAdapter calendarAdapter;
    private List<CalendarDate> calendarDateList;
    private Calendar currentCalendar;
    private String selectedDoctorId, selectedDoctorName, selectedDoctorSpecialty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set system bars
        setSystemBars();

        setContentView(R.layout.activity_date_selection);

        // Handle window insets
        setupWindowInsets();

        // Get selected doctor data
        getSelectedDoctorData();

        initializeViews();
        setupCalendar();
        setupMonthNavigation();
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
            // Handle navigation bar insets
            int navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;

            // Add padding to bottom views if needed
            View btnConfirm = findViewById(R.id.btnConfirmDate);
            if (btnConfirm != null) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) btnConfirm.getLayoutParams();
                params.bottomMargin = navigationBarHeight + 16; // 16dp original margin
                btnConfirm.setLayoutParams(params);
            }

            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void getSelectedDoctorData() {
        SharedPreferences prefs = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        selectedDoctorId = prefs.getString("selected_doctor_id", "");
        selectedDoctorName = prefs.getString("selected_doctor_name", "");
        selectedDoctorSpecialty = prefs.getString("selected_doctor_specialty", "");

        // If no data from intent, use sample data
        if (selectedDoctorName.isEmpty()) {
            selectedDoctorName = "Dr. Maria Santos";
            selectedDoctorSpecialty = "General Medicine";
        }
    }

    private void initializeViews() {
        rvCalendar = findViewById(R.id.rvCalendar);
        tvSelectedDoctor = findViewById(R.id.tvSelectedDoctor);
        tvCurrentMonth = findViewById(R.id.tvCurrentMonth);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);

        // Set selected doctor info
        String doctorInfo = selectedDoctorName + " - " + selectedDoctorSpecialty;
        tvSelectedDoctor.setText(doctorInfo);

        // Initialize calendar to current month
        currentCalendar = Calendar.getInstance();
    }

    private void setupCalendar() {
        updateCalendar();

        // Setup RecyclerView with grid layout (7 columns for days of week)
        GridLayoutManager layoutManager = new GridLayoutManager(this, 7);
        rvCalendar.setLayoutManager(layoutManager);

        calendarAdapter = new CalendarAdapter(calendarDateList, new CalendarAdapter.OnDateClickListener() {
            @Override
            public void onDateClick(CalendarDate calendarDate) {
                if (calendarDate.isSelectable()) {
                    onDateSelected(calendarDate);
                } else {
                    Toast.makeText(DateSelectionActivity.this, "This date is not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rvCalendar.setAdapter(calendarAdapter);
    }

    private void setupMonthNavigation() {
        btnPrevMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });
    }

    private void updateCalendar() {
        // Update month header
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvCurrentMonth.setText(monthFormat.format(currentCalendar.getTime()));

        // Generate calendar dates for current month
        calendarDateList = generateCalendarDates();
        if (calendarAdapter != null) {
            calendarAdapter.updateDates(calendarDateList);
        }
    }

    private List<CalendarDate> generateCalendarDates() {
        List<CalendarDate> dates = new ArrayList<>();

        Calendar calendar = (Calendar) currentCalendar.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        // Get the first day of month and number of days in month
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Add empty cells for days before the first day of month
        for (int i = 1; i < firstDayOfWeek; i++) {
            dates.add(new CalendarDate("", "", false, false));
        }

        // Get current date for comparison
        Calendar today = Calendar.getInstance();

        // Add days of the month
        for (int day = 1; day <= daysInMonth; day++) {
            calendar.set(Calendar.DAY_OF_MONTH, day);

            String dayNumber = String.valueOf(day);
            String dayName = new SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.getTime());

            // Check if date is selectable (not in past and not too far in future)
            boolean isSelectable = !calendar.before(today) &&
                    !isDateTooFarInFuture(calendar);
            boolean isToday = isSameDay(calendar, today);

            dates.add(new CalendarDate(dayNumber, dayName, isSelectable, isToday));
        }

        return dates;
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private boolean isDateTooFarInFuture(Calendar date) {
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.MONTH, 3); // Allow scheduling up to 3 months in advance
        return date.after(maxDate);
    }

    private void onDateSelected(CalendarDate calendarDate) {
        // Save selected date
        Calendar selectedDate = (Calendar) currentCalendar.clone();
        selectedDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(calendarDate.getDayNumber()));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String selectedDateStr = dateFormat.format(selectedDate.getTime());

        SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        String displayDateStr = displayFormat.format(selectedDate.getTime());

        SharedPreferences prefs = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("selected_date", selectedDateStr);
        editor.putString("selected_date_display", displayDateStr);
        editor.apply();

        Toast.makeText(this, "Date selected: " + displayDateStr, Toast.LENGTH_SHORT).show();

        // Proceed to time selection
        startActivity(new Intent(DateSelectionActivity.this, TimeSelectionActivity.class));
    }
}