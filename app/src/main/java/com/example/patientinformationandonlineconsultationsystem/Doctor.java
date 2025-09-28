package com.example.patientinformationandonlineconsultationsystem;

public class Doctor {
    private String name;
    private String specialization;
    private String experience;
    private String contact;
    private String email;

    public Doctor(String name, String specialization, String experience, String contact, String email) {
        this.name = name;
        this.specialization = specialization;
        this.experience = experience;
        this.contact = contact;
        this.email = email;
    }

    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public String getExperience() { return experience; }
    public String getContact() { return contact; }
    public String getEmail() { return email; }
}
