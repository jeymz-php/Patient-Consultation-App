package com.example.patientinformationandonlineconsultationsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
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
            int[] icons = {R.drawable.ic_consultation, R.drawable.ic_patient}; // replace with your logos

            OptionAdapter adapter = new OptionAdapter(MainActivity.this, options, icons);

            new MaterialAlertDialogBuilder(MainActivity.this)
                    .setTitle("Choose an option")
                    .setAdapter(adapter, (dialog, which) -> {
                        if (which == 0) {
                            Toast.makeText(MainActivity.this, "Schedule a Consultation clicked", Toast.LENGTH_SHORT).show();
                        } else if (which == 1) {
                            // ✅ Open PatientInformation activity
                            Intent intent = new Intent(MainActivity.this, PatientInformationActivity.class);
                            startActivity(intent);
                        }
                    })
                    .show();
        });
    }
}
