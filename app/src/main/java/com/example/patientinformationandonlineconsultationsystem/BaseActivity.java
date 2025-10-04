package com.example.patientinformationandonlineconsultationsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupDrawer() {
        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Burger icon toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (id == R.id.nav_consultations) {
            startActivity(new Intent(this, ConsultationLogsActivity.class));
        } else if (id == R.id.nav_doctors) {
            startActivity(new Intent(this, DoctorsListActivity.class));
        } else if (id == R.id.nav_patient_info) {
            // Check if patient info exists in SharedPreferences
            SharedPreferences prefs = getSharedPreferences("PatientData", MODE_PRIVATE);
            boolean hasPatientData = prefs.getBoolean("has_patient_data", false);

            Intent intent;
            if (hasPatientData) {
                // If patient data exists, go to profile activity
                intent = new Intent(this, PatientProfileActivity.class);
            } else {
                // If no data exists, open form to fill new patient info
                intent = new Intent(this, PatientInformationActivity.class);
            }
            startActivity(intent);
        }


        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
