package com.activity.communityhealthcare;

public class TimeSlot {
    private String time;
    private boolean available;

    public TimeSlot(String time, boolean available) {
        this.time = time;
        this.available = available;
    }

    public String getTime() {
        return time;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}