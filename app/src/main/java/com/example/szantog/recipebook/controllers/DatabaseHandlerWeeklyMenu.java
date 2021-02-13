package com.example.szantog.recipebook.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.szantog.recipebook.models.WeeklyMenu;


public class DatabaseHandlerWeeklyMenu extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "recipes";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_NAME = "weeklymenu";

    private static final String ID = "_id";
    private static final String MONDAY = "monday";
    private static final String TUESDAY = "tuesday";
    private static final String WEDNESDAY = "wednesday";
    private static final String THURSDAY = "thursday";
    private static final String FRIDAY = "friday";
    private static final String SATURDAY = "saturday";
    private static final String SUNDAY = "sunday";
    private static final String DINNER_TAG = "_dinner";
    private static final String MONDAY_DINNER = MONDAY + DINNER_TAG;
    private static final String TUESDAY_DINNER = TUESDAY + DINNER_TAG;
    private static final String WEDNESDAY_DINNER = WEDNESDAY + DINNER_TAG;
    private static final String THURSDAY_DINNER = THURSDAY + DINNER_TAG;
    private static final String FRIDAY_DINNER = FRIDAY + DINNER_TAG;
    private static final String SATURDAY_DINNER = SATURDAY + DINNER_TAG;
    private static final String SUNDAY_DINNER = SUNDAY + DINNER_TAG;
    private static final String TIME = "time";

    public static final String WEEKLYMENUTABLE_DEFINITION = "CREATE TABLE " + TABLE_NAME + " (" +
            ID + " TEXT PRIMARY KEY," +
            MONDAY + " TEXT," +
            TUESDAY + " TEXT," +
            WEDNESDAY + " TEXT," +
            THURSDAY + " TEXT," +
            FRIDAY + " TEXT," +
            SATURDAY + " TEXT," +
            SUNDAY + " TEXT," +
            MONDAY_DINNER + " TEXT," +
            TUESDAY_DINNER + " TEXT," +
            WEDNESDAY_DINNER + " TEXT," +
            THURSDAY_DINNER + " TEXT," +
            FRIDAY_DINNER + " TEXT," +
            SATURDAY_DINNER + " TEXT," +
            SUNDAY_DINNER + " TEXT," +
            TIME + " TEXT)";

    public DatabaseHandlerWeeklyMenu(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DatabaseHandlerRecipes.RECIPETABLE_DEFINITION);
        sqLiteDatabase.execSQL(WEEKLYMENUTABLE_DEFINITION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //
    }

    public WeeklyMenu getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        cursor.moveToFirst();
        String _id = cursor.getString(cursor.getColumnIndex(ID));
        String monday = cursor.getString(cursor.getColumnIndex(MONDAY));
        String tuesday = cursor.getString(cursor.getColumnIndex(TUESDAY));
        String wednesday = cursor.getString(cursor.getColumnIndex(WEDNESDAY));
        String thursday = cursor.getString(cursor.getColumnIndex(THURSDAY));
        String friday = cursor.getString(cursor.getColumnIndex(FRIDAY));
        String saturday = cursor.getString(cursor.getColumnIndex(SATURDAY));
        String sunday = cursor.getString(cursor.getColumnIndex(SUNDAY));
        String monday_dinner = cursor.getString(cursor.getColumnIndex(MONDAY_DINNER));
        String tuesday_dinner = cursor.getString(cursor.getColumnIndex(TUESDAY_DINNER));
        String wednesday_dinner = cursor.getString(cursor.getColumnIndex(WEDNESDAY_DINNER));
        String thursday_dinner = cursor.getString(cursor.getColumnIndex(THURSDAY_DINNER));
        String friday_dinner = cursor.getString(cursor.getColumnIndex(FRIDAY_DINNER));
        String saturday_dinner = cursor.getString(cursor.getColumnIndex(SATURDAY_DINNER));
        String sunday_dinner = cursor.getString(cursor.getColumnIndex(SUNDAY_DINNER));
        Long time = Long.parseLong(cursor.getString(cursor.getColumnIndex(TIME)));
        db.close();
        WeeklyMenu weeklyMenu = new WeeklyMenu(_id, monday, tuesday, wednesday, thursday, friday, saturday, sunday, monday_dinner, tuesday_dinner, wednesday_dinner, thursday_dinner, friday_dinner, saturday_dinner, sunday_dinner, time);
        return weeklyMenu;
    }

    public void insertData(WeeklyMenu weeklyMenu) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, weeklyMenu.get_id());
        values.put(MONDAY, weeklyMenu.getMonday());
        values.put(TUESDAY, weeklyMenu.getTuesday());
        values.put(WEDNESDAY, weeklyMenu.getWednesday());
        values.put(THURSDAY, weeklyMenu.getThursday());
        values.put(FRIDAY, weeklyMenu.getFriday());
        values.put(SATURDAY, weeklyMenu.getSaturday());
        values.put(SUNDAY, weeklyMenu.getSunday());
        values.put(MONDAY_DINNER, weeklyMenu.getMondayDinner());
        values.put(TUESDAY_DINNER, weeklyMenu.getTuesdayDinner());
        values.put(WEDNESDAY_DINNER, weeklyMenu.getWednesdayDinner());
        values.put(THURSDAY_DINNER, weeklyMenu.getThursdayDinner());
        values.put(FRIDAY_DINNER, weeklyMenu.getFridayDinner());
        values.put(SATURDAY_DINNER, weeklyMenu.getSaturdayDinner());
        values.put(SUNDAY_DINNER, weeklyMenu.getSundayDinner());
        values.put(TIME, String.valueOf(weeklyMenu.getTime()));
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void updateOneEntry(String newEntry, int dayNumber, Boolean isDinner) {
        //dayNumber - 0:Monday, 6:Sunday
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String col = "";
        switch (dayNumber) {
            case 0:
                col = MONDAY;
                break;
            case 1:
                col = TUESDAY;
                break;
            case 2:
                col = WEDNESDAY;
                break;
            case 3:
                col = THURSDAY;
                break;
            case 4:
                col = FRIDAY;
                break;
            case 5:
                col = SATURDAY;
                break;
            case 6:
                col = SUNDAY;
                break;
            default:
                return;
        }
        if (isDinner) {
            col += DINNER_TAG;
        }
        values.put(col, newEntry);
        db.update(TABLE_NAME, values, null, null);
        db.close();
    }

    public void DeleteAllRows() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

}


