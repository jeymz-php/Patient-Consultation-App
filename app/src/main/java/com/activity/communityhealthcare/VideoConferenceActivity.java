package com.activity.communityhealthcare;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class VideoConferenceActivity extends AppCompatActivity {

    private static final String TAG = "VideoConferenceDebug";

    // UI Components
    private MaterialButton btnBack, btnMute, btnVideo, btnEndCall, btnSwitchCamera, btnMore;
    private SurfaceView surfaceRemoteVideo, surfaceLocalVideo;
    private TextView txtDoctorName, txtDoctorSpecialty, txtCallStatus, txtCallTimer, txtRemoteParticipant;
    private View layoutVideoOff, layoutLocalVideoOff, layoutConnectionQuality;
    private TextView txtConnectionQuality;
    private ImageView imgConnectionQuality;

    // Conference state
    private boolean isMuted = false;
    private boolean isVideoOn = true;
    private boolean isFrontCamera = true;
    private boolean isCallActive = false;

    // Timer
    private CountDownTimer callTimer;
    private long callDuration = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting VideoConferenceActivity");

        setFullScreenMode();
        setContentView(R.layout.activity_video_conference);
        setupWindowInsets();

        initializeViews();
        setupCall();
        setupControls();

        Log.d(TAG, "Video conference setup complete");
    }

    private void setFullScreenMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.parseColor("#0F172A"));
            window.setNavigationBarColor(Color.parseColor("#1E293B"));

            // Make content appear behind the status bar
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }

        // Keep screen on during call
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content),
                (v, insets) -> {
                    // Simple insets handling without the problematic controlsLayout
                    return WindowInsetsCompat.CONSUMED;
                });
    }

    private void initializeViews() {
        Log.d(TAG, "Initializing video conference views...");

        // Header views
        btnBack = findViewById(R.id.btnBack);
        txtDoctorName = findViewById(R.id.txtDoctorName);
        txtDoctorSpecialty = findViewById(R.id.txtDoctorSpecialty);
        txtCallStatus = findViewById(R.id.txtCallStatus);

        // Video views
        surfaceRemoteVideo = findViewById(R.id.surfaceRemoteVideo);
        surfaceLocalVideo = findViewById(R.id.surfaceLocalVideo);
        layoutVideoOff = findViewById(R.id.layoutVideoOff);
        layoutLocalVideoOff = findViewById(R.id.layoutLocalVideoOff);
        txtRemoteParticipant = findViewById(R.id.txtRemoteParticipant);

        // Connection quality
        layoutConnectionQuality = findViewById(R.id.layoutConnectionQuality);
        txtConnectionQuality = findViewById(R.id.txtConnectionQuality);
        imgConnectionQuality = findViewById(R.id.imgConnectionQuality);

        // Control views
        txtCallTimer = findViewById(R.id.txtCallTimer);
        btnMute = findViewById(R.id.btnMute);
        btnVideo = findViewById(R.id.btnVideo);
        btnEndCall = findViewById(R.id.btnEndCall);
        btnSwitchCamera = findViewById(R.id.btnSwitchCamera);
        btnMore = findViewById(R.id.btnMore);

        Log.d(TAG, "All views initialized successfully");
    }

    private void setupCall() {
        Log.d(TAG, "Setting up video call...");

        // Get doctor info from intent
        Intent intent = getIntent();
        String doctorName = intent.getStringExtra("doctor_name");
        String doctorSpecialty = intent.getStringExtra("doctor_specialty");

        if (doctorName != null) {
            txtDoctorName.setText(doctorName);
            txtRemoteParticipant.setText(doctorName);
        }
        if (doctorSpecialty != null) {
            txtDoctorSpecialty.setText(doctorSpecialty);
        }

        // Simulate call connection
        new Handler().postDelayed(() -> {
            runOnUiThread(() -> {
                txtCallStatus.setText("Connected");
                txtCallStatus.setTextColor(Color.parseColor("#FFA726"));
                isCallActive = true;
                startCallTimer();
                showConnectionQuality();
                playConnectionSound();
            });
        }, 2000);

        Log.d(TAG, "Call setup completed");
    }

    private void setupControls() {
        Log.d(TAG, "Setting up control buttons...");

        // Back button
        btnBack.setOnClickListener(v -> {
            showEndCallConfirmation();
        });

        // Mute button
        btnMute.setOnClickListener(v -> {
            toggleMute();
        });

        // Video button
        btnVideo.setOnClickListener(v -> {
            toggleVideo();
        });

        // End call button
        btnEndCall.setOnClickListener(v -> {
            endCall();
        });

        // Switch camera button
        btnSwitchCamera.setOnClickListener(v -> {
            switchCamera();
        });

        // More options button
        btnMore.setOnClickListener(v -> {
            showMoreOptions();
        });

        Log.d(TAG, "Control buttons setup completed");
    }

    private void toggleMute() {
        isMuted = !isMuted;

        if (isMuted) {
            btnMute.setIconResource(R.drawable.ic_mic_off);
            btnMute.setBackgroundTintList(getColorStateList(R.color.muted_state_orange));
            Toast.makeText(this, "Microphone muted", Toast.LENGTH_SHORT).show();
        } else {
            btnMute.setIconResource(R.drawable.ic_mic_on);
            btnMute.setBackgroundTintList(getColorStateList(R.color.normal_state));
            Toast.makeText(this, "Microphone on", Toast.LENGTH_SHORT).show();
        }

        // TODO: Implement actual mute functionality with your video SDK
        Log.d(TAG, "Microphone " + (isMuted ? "muted" : "unmuted"));
    }

    private void toggleVideo() {
        isVideoOn = !isVideoOn;

        if (isVideoOn) {
            btnVideo.setIconResource(R.drawable.ic_video_on);
            btnVideo.setBackgroundTintList(getColorStateList(R.color.normal_state));
            surfaceLocalVideo.setVisibility(View.VISIBLE);
            layoutLocalVideoOff.setVisibility(View.GONE);
            Toast.makeText(this, "Video on", Toast.LENGTH_SHORT).show();
        } else {
            btnVideo.setIconResource(R.drawable.ic_video_off);
            btnVideo.setBackgroundTintList(getColorStateList(R.color.muted_state_orange));
            surfaceLocalVideo.setVisibility(View.GONE);
            layoutLocalVideoOff.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Video off", Toast.LENGTH_SHORT).show();
        }

        // TODO: Implement actual video toggle functionality with your video SDK
        Log.d(TAG, "Video " + (isVideoOn ? "enabled" : "disabled"));
    }

    private void switchCamera() {
        isFrontCamera = !isFrontCamera;

        if (isFrontCamera) {
            Toast.makeText(this, "Switched to front camera", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Switched to rear camera", Toast.LENGTH_SHORT).show();
        }

        // TODO: Implement actual camera switching with your video SDK
        Log.d(TAG, "Switched to " + (isFrontCamera ? "front" : "rear") + " camera");
    }

    private void showMoreOptions() {
        // TODO: Implement more options menu (chat, share screen, etc.)
        Toast.makeText(this, "More options coming soon", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "More options requested");
    }

    private void startCallTimer() {
        callTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                callDuration += 1000;
                updateCallTimer();
            }

            @Override
            public void onFinish() {
                // Timer finished (shouldn't happen with Long.MAX_VALUE)
            }
        };
        callTimer.start();
    }

    private void updateCallTimer() {
        long seconds = callDuration / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;

        String timeString = String.format("%02d:%02d", minutes, seconds);
        txtCallTimer.setText(timeString);
    }

    private void showConnectionQuality() {
        layoutConnectionQuality.setVisibility(View.VISIBLE);

        // Simulate connection quality changes
        new Handler().postDelayed(() -> {
            runOnUiThread(() -> {
                txtConnectionQuality.setText("Excellent");
                if (imgConnectionQuality != null) {
                    imgConnectionQuality.setColorFilter(Color.parseColor("#FFA726"));
                }
            });
        }, 1000);
    }

    private void playConnectionSound() {
        try {
            // TODO: Play connection sound if needed
            // MediaPlayer.create(this, R.raw.connection_sound).start();
        } catch (Exception e) {
            Log.e(TAG, "Error playing connection sound", e);
        }
    }

    private void showEndCallConfirmation() {
        if (isCallActive) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("End Call")
                    .setMessage("Are you sure you want to end the video consultation?")
                    .setPositiveButton("End Call", (dialog, which) -> endCall())
                    .setNegativeButton("Continue", null)
                    .show();
        } else {
            finish();
        }
    }

    private void endCall() {
        Log.d(TAG, "Ending video call...");

        if (callTimer != null) {
            callTimer.cancel();
        }

        isCallActive = false;

        // TODO: Implement actual call termination with your video SDK

        // Show call ended message
        Toast.makeText(this, "Call ended - Duration: " + txtCallTimer.getText(), Toast.LENGTH_LONG).show();

        // Return to previous activity
        new Handler().postDelayed(() -> {
            finish();
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "VideoConferenceActivity destroyed");

        if (callTimer != null) {
            callTimer.cancel();
        }

        if (isCallActive) {
            endCall();
        }
    }

    @Override
    public void onBackPressed() {
        showEndCallConfirmation();
    }
}