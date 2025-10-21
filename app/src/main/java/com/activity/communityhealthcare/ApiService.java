package com.activity.communityhealthcare;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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

        // Set default timeout for all requests
        this.requestQueue.getCache().initialize();
    }

    public interface ApiResponseListener {
        void onSuccess(JSONObject response);
        void onError(String error);
    }

    public void validateTrackingNumber(String trackingNumber, ApiResponseListener listener) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("tracking_number", trackingNumber);

            Log.d("ApiService", "Sending request to: " + ApiConfig.VALIDATE_TRACKING);
            Log.d("ApiService", "Request body: " + requestBody.toString());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiConfig.VALIDATE_TRACKING,
                    requestBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("ApiService", "Response received: " + response.toString());
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
                                Log.e("ApiService", "Network error: " + error.getMessage());
                            } else {
                                Log.e("ApiService", "Unknown network error");
                            }

                            // Log the full error for debugging
                            error.printStackTrace();
                            listener.onError(errorMessage);
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Accept", "application/json");
                    return headers;
                }
            };

            // Set timeout using RetryPolicy instead
            jsonObjectRequest.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                    30000, // 30 seconds timeout
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e) {
            Log.e("ApiService", "JSON error: " + e.getMessage());
            listener.onError("JSON error: " + e.getMessage());
        }
    }

    // Add method to get all tracking numbers (for debugging)
    public void getAllTrackingNumbers(ApiResponseListener listener) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                ApiConfig.GET_ALL_TRACKING,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("ApiService", "Tracking numbers response: " + response.toString());
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
                        } else if (error.getMessage() != null) {
                            errorMessage = "Network error: " + error.getMessage();
                        }

                        Log.e("ApiService", "Error getting tracking numbers: " + errorMessage);
                        listener.onError(errorMessage);
                    }
                }
        );

        // Set timeout for this request too
        jsonObjectRequest.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(jsonObjectRequest);
    }

    public void saveAppointment(String patientId, String trackingNumber, String doctorName,
                                String doctorSpecialty, String appointmentDate, String appointmentTime,
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
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("ApiService", "Appointment response: " + response.toString());
                            listener.onSuccess(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String errorMessage = "Network error";
                            if (error.getMessage() != null) {
                                errorMessage = "Network error: " + error.getMessage();
                            }
                            Log.e("ApiService", "Appointment save error: " + errorMessage);
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
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("ApiService", "Consultation logs response: " + response.toString());
                            listener.onSuccess(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String errorMessage = "Network error";
                            if (error.getMessage() != null) {
                                errorMessage = "Network error: " + error.getMessage();
                            }
                            Log.e("ApiService", "Consultation logs error: " + errorMessage);
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
    // Add this method to your ApiService class
    public void checkAppointmentAvailability(String appointmentDate, String appointmentTime, ApiResponseListener listener) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("appointment_date", appointmentDate);
            requestBody.put("appointment_time", appointmentTime);

            Log.d("ApiService", "Checking availability - Date: " + appointmentDate + ", Time: " + appointmentTime);
            Log.d("ApiService", "Sending request to: " + ApiConfig.CHECK_AVAILABILITY);
            Log.d("ApiService", "Request body: " + requestBody.toString());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiConfig.CHECK_AVAILABILITY,
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
}