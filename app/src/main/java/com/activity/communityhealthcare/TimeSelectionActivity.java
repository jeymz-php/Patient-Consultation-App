package com.activity.communityhealthcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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

public class TimeSelectionActivity extends AppCompatActivity {

    private RecyclerView rvTimeSlots;
    private TextView tvSelectedDate;
    private TimeSlotAdapter timeSlotAdapter;
    private List<TimeSlot> timeSlotList;
    private String selectedDate; // Store the selected date in yyyy-MM-dd format

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set system bars
        setSystemBars();

        setContentView(R.layout.activity_time_selection);

        // Handle window insets
        setupWindowInsets();

        initializeViews();
        checkAvailabilityAndSetupTimeSlots();
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
            View btnConfirm = findViewById(R.id.btnConfirmTime);
            if (btnConfirm != null) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) btnConfirm.getLayoutParams();
                params.bottomMargin = navigationBarHeight + 16; // 16dp original margin
                btnConfirm.setLayoutParams(params);
            }

            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void initializeViews() {
        rvTimeSlots = findViewById(R.id.rvTimeSlots);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);

        // Set selected date from shared preferences
        SharedPreferences prefs = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        String selectedDateDisplay = prefs.getString("selected_date_display", "Today");
        selectedDate = prefs.getString("selected_date", ""); // yyyy-MM-dd format

        tvSelectedDate.setText(selectedDateDisplay);
    }

    private void checkAvailabilityAndSetupTimeSlots() {

        // Generate all possible time slots first
        timeSlotList = generateTimeSlots();

        // Check availability for each time slot
        setupTimeSlotsAdapter();
    }

    private void setupTimeSlotsAdapter() {
        // DEBUG: Check final time slots availability
        System.out.println("Final time slots availability:");
        int availableCount = 0;
        for (TimeSlot slot : timeSlotList) {
            System.out.println("Time: " + slot.getTime() + ", Available: " + slot.isAvailable());
            if (slot.isAvailable()) availableCount++;
        }
        System.out.println("Total available slots: " + availableCount + "/" + timeSlotList.size());

        // Setup RecyclerView with grid layout (3 columns)
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        rvTimeSlots.setLayoutManager(layoutManager);

        timeSlotAdapter = new TimeSlotAdapter(timeSlotList, new TimeSlotAdapter.OnTimeSlotClickListener() {
            @Override
            public void onTimeSlotClick(TimeSlot timeSlot) {
                if (timeSlot.isAvailable()) {
                    onTimeSlotSelected(timeSlot);
                }
            }
        });
        rvTimeSlots.setAdapter(timeSlotAdapter);

        // Make sure RecyclerView is visible
        rvTimeSlots.setVisibility(View.VISIBLE);

    }

    private List<TimeSlot> generateTimeSlots() {
        List<TimeSlot> slots = new ArrayList<>();

        // Generate time slots from 8:00 AM to 5:00 PM with 30-minute intervals
        // Skip lunch time: 12:00 PM and 12:30 PM

        int startHour = 8;
        int endHour = 17; // 5:00 PM

        for (int hour = startHour; hour <= endHour; hour++) {
            // Skip lunch hours (12:00 PM and 12:30 PM)
            if (hour == 12) continue;

            for (int minute = 0; minute < 60; minute += 30) {
                // Skip 5:30 PM since we end at 5:00 PM
                if (hour == 17 && minute == 30) continue;

                String period = (hour < 12) ? "AM" : "PM";
                int displayHour = (hour > 12) ? hour - 12 : hour;
                if (displayHour == 0) displayHour = 12;

                String time = String.format(Locale.getDefault(), "%d:%02d %s", displayHour, minute, period);
                slots.add(new TimeSlot(time, true)); // Initially mark all as available
            }
        }

        return slots;
    }

    private void onTimeSlotSelected(TimeSlot timeSlot) {
        // Save selected time slot
        SharedPreferences prefs = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("selected_time", timeSlot.getTime());
        editor.apply();

        Toast.makeText(this, "Time slot selected: " + timeSlot.getTime(), Toast.LENGTH_SHORT).show();

        // Proceed to confirmation activity
        Intent intent = new Intent(TimeSelectionActivity.this, ConfirmationSchedConsultActivity.class);
        startActivity(intent);

        // Optional: close this screen so the user can't go back here accidentally
        finish();
    }
}