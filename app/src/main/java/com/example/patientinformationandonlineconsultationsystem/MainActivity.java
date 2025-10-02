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
            String[] options = {"Schedule a Consultation", "Patient Information"};
            int[] icons = {R.drawable.ic_consultation, R.drawable.ic_patient};

            OptionAdapter adapter = new OptionAdapter(MainActivity.this, options, icons);

            new MaterialAlertDialogBuilder(MainActivity.this)
                    .setTitle("Choose an option")
                    .setAdapter(adapter, (dialog, which) -> {
                        if (which == 0) {
                            // ✅ Go to Doctors List
                            startActivity(new Intent(MainActivity.this, DoctorsListActivity.class));
                        } else if (which == 1) {
                            // Check if patient data exists
                            android.content.SharedPreferences prefs = getSharedPreferences("PatientData", MODE_PRIVATE);
                            boolean hasPatientData = prefs.getBoolean("has_patient_data", false);

                            if (hasPatientData) {
                                // Go directly to profile if data exists
                                startActivity(new Intent(MainActivity.this, PatientProfileActivity.class));
                            } else {
                                // ✅ Show Terms and Conditions Dialog First
                                showTermsAndConditionsDialog();
                            }
                        }
                    })
                    .show();
        });
    }

    private void checkPatientData() {
        android.content.SharedPreferences prefs = getSharedPreferences("PatientData", MODE_PRIVATE);
        boolean hasPatientData = prefs.getBoolean("has_patient_data", false);

        // Optional: Auto-navigate to profile if data exists (uncomment if needed)
        // if (hasPatientData) {
        //     startActivity(new Intent(MainActivity.this, PatientProfileActivity.class));
        //     finish();
        // }
    }

    private void showTermsAndConditionsDialog() {
        // Inflate custom layout for the dialog
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_terms_and_conditions, null);

        CheckBox checkBoxTerms = dialogView.findViewById(R.id.checkBoxTerms);
        CheckBox checkBoxPrivacy = dialogView.findViewById(R.id.checkBoxPrivacy);
        Button btnNext = dialogView.findViewById(R.id.btnNext);

        // Initially hide the Next button
        btnNext.setVisibility(View.GONE);

        // Create the dialog
        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // Listener for checkboxes
        View.OnClickListener checkBoxListener = v -> {
            if (checkBoxTerms.isChecked() && checkBoxPrivacy.isChecked()) {
                btnNext.setVisibility(View.VISIBLE);
            } else {
                btnNext.setVisibility(View.GONE);
            }
        };

        checkBoxTerms.setOnClickListener(checkBoxListener);
        checkBoxPrivacy.setOnClickListener(checkBoxListener);

        // Next button click listener
        btnNext.setOnClickListener(v -> {
            dialog.dismiss();
            // Proceed to Patient Information Activity
            startActivity(new Intent(MainActivity.this, PatientInformationActivity.class));
        });

        dialog.show();
    }
}