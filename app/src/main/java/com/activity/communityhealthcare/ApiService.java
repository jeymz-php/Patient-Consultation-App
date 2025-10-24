package com.activity.communityhealthcare;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiService {
    private RequestQueue requestQueue;
    private Context context;

    public ApiService(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public interface ApiResponseListener {
        void onSuccess(JSONObject response);
        void onError(String error);
    }

    // ✅ 1. Validate Tracking Number  → checkTrackingID.php
    public void validateTrackingNumber(String trackingNumber, ApiResponseListener listener) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                ApiConfig.VALIDATE_TRACKING,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        listener.onSuccess(jsonResponse);
                    } catch (JSONException e) {
                        listener.onError("Invalid JSON: " + e.getMessage());
                    }
                },
                error -> {
                    String errorMessage = "Network error";
                    if (error.getMessage() != null) {
                        errorMessage = error.getMessage();
                    }
                    listener.onError(errorMessage);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tracking_number", trackingNumber);
                Log.d("ApiService", "Sending tracking_number: " + trackingNumber);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(request);
    }

    // ✅ Restored getAllTrackingNumbers() — uses checkTrackingID.php and returns JSONObject
    public void getAllTrackingNumbers(final ApiResponseListener listener) {
        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.VALIDATE_TRACKING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            listener.onSuccess(jsonResponse);
                        } catch (JSONException e) {
                            listener.onError("Invalid JSON response: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Network error";
                        if (error.getMessage() != null) {
                            errorMessage = error.getMessage();
                        }
                        listener.onError(errorMessage);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // 🧠 No parameters — fetch all tracking IDs
                return new HashMap<>();
            }
        };

        // Optional: retry policy for reliability
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(request);
    }

    // ✅ 2. Add Schedule Consultation  → addSchedule.php
    public void saveAppointment(String patientId, String trackingNumber,
                                String doctorName, String doctorSpecialty,
                                String appointmentDate, String appointmentTime,
                                ApiResponseListener listener) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("patient_id", patientId);
            requestBody.put("tracking_number", trackingNumber);
            requestBody.put("doctor_name", doctorName);
            requestBody.put("doctor_specialty", doctorSpecialty);
            requestBody.put("appointment_date", appointmentDate);
            requestBody.put("appointment_time", appointmentTime);

            Log.d("ApiService", "Saving appointment: " + requestBody.toString());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiConfig.SAVE_APPOINTMENT,
                    requestBody,
                    response -> {
                        Log.d("ApiService", "Appointment response: " + response.toString());
                        listener.onSuccess(response);
                    },
                    error -> handleError("saveAppointment", error, listener)
            );

            applyRetryPolicy(jsonObjectRequest);
            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e) {
            listener.onError("JSON error: " + e.getMessage());
        }
    }

    // ✅ 3. Register/Add Patient → addPatient.php
    public void registerPatient(String fullName, String age, String gender,
                                String contactNumber, String address,
                                ApiResponseListener listener) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("fullname", fullName);
            requestBody.put("age", age);
            requestBody.put("gender", gender);
            requestBody.put("contact", contactNumber);
            requestBody.put("address", address);

            Log.d("ApiService", "Registering patient: " + requestBody.toString());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiConfig.REGISTER_PATIENT,
                    requestBody,
                    response -> {
                        Log.d("ApiService", "Register patient response: " + response.toString());
                        listener.onSuccess(response);
                    },
                    error -> handleError("registerPatient", error, listener)
            );

            applyRetryPolicy(jsonObjectRequest);
            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e) {
            listener.onError("JSON error: " + e.getMessage());
        }
    }

    // ✅ 4. Get Consultation Logs → getSchedulesMeet.php
    public void getConsultationLogs(String patientId, String trackingNumber, ApiResponseListener listener) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("patient_id", patientId);
            requestBody.put("tracking_number", trackingNumber);

            Log.d("ApiService", "Fetching consultation logs: " + requestBody.toString());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiConfig.GET_CONSULTATION_LOGS,
                    requestBody,
                    response -> {
                        Log.d("ApiService", "Consultation logs response: " + response.toString());
                        listener.onSuccess(response);
                    },
                    error -> handleError("getConsultationLogs", error, listener)
            );

            applyRetryPolicy(jsonObjectRequest);
            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e) {
            listener.onError("JSON error: " + e.getMessage());
        }
    }

    // ✅ checkAppointmentAvailability() now uses getSchedulesMeet.php
    public void checkAppointmentAvailability(String appointmentDate, String appointmentTime, ApiResponseListener listener) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("appointment_date", appointmentDate);
            requestBody.put("appointment_time", appointmentTime);

            Log.d("ApiService", "Checking appointment availability...");
            Log.d("ApiService", "Request body: " + requestBody.toString());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiConfig.GET_CONSULTATION_LOGS, // ✅ getSchedulesMeet.php
                    requestBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("ApiService", "Availability response: " + response.toString());
                            listener.onSuccess(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String errorMessage = "Network error";
                            if (error.networkResponse != null) {
                                int statusCode = error.networkResponse.statusCode;
                                String responseData = new String(error.networkResponse.data);
                                errorMessage = "Server error " + statusCode + ": " + responseData;
                                Log.e("ApiService", "Server error: " + statusCode + " - " + responseData);
                            } else if (error.getMessage() != null) {
                                errorMessage = "Network error: " + error.getMessage();
                            }
                            Log.e("ApiService", "Availability check error: " + errorMessage);
                            listener.onError(errorMessage);
                        }
                    }
            );

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e) {
            Log.e("ApiService", "JSON error", e);
            listener.onError("JSON error: " + e.getMessage());
        }
    }

    // ⚙️ Helper to handle errors consistently
    private void handleError(String tag, VolleyError error, ApiResponseListener listener) {
        String errorMessage = "Network error";
        if (error.networkResponse != null) {
            int statusCode = error.networkResponse.statusCode;
            String responseData = new String(error.networkResponse.data);
            errorMessage = "Server error " + statusCode + ": " + responseData;
            Log.e("ApiService", tag + " - Server error: " + statusCode + " - " + responseData);
        } else if (error.getMessage() != null) {
            errorMessage = "Network error: " + error.getMessage();
            Log.e("ApiService", tag + " - Network error: " + error.getMessage());
        } else {
            Log.e("ApiService", tag + " - Unknown network error");
        }
        error.printStackTrace();
        listener.onError(errorMessage);
    }

    // ⚙️ Helper to apply retry settings
    private void applyRetryPolicy(JsonObjectRequest request) {
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
    }
}
