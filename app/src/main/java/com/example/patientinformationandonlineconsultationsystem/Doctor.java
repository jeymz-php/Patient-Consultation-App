package com.example.patientinformationandonlineconsultationsystem;

public class Doctor {
    private int id;
    private String name;
    private String specialization;
    private String contact;
    private String email;

    // Constructor with ID
    public Doctor(int id, String name, String specialization, String contact, String email) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.contact = contact;
        this.email = email;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public String getContact() { return contact; }
    public String getEmail() { return email; }
}
