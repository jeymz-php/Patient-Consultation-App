package com.activity.communityhealthcare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class VideoConferenceActivity extends AppCompatActivity {

    private static final String TAG = "VideoConferenceDebug";
    private String doctorName, appointmentId;
    private boolean isFetchingToken = false;

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

        // Test the API first
        testJWTAPI();

        // Initialize Jitsi Meet and fetch JWT token
        initializeJitsiMeet();
    }

    private void initializeJitsiMeet() {
        try {
            // Use 8x8.vc server to match desktop website
            URL serverURL = new URL("https://8x8.vc");

            Log.d(TAG, "Using 8x8.vc server: " + serverURL.toString());

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
            Log.d(TAG, "Jitsi Meet initialized successfully with 8x8.vc server");

            // Fetch fresh JWT token from API
            fetchJWTToken();

        } catch (Exception e) {
            Log.e(TAG, "Error initializing Jitsi Meet: " + e.getMessage(), e);
            showErrorAndFinish("Failed to initialize video conference");
        }
    }

    private void fetchJWTToken() {
        if (isFetchingToken) {
            return;
        }

        isFetchingToken = true;

        String patientName = getPatientName();
        String patientEmail = getPatientEmail();
        String roomName = "vpaas-magic-cookie-40d1b83f34d64323bff4f2b89581daf0/BarangayConsultation";

        // Try the API first
        android.net.Uri uri = android.net.Uri.parse("https://communityhealthcare.bsitfoura.com/api/generateJWT.php")
                .buildUpon()
                .appendQueryParameter("room", roomName)
                .appendQueryParameter("name", patientName)
                .appendQueryParameter("email", patientEmail)
                .build();

        String url = uri.toString();

        Log.d(TAG, "=== JWT Token Request ===");
        Log.d(TAG, "URL: " + url);
        Log.d(TAG, "Patient Name: " + patientName);
        Log.d(TAG, "Patient Email: " + patientEmail);
        Log.d(TAG, "Room: " + roomName);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    isFetchingToken = false;
                    try {
                        Log.d(TAG, "=== JWT API Response ===");
                        Log.d(TAG, "Full Response: " + response.toString());

                        if (response.has("jwt")) {
                            String jwtToken = response.getString("jwt");
                            Log.d(TAG, "JWT Token received successfully");
                            Log.d(TAG, "Token length: " + jwtToken.length());

                            // Test if the token is valid by checking its structure
                            if (jwtToken.startsWith("eyJ")) {
                                Log.d(TAG, "Token appears valid (starts with eyJ)");
                                startVideoCall(jwtToken);
                            } else {
                                Log.e(TAG, "Token appears invalid - doesn't start with eyJ");
                                useFallbackApproach();
                            }
                        } else {
                            Log.e(TAG, "No JWT token in API response");
                            useFallbackApproach();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing JWT response: " + e.getMessage(), e);
                        useFallbackApproach();
                    }
                },
                error -> {
                    isFetchingToken = false;
                    Log.e(TAG, "=== JWT API Error ===");
                    Log.e(TAG, "Error: " + error.getMessage());

                    if (error.networkResponse != null) {
                        Log.e(TAG, "Status Code: " + error.networkResponse.statusCode);
                        if (error.networkResponse.data != null) {
                            String errorBody = new String(error.networkResponse.data);
                            Log.e(TAG, "Error Body: " + errorBody);
                        }
                    }

                    useFallbackApproach();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void useFallbackApproach() {
        Log.d(TAG, "Trying alternative approaches...");

        // Option 1: Try without JWT token on 8x8.vc
        tryWithoutToken();
    }

    private void tryWithoutToken() {
        try {
            String patientName = getPatientName();
            String roomName = "vpaas-magic-cookie-40d1b83f34d64323bff4f2b89581daf0/BarangayConsultation";

            Log.d(TAG, "=== Trying Video Call WITHOUT JWT Token ===");
            Log.d(TAG, "Room: " + roomName);
            Log.d(TAG, "Patient: " + patientName);

            JitsiMeetUserInfo userInfo = new JitsiMeetUserInfo();
            userInfo.setDisplayName(patientName);
            userInfo.setEmail(getPatientEmail());

            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://8x8.vc"))
                    .setRoom(roomName)
                    .setUserInfo(userInfo)
                    .setSubject("Medical Consultation")
                    .setFeatureFlag("meeting-password.enabled", false)
                    .setFeatureFlag("invite.enabled", false)
                    .setFeatureFlag("live-streaming.enabled", false)
                    .setFeatureFlag("recording.enabled", false)
                    .setFeatureFlag("lobby-mode.enabled", false)
                    .setFeatureFlag("add-people.enabled", false)
                    .setConfigOverride("prejoinPageEnabled", false)
                    .setConfigOverride("requireDisplayName", false)
                    .setConfigOverride("enableWelcomePage", false)
                    .setConfigOverride("enableLobby", false)
                    .setAudioMuted(false)
                    .setVideoMuted(false)
                    .build();

            JitsiMeetActivity.launch(this, options);
            Log.d(TAG, "Launched Jitsi without JWT token");

        } catch (Exception e) {
            Log.e(TAG, "Error starting video call without token: " + e.getMessage(), e);
            // Final fallback - try public Jitsi server
            tryPublicJitsiServer();
        }
    }

    private void tryPublicJitsiServer() {
        try {
            String patientName = getPatientName();
            String roomName = "BarangayConsultation" + System.currentTimeMillis(); // Unique room

            Log.d(TAG, "=== Trying Public Jitsi Server ===");
            Log.d(TAG, "Room: " + roomName);
            Log.d(TAG, "Patient: " + patientName);

            JitsiMeetUserInfo userInfo = new JitsiMeetUserInfo();
            userInfo.setDisplayName(patientName);

            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://meet.jit.si"))
                    .setRoom(roomName)
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

            JitsiMeetActivity.launch(this, options);
            Log.d(TAG, "Launched Jitsi with public server");

        } catch (Exception e) {
            Log.e(TAG, "Error starting video call on public server: " + e.getMessage(), e);
            showErrorAndFinish("Cannot connect to video conference. Please try again later.");
        }
    }

    private void testJWTAPI() {
        // Test the generateJWT.php API directly
        String testUrl = "https://communityhealthcare.bsitfoura.com/api/generateJWT.php?room=testroom&name=Test%20Patient&email=test@example.com";

        Log.d(TAG, "=== TEST JWT API ===");
        Log.d(TAG, "Test URL: " + testUrl);
        Log.d(TAG, "Please open this URL in a web browser to check if the API works");
        Log.d(TAG, "Expected response: {\"jwt\":\"eyJ...\"}");
        Log.d(TAG, "If you see an error, the PHP API has issues");

        // You can manually test this URL in a browser
    }

    private void startVideoCall(String jwtToken) {
        try {
            String patientName = getPatientName();
            String roomName = "vpaas-magic-cookie-40d1b83f34d64323bff4f2b89581daf0/BarangayConsultation";

            Log.d(TAG, "=== Starting Video Call ===");
            Log.d(TAG, "Room: " + roomName);
            Log.d(TAG, "Patient: " + patientName);
            Log.d(TAG, "Using JWT Token: " + (jwtToken != null ? "Yes" : "No"));

            JitsiMeetUserInfo userInfo = new JitsiMeetUserInfo();
            userInfo.setDisplayName(patientName);
            userInfo.setEmail(getPatientEmail());

            JitsiMeetConferenceOptions.Builder optionsBuilder = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://8x8.vc"))
                    .setRoom(roomName)
                    .setUserInfo(userInfo)
                    .setSubject("Medical Consultation")
                    .setFeatureFlag("meeting-password.enabled", false)
                    .setFeatureFlag("invite.enabled", false)
                    .setFeatureFlag("live-streaming.enabled", false)
                    .setFeatureFlag("recording.enabled", false)
                    .setFeatureFlag("lobby-mode.enabled", false)
                    .setFeatureFlag("add-people.enabled", false)
                    .setConfigOverride("prejoinPageEnabled", false)
                    .setConfigOverride("requireDisplayName", false)
                    .setConfigOverride("enableWelcomePage", false)
                    .setConfigOverride("enableLobby", false)
                    .setAudioMuted(false)
                    .setVideoMuted(false)
                    .setConfigOverride("disableModeratorIndicator", false)
                    .setConfigOverride("startScreenSharing", false)
                    .setConfigOverride("enableEmailInStats", false)
                    .setConfigOverride("enableNoAudioDetection", true)
                    .setConfigOverride("startAudioOnly", false)
                    .setConfigOverride("startWithAudioMuted", false)
                    .setConfigOverride("startWithVideoMuted", false);

            // Add JWT token if available
            if (jwtToken != null && !jwtToken.isEmpty()) {
                optionsBuilder.setToken(jwtToken);
            }

            JitsiMeetConferenceOptions options = optionsBuilder.build();

            // Launch Jitsi Meet Activity
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

    private String getPatientEmail() {
        try {
            android.content.SharedPreferences prefs = getSharedPreferences("PatientData", MODE_PRIVATE);
            return prefs.getString("email", "patient@example.com");
        } catch (Exception e) {
            Log.e(TAG, "Error getting patient email: " + e.getMessage());
            return "patient@example.com";
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