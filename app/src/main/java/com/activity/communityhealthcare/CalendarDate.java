package com.activity.communityhealthcare;

public class CalendarDate {
    private String dayNumber;
    private String dayName;
    private boolean selectable;
    private boolean today;

    public CalendarDate(String dayNumber, String dayName, boolean selectable, boolean today) {
        this.dayNumber = dayNumber;
        this.dayName = dayName;
        this.selectable = selectable;
        this.today = today;
    }

    public String getDayNumber() {
        return dayNumber;
    }

    public String getDayName() {
        return dayName;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public boolean isToday() {
        return today;
    }
}