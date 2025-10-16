package com.activity.communityhealthcare;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.Arrays;
import java.util.List;

public class LandingPageActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private MaterialButton btnNext;
    private LinearLayout dotsLayout;
    private View txtSkip;
    private LinearLayout bottomSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set status bar color programmatically - ADD THIS
        setDarkStatusBar();

        // Rest of your existing code...
        SharedPreferences prefs = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        boolean onboardingCompleted = prefs.getBoolean("onboarding_completed", false);

        if (onboardingCompleted) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_landing_page);
        initializeViews();
        setupOnboardingItems();
        setupDotsIndicator();
        setupClickListeners();
    }

    private void setDarkStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For Android M and above - set dark status bar icons
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Set the status bar color
            getWindow().setStatusBarColor(Color.parseColor("#C96A00"));
        }
    }

    private void initializeViews() {
        viewPager = findViewById(R.id.viewPager);
        btnNext = findViewById(R.id.btnNext);
        dotsLayout = findViewById(R.id.dotsLayout);
        txtSkip = findViewById(R.id.txtSkip);
        bottomSection = findViewById(R.id.bottomSection);
    }

    private void setupSystemInsets() {
        // Handle system window insets for modern Android
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
                int navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;

                // Apply padding to avoid overlaps
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) bottomSection.getLayoutParams();
                mlp.bottomMargin = navigationBarHeight + 16; // 16dp original margin
                bottomSection.setLayoutParams(mlp);

                // Add top margin to ViewPager to avoid status bar
                ViewGroup.MarginLayoutParams vpMlp = (ViewGroup.MarginLayoutParams) viewPager.getLayoutParams();
                vpMlp.topMargin = statusBarHeight;
                viewPager.setLayoutParams(vpMlp);
            } else {
                // For older versions, use a safe area
                int statusBarHeight = getStatusBarHeight();
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) bottomSection.getLayoutParams();
                mlp.bottomMargin = getNavigationBarHeight() + 16;
                bottomSection.setLayoutParams(mlp);

                ViewGroup.MarginLayoutParams vpMlp = (ViewGroup.MarginLayoutParams) viewPager.getLayoutParams();
                vpMlp.topMargin = statusBarHeight;
                viewPager.setLayoutParams(vpMlp);
            }
            return insets;
        });
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private int getNavigationBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void setupOnboardingItems() {
        List<OnboardingItem> onboardingItems = Arrays.asList(
                new OnboardingItem(
                        R.drawable.d_3d_calendar,
                        "Easy Scheduling",
                        "Book consultations with doctors at your convenience. Schedule appointments in just a few taps."
                ),
                new OnboardingItem(
                        R.drawable.d_3d_mobile,
                        "Online Consultations",
                        "Connect with healthcare professionals through secure video calls from the comfort of your home."
                ),
                new OnboardingItem(
                        R.drawable.d_3d_heart,
                        "Health Management",
                        "Keep track of your medical records, prescriptions, and health history all in one place."
                )
        );

        OnboardingAdapter onboardingAdapter = new OnboardingAdapter(onboardingItems);
        viewPager.setAdapter(onboardingAdapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateDotsIndicator(position);

                if (position == onboardingItems.size() - 1) {
                    btnNext.setText("Get Started");
                    txtSkip.setVisibility(View.GONE);
                } else {
                    btnNext.setText("Next");
                    txtSkip.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setupDotsIndicator() {
        int dotsCount = 3;
        ImageView[] dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                    R.drawable.dot_inactive, null));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dotsLayout.addView(dots[i], params);
        }

        updateDotsIndicator(0);
    }

    private void updateDotsIndicator(int position) {
        int childCount = dotsLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView dot = (ImageView) dotsLayout.getChildAt(i);
            if (i == position) {
                dot.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.dot_active, null));
            } else {
                dot.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.dot_inactive, null));
            }
        }
    }

    private void setupClickListeners() {
        btnNext.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < 2) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                showTermsAndConditionsDialog();
            }
        });

        txtSkip.setOnClickListener(v -> {
            showTermsAndConditionsDialog();
        });
    }

    private void showTermsAndConditionsDialog() {
        try {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_terms_and_conditions, null);

            CheckBox checkBoxTerms = dialogView.findViewById(R.id.checkBoxTerms);
            MaterialButton btnProceed = dialogView.findViewById(R.id.btnProceed);

            btnProceed.setEnabled(false);

            androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                    .setView(dialogView)
                    .setCancelable(false)
                    .create();

            // Set proper dialog window properties for modal size
            // Set proper dialog window properties for modal size
            dialog.setOnShowListener(dialogInterface -> {
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);

                    // Set proper modal dimensions with min/max constraints
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                    int maxWidth = (int) (displayMetrics.widthPixels * 0.90);
                    int minWidth = (int) (displayMetrics.widthPixels * 0.70);
                    int width = Math.min(maxWidth, Math.max(minWidth, (int) (displayMetrics.widthPixels * 0.85)));

                    dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

                    // Center the dialog
                    dialog.getWindow().setGravity(Gravity.CENTER);
                }
            });

            // Only check for Terms and Conditions checkbox
            checkBoxTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
                btnProceed.setEnabled(isChecked);
                if (isChecked) {
                    animateButtonPulse(btnProceed);
                }
            });

            btnProceed.setOnClickListener(v -> {
                // Save onboarding completion
                SharedPreferences prefs = getSharedPreferences("AppPreferences", MODE_PRIVATE);
                prefs.edit().putBoolean("onboarding_completed", true).apply();

                dialog.dismiss();
                navigateToMainActivity();
            });

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            navigateToMainActivity();
        }
    }

    private void animateButtonPulse(MaterialButton button) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 1.05f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 1.05f, 1f);

        AnimatorSet pulseAnimator = new AnimatorSet();
        pulseAnimator.playTogether(scaleX, scaleY);
        pulseAnimator.setDuration(300);
        pulseAnimator.start();
    }

    private void navigateToMainActivity() {
        startActivity(new Intent(LandingPageActivity.this, DashboardActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}