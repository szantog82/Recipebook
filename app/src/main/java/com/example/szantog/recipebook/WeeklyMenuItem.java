package com.example.szantog.recipebook;

/**
 * Created by szantog on 2018.04.26..
 */

public class WeeklyMenuItem {

    private String lunch;
    private String dinner;

    public WeeklyMenuItem(String lunch, String dinner) {
        this.lunch = lunch;
        this.dinner = dinner;
    }

    public String getLunch() {
        return lunch;
    }

    public void setLunch(String lunch) {
        this.lunch = lunch;
    }

    public String getDinner() {
        return dinner;
    }

    public void setDinner(String dinner) {
        this.dinner = dinner;
    }
}
