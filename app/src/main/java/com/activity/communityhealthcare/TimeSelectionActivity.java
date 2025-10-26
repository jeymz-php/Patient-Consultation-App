package com.activity.communityhealthcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TimeSelectionActivity extends AppCompatActivity {

    private RecyclerView rvTimeSlots;
    private TextView tvSelectedDate;
    private MaterialButton btnConfirmTime;
    private TextInputEditText etCustomHour, etCustomMinute;
    private MaterialButtonToggleGroup togglePeriod;
    private MaterialButton btnUseCustomTime;

    private TimeSlotAdapter timeSlotAdapter;
    private List<TimeSlot> timeSlotList;
    private String selectedDate; // Store the selected date in yyyy-MM-dd format
    private String selectedTime = ""; // Store the currently selected time in 12-hour format
    private String selectedTime24Hour = ""; // Store the currently selected time in 24-hour format

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
        setupCustomTimeSelection();
        setupConfirmButton();
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
        btnConfirmTime = findViewById(R.id.btnConfirmTime);
        etCustomHour = findViewById(R.id.etCustomHour);
        etCustomMinute = findViewById(R.id.etCustomMinute);
        togglePeriod = findViewById(R.id.togglePeriod);
        btnUseCustomTime = findViewById(R.id.btnUseCustomTime);

        // Set selected date from shared preferences
        SharedPreferences prefs = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        String selectedDateDisplay = prefs.getString("selected_date_display", "Today");
        selectedDate = prefs.getString("selected_date", ""); // yyyy-MM-dd format

        tvSelectedDate.setText(selectedDateDisplay);

        // Initially disable confirm button until a time is selected
        btnConfirmTime.setEnabled(false);
        btnConfirmTime.setAlpha(0.5f);
    }

    private void setupCustomTimeSelection() {
        // Set default period to AM
        togglePeriod.check(R.id.btnAM);

        // Add listener to style selected button
        togglePeriod.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                // Style the checked button
                MaterialButton checkedButton = findViewById(checkedId);
                if (checkedButton != null) {
                    checkedButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E87C00")));
                    checkedButton.setTextColor(Color.WHITE);
                }

                // Style the unchecked button
                for (int i = 0; i < group.getChildCount(); i++) {
                    MaterialButton button = (MaterialButton) group.getChildAt(i);
                    if (button.getId() != checkedId) {
                        button.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                        button.setTextColor(Color.parseColor("#666666"));
                    }
                }
            }
        });

        btnUseCustomTime.setOnClickListener(v -> {
            String hourStr = etCustomHour.getText().toString().trim();
            String minuteStr = etCustomMinute.getText().toString().trim();

            if (TextUtils.isEmpty(hourStr) || TextUtils.isEmpty(minuteStr)) {
                Toast.makeText(this, "Please enter both hour and minute", Toast.LENGTH_SHORT).show();
                return;
            }

            int hour = Integer.parseInt(hourStr);
            int minute = Integer.parseInt(minuteStr);

            // Validate hour (1-12)
            if (hour < 1 || hour > 12) {
                Toast.makeText(this, "Hour must be between 1 and 12", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate minute (0-59)
            if (minute < 0 || minute > 59) {
                Toast.makeText(this, "Minute must be between 00 and 59", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get selected period
            String period = togglePeriod.getCheckedButtonId() == R.id.btnAM ? "AM" : "PM";

            // Format the time in 12-hour format for display
            String customTime12Hour = String.format(Locale.getDefault(), "%d:%02d %s", hour, minute, period);

            // Convert to 24-hour format for database
            String customTime24Hour = convertTo24HourFormat(hour, minute, period);

            // Select the custom time
            selectTime(customTime12Hour, customTime24Hour);

            // Clear any grid selection
            if (timeSlotAdapter != null) {
                timeSlotAdapter.setSelectedTime(""); // Clear grid selection
            }

            Toast.makeText(this, "Custom time selected: " + customTime12Hour, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupConfirmButton() {
        btnConfirmTime.setOnClickListener(v -> {
            if (selectedTime.isEmpty()) {
                Toast.makeText(this, "Please select a time slot first", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save selected time slot (use 24-hour format for database)
            SharedPreferences prefs = getSharedPreferences("AppPreferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("selected_time", selectedTime24Hour); // Save 24-hour format
            editor.apply();

            Toast.makeText(this, "Time slot confirmed: " + selectedTime, Toast.LENGTH_SHORT).show();

            // Proceed to confirmation activity
            Intent intent = new Intent(TimeSelectionActivity.this, ConfirmationSchedConsultActivity.class);
            startActivity(intent);

            // Optional: close this screen so the user can't go back here accidentally
            finish();
        });
    }

    private String convertTo24HourFormat(int hour, int minute, String period) {
        int hour24 = hour;

        if (period.equals("AM")) {
            // 12 AM becomes 00
            if (hour == 12) {
                hour24 = 0;
            }
        } else { // PM
            // 12 PM remains 12, others add 12
            if (hour != 12) {
                hour24 = hour + 12;
            }
        }

        return String.format(Locale.getDefault(), "%02d:%02d", hour24, minute);
    }

    private void checkAvailabilityAndSetupTimeSlots() {
        // Generate all possible time slots first
        timeSlotList = generateTimeSlots();
        setupTimeSlotsAdapter();
    }

    private void setupTimeSlotsAdapter() {
        // Setup RecyclerView with grid layout (3 columns)
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        rvTimeSlots.setLayoutManager(layoutManager);

        timeSlotAdapter = new TimeSlotAdapter(timeSlotList, new TimeSlotAdapter.OnTimeSlotClickListener() {
            @Override
            public void onTimeSlotClick(TimeSlot timeSlot) {
                if (timeSlot.isAvailable()) {
                    // For grid time slots, convert from 12-hour to 24-hour format
                    String time12Hour = timeSlot.getTime();
                    String time24Hour = convertGridTimeTo24Hour(time12Hour);
                    selectTime(time12Hour, time24Hour);

                    // Clear custom time inputs when selecting from grid
                    etCustomHour.getText().clear();
                    etCustomMinute.getText().clear();
                    togglePeriod.check(R.id.btnAM);
                }
            }
        });
        rvTimeSlots.setAdapter(timeSlotAdapter);

        // Make sure RecyclerView is visible
        rvTimeSlots.setVisibility(View.VISIBLE);
    }

    private String convertGridTimeTo24Hour(String time12Hour) {
        try {
            // Parse the grid time format (e.g., "8:00 AM", "2:30 PM")
            String[] parts = time12Hour.split(" ");
            String timePart = parts[0];
            String period = parts[1];

            String[] timeParts = timePart.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            return convertTo24HourFormat(hour, minute, period);
        } catch (Exception e) {
            Log.e("TimeConversion", "Error converting grid time: " + time12Hour, e);
            return time12Hour; // Fallback to original if conversion fails
        }
    }

    private void selectTime(String time12Hour, String time24Hour) {
        selectedTime = time12Hour; // Store 12-hour format for display
        selectedTime24Hour = time24Hour; // Store 24-hour format for database

        // Update the adapter to show which time is selected
        if (timeSlotAdapter != null) {
            timeSlotAdapter.setSelectedTime(time12Hour);
        }

        // Enable confirm button
        btnConfirmTime.setEnabled(true);
        btnConfirmTime.setAlpha(1.0f);

        Toast.makeText(this, "Time selected: " + time12Hour, Toast.LENGTH_SHORT).show();
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
}