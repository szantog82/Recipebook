package com.example.szantog.recipebook.models;

public class WeeklyMenu {

    private String _id;
    private String monday;
    private String tuesday;
    private String wednesday;
    private String thursday;
    private String friday;
    private String saturday;
    private String sunday;
    private String mondayDinner;
    private String tuesdayDinner;
    private String wednesdayDinner;
    private String thursdayDinner;
    private String fridayDinner;
    private String saturdayDinner;
    private String sundayDinner;
    private long time;

    public WeeklyMenu(String _id, String monday, String tuesday, String wednesday, String thursday, String friday, String saturday, String sunday, String mondayDinner, String tuesdayDinner, String wednesdayDinner, String thursdayDinner, String fridayDinner, String saturdayDinner, String sundayDinner, long time) {
        this._id = _id;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
        this.mondayDinner = mondayDinner;
        this.tuesdayDinner = tuesdayDinner;
        this.wednesdayDinner = wednesdayDinner;
        this.thursdayDinner = thursdayDinner;
        this.fridayDinner = fridayDinner;
        this.saturdayDinner = saturdayDinner;
        this.sundayDinner = sundayDinner;
        this.time = time;
    }

    public String get_id() {
        return _id;
    }

    public String getMonday() {
        return monday;
    }

    public String getTuesday() {
        return tuesday;
    }

    public String getWednesday() {
        return wednesday;
    }

    public String getThursday() {
        return thursday;
    }

    public String getFriday() {
        return friday;
    }

    public String getSaturday() {
        return saturday;
    }

    public String getSunday() {
        return sunday;
    }

    public String getMondayDinner() {
        return mondayDinner;
    }

    public String getTuesdayDinner() {
        return tuesdayDinner;
    }

    public String getWednesdayDinner() {
        return wednesdayDinner;
    }

    public String getThursdayDinner() {
        return thursdayDinner;
    }

    public String getFridayDinner() {
        return fridayDinner;
    }

    public String getSaturdayDinner() {
        return saturdayDinner;
    }

    public String getSundayDinner() {
        return sundayDinner;
    }

    public long getTime() {
        return time;
    }

    public void setMondayDinner(String mondayDinner) {
        this.mondayDinner = mondayDinner;
    }

    public void setTuesdayDinner(String tuesdayDinner) {
        this.tuesdayDinner = tuesdayDinner;
    }

    public void setWednesdayDinner(String wednesdayDinner) {
        this.wednesdayDinner = wednesdayDinner;
    }

    public void setThursdayDinner(String thursdayDinner) {
        this.thursdayDinner = thursdayDinner;
    }

    public void setFridayDinner(String fridayDinner) {
        this.fridayDinner = fridayDinner;
    }

    public void setSaturdayDinner(String saturdayDinner) {
        this.saturdayDinner = saturdayDinner;
    }

    public void setSundayDinner(String sundayDinner) {
        this.sundayDinner = sundayDinner;
    }
}
