package com.example.szantog.recipebook.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.szantog.recipebook.models.RecipeItem;

import java.util.ArrayList;

public class DatabaseHandlerRecipes extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Recipes";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "recipebook";

    private static final String ID = "_id";
    private static final String DESCRIPTION = "description";
    private static final String TYPE = "type";
    private static final String SEASON = "season";
    private static final String NAME = "name";
    private static final String INGREDIENTS = "ingredients";
    private static final String CONTAINSDAIRY = "containsDairy";
    private static final String TO_BE_UPLOADED = "tobeuploaded";

    public static final String DIVIDER = "___--___DIVIDER___--___";

    public static final String RECIPETABLE_DEFINITION= "CREATE TABLE " + TABLE_NAME + "(" + ID + " TEXT PRIMARY KEY, " + DESCRIPTION + " TEXT, "
            + TYPE + " TEXT, " + SEASON + " TEXT, " + NAME + " TEXT, " + INGREDIENTS + " TEXT, "
            + CONTAINSDAIRY + " TEXT, " + TO_BE_UPLOADED + " INTEGER)";

    public DatabaseHandlerRecipes(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(RECIPETABLE_DEFINITION);
        sqLiteDatabase.execSQL(DatabaseHandlerWeeklyMenu.WEEKLYMENUTABLE_DEFINITION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //
    }

    public void insertDownloadedData(RecipeItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String ingredientList = "";
        for (String entry : item.getIngredients()) {
            if (ingredientList.length() < 1) {
                ingredientList += entry;
            } else {
                ingredientList += DIVIDER + entry;
            }
        }
        values.put(ID, item.getId());
        values.put(DESCRIPTION, item.getDescription());
        values.put(TYPE, item.getType());
        values.put(SEASON, item.getSeason());
        values.put(NAME, item.getName());
        values.put(INGREDIENTS, ingredientList);
        values.put(CONTAINSDAIRY, String.valueOf(item.isContainsDiary()));
        values.put(TO_BE_UPLOADED, 0);
        try {
            db.insert(TABLE_NAME, null, values);
        } catch (SQLException e) {
            Log.e("SQLException", "Recipe named " + item.getName() + "already present!");
        }
        db.close();
    }

    public void insertLocalNotUploadedData(RecipeItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String ingredientList = "";
        for (String entry : item.getIngredients()) {
            if (ingredientList.length() < 1) {
                ingredientList += entry;
            } else {
                ingredientList += DIVIDER + entry;
            }
        }
        values.put(ID, item.getId());
        values.put(DESCRIPTION, item.getDescription());
        values.put(TYPE, item.getType());
        values.put(SEASON, item.getSeason());
        values.put(NAME, item.getName());
        values.put(INGREDIENTS, ingredientList);
        values.put(CONTAINSDAIRY, String.valueOf(item.isContainsDiary()));
        values.put(TO_BE_UPLOADED, 1);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void deleteItem(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, ID + "=?", new String[]{id});
        db.close();
    }

    public ArrayList<RecipeItem> getRecipesToUpload() {
        ArrayList<RecipeItem> recipesToUpload = new ArrayList<RecipeItem>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + TO_BE_UPLOADED + " = 1", null);

        int rowCount = cursor.getCount();

        for (int i = 0; i < rowCount; i++) {
            cursor.moveToPosition(i);
            String _id = cursor.getString(cursor.getColumnIndex(ID));
            String description = cursor.getString(cursor.getColumnIndex(DESCRIPTION));
            String type = cursor.getString(cursor.getColumnIndex(TYPE));
            String season = cursor.getString(cursor.getColumnIndex(SEASON));
            String name = cursor.getString(cursor.getColumnIndex(NAME));
            String ingredientList = cursor.getString(cursor.getColumnIndex(INGREDIENTS));
            Boolean containsDiary = Boolean.getBoolean(cursor.getString(cursor.getColumnIndex(CONTAINSDAIRY)));

            String[] ingredients = ingredientList.split(DIVIDER);
            recipesToUpload.add((new RecipeItem(_id, name, season, type, containsDiary, ingredients, description)));
        }
        db.close();
        cursor.close();
        return recipesToUpload;
    }

    public boolean hasRecipesToBeUploaded() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = String.format("SELECT * FROM %s WHERE %s = 1 ", TABLE_NAME, TO_BE_UPLOADED);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            db.close();
            cursor.close();
            return true;
        } else {
            db.close();
            cursor.close();
            return false;
        }
    }

    public ArrayList<RecipeItem> getAllData() {
        ArrayList<RecipeItem> allItem = new ArrayList<RecipeItem>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER By " + NAME, null);

        int rowCount = cursor.getCount();

        for (int i = 0; i < rowCount; i++) {
            cursor.moveToPosition(i);
            String _id = cursor.getString(cursor.getColumnIndex(ID));
            String description = cursor.getString(cursor.getColumnIndex(DESCRIPTION));
            String type = cursor.getString(cursor.getColumnIndex(TYPE));
            String season = cursor.getString(cursor.getColumnIndex(SEASON));
            String name = cursor.getString(cursor.getColumnIndex(NAME));
            String ingredientList = cursor.getString(cursor.getColumnIndex(INGREDIENTS));
            Boolean containsDiary = Boolean.getBoolean(cursor.getString(cursor.getColumnIndex(CONTAINSDAIRY)));

            String[] ingredients = ingredientList.split(DIVIDER);
            allItem.add((new RecipeItem(_id, name, season, type, containsDiary, ingredients, description)));
        }
        db.close();
        return allItem;
    }
}
