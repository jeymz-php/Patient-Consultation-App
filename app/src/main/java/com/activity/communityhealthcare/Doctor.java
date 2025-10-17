package com.activity.communityhealthcare;

public class Doctor {
    private String id;
    private String name;
    private String specialty;
    private String availability;
    private int avatarResId;

    public Doctor(String id, String name, String specialty, String availability, int avatarResId) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.availability = availability;
        this.avatarResId = avatarResId;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getSpecialty() { return specialty; }
    public String getAvailability() { return availability; }
    public int getAvatarResId() { return avatarResId; }
}