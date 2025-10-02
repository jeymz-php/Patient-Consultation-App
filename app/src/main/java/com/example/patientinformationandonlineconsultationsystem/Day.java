package com.example.patientinformationandonlineconsultationsystem;

public class Day {
    private int day;
    private int month;
    private int year;
    private boolean isSelected;

    public Day(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.isSelected = false;
    }

    public int getDay() { return day; }
    public int getMonth() { return month; }
    public int getYear() { return year; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}
