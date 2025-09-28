package com.example.patientinformationandonlineconsultationsystem;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ConfirmationActivity extends AppCompatActivity {
    private Button btnContinue;
    private TextView tvAppointmentDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        btnContinue = findViewById(R.id.btnContinue);
        tvAppointmentDetail = findViewById(R.id.tvAppointmentDetail);

        String doctorName = getIntent().getStringExtra("doctorName");
        String specialization = getIntent().getStringExtra("specialization");
        String selectedDate = getIntent().getStringExtra("selectedDate");
        String selectedTime = getIntent().getStringExtra("selectedTime");

        tvAppointmentDetail.setText(
                "Doctor: " + doctorName + " (" + specialization + ")\n" +
                        "Date: " + selectedDate + "\n" +
                        "Time: " + selectedTime
        );

        btnContinue.setOnClickListener(v -> {
            // return to doctors list (or main screen)
            finish();
        });
    }
}
