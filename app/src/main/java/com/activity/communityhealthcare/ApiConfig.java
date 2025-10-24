package com.activity.communityhealthcare;

public class ApiConfig {
    // Base URL for your live API
    public static final String BASE_URL = "https://communityhealthcare.bsitfoura.com/api/";

    // Correct API Endpoints (using your original PHP files)
    public static final String VALIDATE_TRACKING = BASE_URL + "checkTrackingID.php";
    public static final String GET_ALL_TRACKING = BASE_URL + "checkTrackingID.php";
    public static final String SAVE_APPOINTMENT = BASE_URL + "addSchedule.php";
    public static final String REGISTER_PATIENT = BASE_URL + "addPatient.php";
    public static final String GET_CONSULTATION_LOGS = BASE_URL + "getSchedulesMeet.php";
    public static final String CHECK_AVAILABILITY = BASE_URL + "getSchedulesMeet.php";

    // Optional: Barangay list (excluded as per your instruction)
    // public static final String GET_BARANGAY = BASE_URL + "getBarangay.php";
}
