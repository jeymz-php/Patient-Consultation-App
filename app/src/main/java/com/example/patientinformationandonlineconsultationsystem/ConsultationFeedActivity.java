package com.example.patientinformationandonlineconsultationsystem;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ConsultationFeedActivity extends AppCompatActivity {

    private ImageButton btnMic, btnEndCall, btnCamera;
    private FrameLayout localVideoContainer, doctorVideoContainer;

    private boolean isMicOn = true;
    private boolean isCameraOn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_consultation_feed);

        // Edge to edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // UI references
        btnMic = findViewById(R.id.btnMic);
        btnEndCall = findViewById(R.id.btnEndCall);
        btnCamera = findViewById(R.id.btnCamera);
        localVideoContainer = findViewById(R.id.localVideoContainer);
        doctorVideoContainer = findViewById(R.id.doctorVideoContainer);

        // Mic toggle
        btnMic.setOnClickListener(v -> {
            isMicOn = !isMicOn;
            btnMic.setAlpha(isMicOn ? 1f : 0.5f);
            Toast.makeText(this, isMicOn ? "Mic Unmuted" : "Mic Muted", Toast.LENGTH_SHORT).show();
            // TODO: Connect to actual audio stream
        });

        // Camera toggle
        btnCamera.setOnClickListener(v -> {
            isCameraOn = !isCameraOn;
            btnCamera.setAlpha(isCameraOn ? 1f : 0.5f);
            Toast.makeText(this, isCameraOn ? "Camera On" : "Camera Off", Toast.LENGTH_SHORT).show();
            // TODO: Connect to actual video stream
        });

        // End call with confirmation
        btnEndCall.setOnClickListener(v -> showEndCallConfirmation());
    }

    private void showEndCallConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("End Consultation")
                .setMessage("Are you sure you want to end this consultation?")
                .setPositiveButton("Yes", (dialog, which) -> finish()) // TODO: navigate to MainActivity if needed
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }
}
