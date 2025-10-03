package com.example.patientinformationandonlineconsultationsystem;

public class Consultation {
    private int id;
    private String date;
    private String time;
    private String status;
    private String diagnosis;
    private String treatment;

    private Doctor doctor;

    public Consultation(int id, String date, String time, String status, String diagnosis, String treatment, Doctor doctor) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.status = status;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.doctor = doctor;
    }

    public int getId() { return id; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getStatus() { return status; }
    public String getDiagnosis() { return diagnosis; }
    public String getTreatment() { return treatment; }
    public Doctor getDoctor() { return doctor; }
}
