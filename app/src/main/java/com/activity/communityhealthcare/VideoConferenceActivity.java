package com.activity.communityhealthcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class VideoConferenceActivity extends AppCompatActivity {

    private static final String TAG = "VideoConferenceDebug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_conference);
        Log.d(TAG, "onCreate: Starting browser-based video conference");

        // Get patient name from shared preferences
        String patientName = getPatientName();

        // Construct the video conference URL (same as desktop website)
        String videoConferenceUrl = "https://communityhealthcare.bsitfoura.com/userConsultation.php?name=" +
                Uri.encode(patientName);

        Log.d(TAG, "Video Conference URL: " + videoConferenceUrl);

        // Open the URL in the device's browser
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoConferenceUrl));
            startActivity(browserIntent);
            Toast.makeText(this, "Opening video conference in browser...", Toast.LENGTH_SHORT).show();

            // Close this activity after launching browser
            finish();

        } catch (Exception e) {
            Log.e(TAG, "Error opening browser: " + e.getMessage());
            Toast.makeText(this, "Error opening browser. Please try again.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private String getPatientName() {
        try {
            SharedPreferences prefs = getSharedPreferences("PatientData", MODE_PRIVATE);
            String firstName = prefs.getString("first_name", "Patient");
            String lastName = prefs.getString("last_name", "");

            if (lastName.isEmpty()) {
                return firstName;
            } else {
                return firstName + " " + lastName;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting patient name: " + e.getMessage());
            return "Patient";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "VideoConferenceActivity resumed");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "VideoConferenceActivity destroyed");
    }
}