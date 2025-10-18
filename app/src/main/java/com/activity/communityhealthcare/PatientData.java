package com.activity.communityhealthcare;

public class PatientData {
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String gender;
    private String contactNumber;
    private String email;
    private String address;
    private String civilStatus;
    private String barangay;
    private String emergencyFirstName;
    private String emergencyLastName;
    private String emergencyRelationship;
    private String emergencyContactNumber;
    private String trackingNumber;
    private int patientId;
    private int residentId;

    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCivilStatus() { return civilStatus; }
    public void setCivilStatus(String civilStatus) { this.civilStatus = civilStatus; }

    public String getBarangay() { return barangay; }
    public void setBarangay(String barangay) { this.barangay = barangay; }

    public String getEmergencyFirstName() { return emergencyFirstName; }
    public void setEmergencyFirstName(String emergencyFirstName) { this.emergencyFirstName = emergencyFirstName; }

    public String getEmergencyLastName() { return emergencyLastName; }
    public void setEmergencyLastName(String emergencyLastName) { this.emergencyLastName = emergencyLastName; }

    public String getEmergencyRelationship() { return emergencyRelationship; }
    public void setEmergencyRelationship(String emergencyRelationship) { this.emergencyRelationship = emergencyRelationship; }

    public String getEmergencyContactNumber() { return emergencyContactNumber; }
    public void setEmergencyContactNumber(String emergencyContactNumber) { this.emergencyContactNumber = emergencyContactNumber; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getResidentId() { return residentId; }
    public void setResidentId(int residentId) { this.residentId = residentId; }
}