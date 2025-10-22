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
        setContentView(R.layout.activity_video_conference);
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
                    .setToken("eyJraWQiOiJ2cGFhcy1tYWdpYy1jb29raWUtNDBkMWI4M2YzNGQ2NDMyM2JmZjRmMmI4OTU4MWRhZjAvOWJmZTY2LVNBTVBMRV9BUFAiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJqaXRzaSIsImlzcyI6ImNoYXQiLCJpYXQiOjE3NjExMDk5NDUsImV4cCI6MTc2MTExNzE0NSwibmJmIjoxNzYxMTA5OTQwLCJzdWIiOiJ2cGFhcy1tYWdpYy1jb29raWUtNDBkMWI4M2YzNGQ2NDMyM2JmZjRmMmI4OTU4MWRhZjAiLCJjb250ZXh0Ijp7ImZlYXR1cmVzIjp7ImxpdmVzdHJlYW1pbmciOnRydWUsImZpbGUtdXBsb2FkIjp0cnVlLCJvdXRib3VuZC1jYWxsIjp0cnVlLCJzaXAtb3V0Ym91bmQtY2FsbCI6ZmFsc2UsInRyYW5zY3JpcHRpb24iOnRydWUsImxpc3QtdmlzaXRvcnMiOmZhbHNlLCJyZWNvcmRpbmciOnRydWUsImZsaXAiOmZhbHNlfSwidXNlciI6eyJoaWRkZW4tZnJvbS1yZWNvcmRlciI6ZmFsc2UsIm1vZGVyYXRvciI6dHJ1ZSwibmFtZSI6ImpvaG5taWNoYWVsaWJlNTEiLCJpZCI6Imdvb2dsZS1vYXV0aDJ8MTA1NTI3NDYwOTIzOTk0NjkxMzEzIiwiYXZhdGFyIjoiIiwiZW1haWwiOiJqb2hubWljaGFlbGliZTUxQGdtYWlsLmNvbSJ9fSwicm9vbSI6IioifQ.Pmb9ORiPnC7UzJUn3bAl2PG-hkNKTGPkPq9fwjQrSiFB5VctNcrqSEVnRE_o8ucAWaYg5hoEvecVI1pJ001D2MO7xxiLBtxmKCaLla2oAkdGCxp4p2FjhYXWk4LaqmKp51tyTvlv8n9gaz1EK3BmMegBC75c9hiCputMn6-dMol-K0JZ7lpqlghcmJ_Jh4Kx9eVLG4dptg1RX4THJkBdyCZQGPpic0d4djHDDFvyw7Z2stMhMkfqXFRIvflnhIN81ZqpqukbpATBOxc9NTg4E8mQ0AwMetXW9Hi5XOSRsHsFr5x6_Q-WycUml7Ha6lfG3AR07wuLbKauZgb9KKttXw")
                    .setUserInfo(userInfo)
                    .setSubject("Medical Consultation")
                    .setFeatureFlag("meeting-password.enabled", false)
                    .setFeatureFlag("invite.enabled", false)
                    .setFeatureFlag("live-streaming.enabled", false)
                    .setFeatureFlag("recording.enabled", false)
                    .setConfigOverride("prejoinPageEnabled", false)
                    .setConfigOverride("requireDisplayName", false)
                    .setAudioMuted(false)
                    .setVideoMuted(false)
                    .build();

            // Launch Jitsi Meet Activity without finishing this one
            JitsiMeetActivity.launch(this, options);
            Log.d(TAG, "Jitsi Meet activity launched successfully");

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