package com.activity.communityhealthcare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;

import java.net.MalformedURLException;
import java.net.URL;

public class VideoConferenceActivity extends AppCompatActivity {

    private static final String TAG = "VideoConferenceDebug";
    private String doctorName, appointmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting Jitsi Video Conference");

        // Get intent data
        Intent intent = getIntent();
        doctorName = intent.getStringExtra("doctor_name");
        appointmentId = intent.getStringExtra("appointment_id");

        Log.d(TAG, "Received data - Doctor: " + doctorName + ", Appointment ID: " + appointmentId);

        // Initialize Jitsi Meet
        initializeJitsiMeet();
    }

    private void initializeJitsiMeet() {
        try {
            // Use public Jitsi server instead of 8x8.vc
            URL serverURL = new URL("https://8x8.vc");

            Log.d(TAG, "Using public Jitsi server: " + serverURL.toString());

            JitsiMeetConferenceOptions defaultOptions = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(serverURL)
                    .setFeatureFlag("meeting-password.enabled", false)
                    .setFeatureFlag("lobby-mode.enabled", false)
                    .setFeatureFlag("add-people.enabled", false)
                    .setFeatureFlag("invite.enabled", false)
                    .setFeatureFlag("live-streaming.enabled", false)
                    .setFeatureFlag("recording.enabled", false)
                    .setConfigOverride("requireDisplayName", false)
                    .setConfigOverride("prejoinPageEnabled", false)
                    .setConfigOverride("enableWelcomePage", false)
                    .setConfigOverride("enableLobby", false)
                    .setConfigOverride("startWithAudioMuted", false)
                    .setConfigOverride("startWithVideoMuted", false)
                    .build();

            JitsiMeet.setDefaultConferenceOptions(defaultOptions);
            Log.d(TAG, "Jitsi Meet initialized successfully with public server");

            startVideoCall();

        } catch (Exception e) {
            Log.e(TAG, "Error initializing Jitsi Meet: " + e.getMessage(), e);
            showErrorAndFinish("Failed to initialize video conference");
        }
    }

    private void startVideoCall() {
        try {
            String patientName = getPatientName();

            // Must match web version
            String roomName = "vpaas-magic-cookie-40d1b83f34d64323bff4f2b89581daf0/BarangayConsultation";

            Log.d(TAG, "=== Starting Video Call ===");
            Log.d(TAG, "Room: " + roomName);
            Log.d(TAG, "Patient: " + patientName);

            JitsiMeetUserInfo userInfo = new JitsiMeetUserInfo();
            userInfo.setDisplayName(patientName);

            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://8x8.vc"))
                    .setRoom("vpaas-magic-cookie-40d1b83f34d64323bff4f2b89581daf0/BarangayConsultation")
                    .setToken("YOUR_JWT_TOKEN_HERE")
                    .setUserInfo(userInfo)
                    .setSubject("Medical Consultation")
                    .setAudioMuted(false)
                    .setVideoMuted(false)
                    .setFeatureFlag("meeting-password.enabled", false)
                    .setFeatureFlag("lobby-mode.enabled", false)
                    .setConfigOverride("prejoinPageEnabled", false)
                    .setConfigOverride("requireDisplayName", false)
                    .build();

            JitsiMeetActivity.launch(this, options);
            Log.d(TAG, "Jitsi Meet activity launched successfully");
            finish();

        } catch (Exception e) {
            Log.e(TAG, "Error starting video call: " + e.getMessage(), e);
            showErrorAndFinish("Failed to start video conference");
        }
    }

    private String getPatientName() {
        try {
            android.content.SharedPreferences prefs = getSharedPreferences("PatientData", MODE_PRIVATE);
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

    private void showErrorAndFinish(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error: " + message);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "VideoConferenceActivity destroyed");
    }
}