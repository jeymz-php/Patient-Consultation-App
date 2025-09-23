package com.example.patientinformationandonlineconsultationsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                            // ✅ Go to Patient Info
                            startActivity(new Intent(MainActivity.this, PatientInformationActivity.class));
                        }
                    })
                    .show();
        });
    }
}
