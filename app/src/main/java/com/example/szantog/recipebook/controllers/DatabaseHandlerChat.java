package com.example.szantog.recipebook.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.szantog.recipebook.models.ChatItem;

import java.util.ArrayList;


public class DatabaseHandlerChat extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Recipes";
    private static final int DATABASE_VERSION = 1;

    private final String TABLE_NAME = "chat";

    private final String TIME = "time";
    private final String FROM = "fromuser";
    private final String MESSAGE = "message";
    private final String SUCCESSFUL_UPLOAD = "uploaded";

    public DatabaseHandlerChat(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + TIME + " TEXT PRIMARY KEY, " + FROM + " TEXT, " + MESSAGE + " TEXT, " + SUCCESSFUL_UPLOAD + " INTEGER)");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + TIME + " TEXT PRIMARY KEY, " + FROM + " TEXT, " + MESSAGE + " TEXT, " + SUCCESSFUL_UPLOAD + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + TIME + " TEXT PRIMARY KEY, " + FROM + " TEXT, " + MESSAGE + " TEXT, " + SUCCESSFUL_UPLOAD + " INTEGER)");
    }

    public void DropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public void InsertData(ChatItem item, Boolean successfulUpload) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TIME, String.valueOf(item.getTime()));
        values.put(FROM, item.getFrom());
        values.put(MESSAGE, item.getMessage());
        if (successfulUpload) {
            values.put(SUCCESSFUL_UPLOAD, 1);
        } else {
            values.put(SUCCESSFUL_UPLOAD, 0);
        }
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<ChatItem> getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + TIME, null);
        int length = cursor.getCount();
        ArrayList<ChatItem> items = new ArrayList<ChatItem>();
        for (int i = 0; i < length; i++) {
            cursor.moveToPosition(i);
            String time = cursor.getString(cursor.getColumnIndex(TIME));
            String from = cursor.getString(cursor.getColumnIndex(FROM));
            String message = cursor.getString(cursor.getColumnIndex(MESSAGE));
            items.add(new ChatItem(Long.parseLong(time), from, message));
        }
        db.close();
        return items;
    }

    public ArrayList<ChatItem> GetFailedUploadData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + SUCCESSFUL_UPLOAD + "=0", null);
        int length = cursor.getCount();
        ArrayList<ChatItem> items = new ArrayList<ChatItem>();
        for (int i = 0; i < length; i++) {
            cursor.moveToPosition(i);
            String time = cursor.getString(cursor.getColumnIndex(TIME));
            String from = cursor.getString(cursor.getColumnIndex(FROM));
            String message = cursor.getString(cursor.getColumnIndex(MESSAGE));
            items.add(new ChatItem(Long.parseLong(time), from, message));
        }
        return items;
    }

    public void ChangeDataToUploaded(ChatItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + TIME + "=" + item.getTime());
        this.InsertData(item, true);
        db.close();
    }

    public void DeleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }
}
