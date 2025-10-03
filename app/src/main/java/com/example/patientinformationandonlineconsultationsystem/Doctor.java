package com.example.patientinformationandonlineconsultationsystem;

public class Doctor {
    private int id;  // add this
    private String name;
    private String specialization;
    private String experience;
    private String contact;
    private String email;

    // Updated constructor with ID
    public Doctor(int id, String name, String specialization, String experience, String contact, String email) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.experience = experience;
        this.contact = contact;
        this.email = email;
    }

    // Getters
    public int getId() { return id; }  // new
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public String getExperience() { return experience; }
    public String getContact() { return contact; }
    public String getEmail() { return email; }
}
