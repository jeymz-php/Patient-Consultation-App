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
import java.util.HashMap;
import java.util.Map;

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
            // Use public Jitsi server
            URL serverURL = new URL("https://meet.jit.si");

            Log.d(TAG, "Using public Jitsi server: " + serverURL.toString());

            // Create config overrides to disable password completely
            Map<String, Object> configOverrides = new HashMap<>();
            configOverrides.put("prejoinPageEnabled", false);
            configOverrides.put("requireDisplayName", false);
            configOverrides.put("enableWelcomePage", false);
            configOverrides.put("enableLobby", false);
            configOverrides.put("enableNoAudioDetection", false);
            configOverrides.put("startWithAudioMuted", false);
            configOverrides.put("startWithVideoMuted", false);
            configOverrides.put("disableModeratorIndicator", false);
            configOverrides.put("startScreenSharing", false);
            configOverrides.put("enableEmailInStats", false);
            configOverrides.put("disableShortcuts", false);
            configOverrides.put("disableInitialGUMPermissionPrompt", false);
            configOverrides.put("enableAutomaticUrlCopy", false);
            configOverrides.put("subject", "Medical Consultation");

            // Critical: Disable password and authentication
            configOverrides.put("enableUserRolesBasedOnToken", false);
            configOverrides.put("enableClosePage", false);
            configOverrides.put("defaultLanguage", "en");
            configOverrides.put("disableThirdPartyRequests", true);

            JitsiMeetConferenceOptions defaultOptions = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(serverURL)
                    // Feature flags to disable security
                    .setFeatureFlag("meeting-password.enabled", false)
                    .setFeatureFlag("lobby-mode.enabled", false)
                    .setFeatureFlag("add-people.enabled", false)
                    .setFeatureFlag("invite.enabled", false)
                    .setFeatureFlag("live-streaming.enabled", false)
                    .setFeatureFlag("recording.enabled", false)
                    .setFeatureFlag("calendar.enabled", false)
                    .setFeatureFlag("close-captions.enabled", false)
                    .setFeatureFlag("pip.enabled", true)
                    .setFeatureFlag("raise-hand.enabled", true)
                    .setFeatureFlag("video-share.enabled", true)
                    .setFeatureFlag("filmstrip.enabled", true)
                    .setFeatureFlag("feedback.enabled", false)
                    .setFeatureFlag("toolbox.alwaysVisible", false)
                    .setFeatureFlag("resolutions.enabled", true)
                    // Config overrides
                    .setConfigOverride("prejoinPageEnabled", false)
                    .setConfigOverride("requireDisplayName", false)
                    .setConfigOverride("enableWelcomePage", false)
                    .setConfigOverride("enableLobby", false)
                    .setConfigOverride("startWithAudioMuted", false)
                    .setConfigOverride("startWithVideoMuted", false)
                    .setConfigOverride("disableThirdPartyRequests", true)
                    .setConfigOverride("enableClosePage", false)
                    .setConfigOverride("disableInviteFunctions", true)
                    .setConfigOverride("toolbarButtons", new String[]{
                            "microphone", "camera", "closedcaptions", "desktop", "fullscreen",
                            "fodeviceselection", "hangup", "profile", "chat", "recording",
                            "livestreaming", "etherpad", "sharedvideo", "settings", "raisehand",
                            "videoquality", "filmstrip", "invite", "feedback", "stats", "shortcuts",
                            "tileview", "videobackgroundblur", "download", "help", "mute-everyone",
                            "mute-video-everyone", "security"
                    })
                    .build();

            JitsiMeet.setDefaultConferenceOptions(defaultOptions);
            Log.d(TAG, "Jitsi Meet initialized successfully with public server");

            // Start the video call
            startVideoCall();

        } catch (MalformedURLException e) {
            Log.e(TAG, "Invalid server URL: " + e.getMessage());
            showErrorAndFinish("Invalid server configuration");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Jitsi Meet: " + e.getMessage(), e);
            showErrorAndFinish("Failed to initialize video conference");
        }
    }

    private void startVideoCall() {
        try {
            String patientName = getPatientName();

            // Generate unique room name - make it simple
            String roomName = "BarangayHealth" + System.currentTimeMillis();

            Log.d(TAG, "=== Starting Video Call on Public Jitsi ===");
            Log.d(TAG, "Room: " + roomName);
            Log.d(TAG, "Patient: " + patientName);
            Log.d(TAG, "Doctor: " + doctorName);
            Log.d(TAG, "========================");

            // Create user info
            JitsiMeetUserInfo userInfo = new JitsiMeetUserInfo();
            userInfo.setDisplayName(patientName);

            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setRoom(roomName)
                    .setUserInfo(userInfo)
                    .setSubject("Medical Consultation with " + (doctorName != null ? doctorName : "Doctor"))
                    .setAudioMuted(false)
                    .setVideoMuted(false)
                    .setAudioOnly(false)
                    // Aggressively disable all security features
                    .setFeatureFlag("meeting-password.enabled", false)
                    .setFeatureFlag("lobby-mode.enabled", false)
                    .setFeatureFlag("prejoinpage.enabled", false)
                    .setFeatureFlag("welcomepage.enabled", false)
                    .setFeatureFlag("invite.enabled", false)
                    .setFeatureFlag("add-people.enabled", false)
                    .setFeatureFlag("live-streaming.enabled", false)
                    .setFeatureFlag("recording.enabled", false)
                    .setFeatureFlag("calendar.enabled", false)
                    .setFeatureFlag("close-captions.enabled", false)
                    .setFeatureFlag("pip.enabled", true)
                    .setFeatureFlag("filmstrip.enabled", true)
                    .setFeatureFlag("toolbox.alwaysVisible", false)
                    .setFeatureFlag("resolution", 360)
                    // Critical config overrides
                    .setConfigOverride("prejoinPageEnabled", false)
                    .setConfigOverride("requireDisplayName", false)
                    .setConfigOverride("enableWelcomePage", false)
                    .setConfigOverride("enableLobby", false)
                    .setConfigOverride("startWithAudioMuted", false)
                    .setConfigOverride("startWithVideoMuted", false)
                    .setConfigOverride("disableThirdPartyRequests", true)
                    .setConfigOverride("enableClosePage", false)
                    .setConfigOverride("disableInviteFunctions", true)
                    // Remove security toolbar button
                    .setConfigOverride("toolbarButtons", new String[]{
                            "microphone", "camera", "closedcaptions", "desktop", "fullscreen",
                            "fodeviceselection", "hangup", "profile", "chat", "recording",
                            "livestreaming", "etherpad", "sharedvideo", "settings", "raisehand",
                            "videoquality", "filmstrip", "invite", "feedback", "stats", "shortcuts",
                            "tileview", "videobackgroundblur", "download", "help", "mute-everyone",
                            "mute-video-everyone"
                            // "security" button removed from the list
                    })
                    .build();

            // Launch Jitsi Meet Activity
            JitsiMeetActivity.launch(this, options);

            Log.d(TAG, "Jitsi Meet activity launched successfully");

            // Finish this activity since Jitsi takes over
            finish();

        } catch (Exception e) {
            Log.e(TAG, "Error starting video call: " + e.getMessage(), e);
            showErrorAndFinish("Failed to start video conference: " + e.getMessage());
        }
    }

    private String getPatientName() {
        try {
            android.content.SharedPreferences prefs = getSharedPreferences("PatientData", MODE_PRIVATE);
            String firstName = prefs.getString("first_name", "Patient");
            String lastName = prefs.getString("last_name", "");
            String patientId = prefs.getString("patient_id", "");

            Log.d(TAG, "Patient data - First: " + firstName + ", Last: " + lastName + ", ID: " + patientId);

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