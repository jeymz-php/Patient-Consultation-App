package com.activity.communityhealthcare;

public class ApiConfig {
    // Updated to your new hosting domain
    public static final String BASE_URL = "https://communityhealthcare.bsitfoura.com/app/";

    // API Endpoints
    public static final String VALIDATE_TRACKING = BASE_URL + "validate_tracking.php";
    public static final String GET_ALL_TRACKING = BASE_URL + "get_all_tracking_numbers.php";
    public static final String SAVE_APPOINTMENT = BASE_URL + "save_appointment.php";
    public static final String CHECK_AVAILABILITY = BASE_URL + "check_appointment_availability.php";
    public static final String GET_CONSULTATION_LOGS = BASE_URL + "get_consultation_logs.php";
    public static final String UPDATE_PATIENT = BASE_URL + "update_patient.php";
    public static final String REGISTER_PATIENT = BASE_URL + "register_patient.php";

    // Note: All endpoints now use HTTPS
}