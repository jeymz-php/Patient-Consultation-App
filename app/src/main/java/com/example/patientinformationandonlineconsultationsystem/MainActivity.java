package com.example.patientinformationandonlineconsultationsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if patient data already exists
        checkPatientData();

        Button btnGetStarted = findViewById(R.id.btnGetStarted);

        btnGetStarted.setOnClickListener(v -> {
            // Add "Consultation Logs" as third option
            String[] options = {"Schedule a Consultation", "Patient Information", "Consultation Logs"};
            int[] icons = {R.drawable.ic_consultation, R.drawable.ic_patient, R.drawable.ic_schedule};

            OptionAdapter adapter = new OptionAdapter(MainActivity.this, options, icons);

            new MaterialAlertDialogBuilder(MainActivity.this)
                    .setTitle("Choose an option")
                    .setAdapter(adapter, (dialog, which) -> {
                        if (which == 0) {
                            // Go to Doctors List / Schedule Consultation
                            startActivity(new Intent(MainActivity.this, DoctorsListActivity.class));
                        } else if (which == 1) {
                            // Patient Information
                            android.content.SharedPreferences prefs = getSharedPreferences("PatientData", MODE_PRIVATE);
                            boolean hasPatientData = prefs.getBoolean("has_patient_data", false);

                            if (hasPatientData) {
                                startActivity(new Intent(MainActivity.this, PatientProfileActivity.class));
                            } else {
                                showTermsAndConditionsDialog();
                            }
                        } else if (which == 2) {
                            // Open Consultation Logs
                            startActivity(new Intent(MainActivity.this, ConsultationLogsActivity.class));
                        }
                    })
                    .show();
        });
    }

    private void checkPatientData() {
        android.content.SharedPreferences prefs = getSharedPreferences("PatientData", MODE_PRIVATE);
        boolean hasPatientData = prefs.getBoolean("has_patient_data", false);

        // Optional: Auto-navigate to profile if data exists
        // if (hasPatientData) {
        //     startActivity(new Intent(MainActivity.this, PatientProfileActivity.class));
        //     finish();
        // }
    }

    private void showTermsAndConditionsDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_terms_and_conditions, null);

        CheckBox checkBoxTerms = dialogView.findViewById(R.id.checkBoxTerms);
        CheckBox checkBoxPrivacy = dialogView.findViewById(R.id.checkBoxPrivacy);
        Button btnNext = dialogView.findViewById(R.id.btnNext);

        btnNext.setVisibility(View.GONE);

        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        View.OnClickListener checkBoxListener = v -> {
            if (checkBoxTerms.isChecked() && checkBoxPrivacy.isChecked()) {
                btnNext.setVisibility(View.VISIBLE);
            } else {
                btnNext.setVisibility(View.GONE);
            }
        };

        checkBoxTerms.setOnClickListener(checkBoxListener);
        checkBoxPrivacy.setOnClickListener(checkBoxListener);

        btnNext.setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(MainActivity.this, PatientInformationActivity.class));
        });

        dialog.show();
    }
}
