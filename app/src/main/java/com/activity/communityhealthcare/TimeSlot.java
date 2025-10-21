package com.activity.communityhealthcare;

public class TimeSlot {
    private String time;
    private boolean available;

    public TimeSlot(String time, boolean available) {
        this.time = time;
        this.available = available;
    }

    // Getters
    public String getTime() { return time; }
    public boolean isAvailable() { return available; }

    // Setter for availability
    public void setAvailable(boolean available) {
        this.available = available;
    }
}