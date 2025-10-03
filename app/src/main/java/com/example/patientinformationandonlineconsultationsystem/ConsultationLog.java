package com.example.patientinformationandonlineconsultationsystem;

public class ConsultationLog {
    private String doctorName;
    private String doctorSpecialty;
    private String date;
    private String time;
    private String status;
    private String diagnosis;
    private String treatment;

    public ConsultationLog(String doctorName, String doctorSpecialty, String date, String time,
                           String status, String diagnosis, String treatment) {
        this.doctorName = doctorName;
        this.doctorSpecialty = doctorSpecialty;
        this.date = date;
        this.time = time;
        this.status = status;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
    }

    public String getDoctorName() { return doctorName; }
    public String getDoctorSpecialty() { return doctorSpecialty; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getStatus() { return status; }
    public String getDiagnosis() { return diagnosis; }
    public String getTreatment() { return treatment; }
}
