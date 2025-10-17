package com.activity.communityhealthcare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class PatientInformationConfirmedActivity extends AppCompatActivity {

    private MaterialButton btnGoDashboard, btnCheckEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_information_confirmed);

        btnGoDashboard = findViewById(R.id.btnGoDashboard);
        btnCheckEmail = findViewById(R.id.btnCheckEmail);

        // Go to Dashboard
        btnGoDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(PatientInformationConfirmedActivity.this, DashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        // Open Email app
        btnCheckEmail.setOnClickListener(v -> {
            try {
                Intent emailIntent = new Intent(Intent.ACTION_MAIN);
                emailIntent.addCategory(Intent.CATEGORY_APP_EMAIL);
                emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(emailIntent, "Open email app"));
            } catch (Exception e) {
                // fallback if no email app found
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://mail.google.com"));
                startActivity(webIntent);
            }
        });
    }
}
