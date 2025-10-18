package com.activity.communityhealthcare;

public class ApiConfig {
    // Change this to your actual server URL
    public static final String BASE_URL = "http://192.168.100.2/communityhealthcare/app/";
    public static final String VALIDATE_TRACKING = BASE_URL + "validate_tracking.php";
    public static final String GET_ALL_TRACKING = BASE_URL + "get_all_tracking_numbers.php";
    public static final String SAVE_APPOINTMENT = BASE_URL + "save_appointment.php";
    public static final String CHECK_AVAILABILITY = BASE_URL + "check_appointment_availability.php";
    public static final String GET_CONSULTATION_LOGS = BASE_URL + "get_consultation_logs.php";
    public static final String UPDATE_PATIENT = BASE_URL + "update_patient.php";
    public static final String REGISTER_PATIENT = BASE_URL + "register_patient.php";

    // For testing with local server (using 10.0.2.2 for Android emulator)
    // public static final String BASE_URL = "http://10.0.2.2/your-project-folder/";
}