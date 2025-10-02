package com.example.patientinformationandonlineconsultationsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ConfirmationActivity extends AppCompatActivity {

    private TextView tvSelectedDate, tvSelectedTime;
    private Button btnConfirm, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnBack = findViewById(R.id.btnBack);

        // Get selected date & time from intent
        Intent intent = getIntent();
        String selectedDate = intent.getStringExtra("selectedDate");
        String selectedTime = intent.getStringExtra("selectedTime");

        if (selectedDate != null) {
            tvSelectedDate.setText("📅 Date: " + selectedDate);
        }
        if (selectedTime != null) {
            tvSelectedTime.setText("⏰ Time: " + selectedTime);
        }

        // Confirm button
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ConfirmationActivity.this, "Consultation Confirmed!", Toast.LENGTH_SHORT).show();

                // TODO: save booking to database or send to server here

                // Go back to home after confirmation
                Intent homeIntent = new Intent(ConfirmationActivity.this, MainActivity.class);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homeIntent);
                finish();
            }
        });

        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // just go back to TimeSelectionActivity
            }
        });
    }
}
