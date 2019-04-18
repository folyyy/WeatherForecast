package com.example.forecast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MyDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "HISTORY_FORECAST_DATABASE";
    private static final String TABLE_NAME = "Forecast_History";
    private static final String COLUMN_DAY_DATE = "Day_Date";
    private static final String COLUMN_DAY_TEMP = "Day_Temp";
    private static final String COLUMN_DAY_DESCRIPTION = "Day_Description";
    private static final String COLUMN_DAY_MIN_PREFERRED = "Min_Preferred";
    private static final String COLUMN_DAY_MAX_PREFERRED = "Max_Preferred";

    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase db) {
        Log.i("DB", "onCreate CALLED");
        String script = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_DAY_DATE + " String PRIMARY KEY," + COLUMN_DAY_TEMP +
                " Double," + COLUMN_DAY_DESCRIPTION + " String," + COLUMN_DAY_MIN_PREFERRED + " Integer,"
                + COLUMN_DAY_MAX_PREFERRED + " Integer)";
        db.execSQL(script);
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("DB", "onUpgrade CALLED");
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public void addDay(HistoryForecast historyForecast) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY_DATE, historyForecast.dayDate);
        values.put(COLUMN_DAY_TEMP, historyForecast.dayTemp);
        values.put(COLUMN_DAY_DESCRIPTION, historyForecast.dayDescription);
        values.put(COLUMN_DAY_MIN_PREFERRED, historyForecast.minPreferred);
        values.put(COLUMN_DAY_MAX_PREFERRED, historyForecast.maxPreferred);

        // Inserting Row
        db.insert(TABLE_NAME,null, values);

        // Closing database connection
        db.close();
        Log.d("DB", "Added day in database: dayDate = " + historyForecast.dayDate + ", dayTemp = "
                + historyForecast.dayTemp + ", dayDescription = " + historyForecast.dayDescription + ", minPreferred = " +
                historyForecast.minPreferred + ", maxPreferred = " + historyForecast.maxPreferred);
    }

    public boolean hasDay(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[] {
                        COLUMN_DAY_DATE, COLUMN_DAY_TEMP, COLUMN_DAY_DESCRIPTION,
                        COLUMN_DAY_MIN_PREFERRED, COLUMN_DAY_MAX_PREFERRED}, COLUMN_DAY_DATE + "=?",
                new String[] { String.valueOf(date) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            return false;
        } else return true;
    }

    public HistoryForecast getDay(String date) {
        Log.i("DB", "getDay CALLED, date = " + date);
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[] {
                COLUMN_DAY_DATE, COLUMN_DAY_TEMP, COLUMN_DAY_DESCRIPTION, COLUMN_DAY_MIN_PREFERRED,
                        COLUMN_DAY_MAX_PREFERRED }, COLUMN_DAY_DATE + "=?",
                new String[] { String.valueOf(date) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        HistoryForecast historyForecast = new HistoryForecast(cursor.getString(0), cursor.getDouble(1),
                cursor.getString(2), cursor.getInt(3), cursor.getInt(4));

        return historyForecast;

    }

    public ArrayList<HistoryForecast> getAllDays() {
        Log.i("DB", "getAllDays CALLED");
        ArrayList<HistoryForecast> dayList = new ArrayList<HistoryForecast>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                HistoryForecast historyForecast = new HistoryForecast();
                historyForecast.dayDate = cursor.getString(0);
                historyForecast.dayTemp = cursor.getDouble(1);
                historyForecast.dayDescription = cursor.getString(2);
                historyForecast.minPreferred = cursor.getInt(3);
                historyForecast.maxPreferred = cursor.getInt(4);
                // Adding day to list
                dayList.add(historyForecast);
            } while (cursor.moveToNext());
        }
        Log.d("DB", "HISTORY: " + dayList);
        return dayList;
    }

    public int updateDay(HistoryForecast historyForecast) {
        Log.i("DB", "updateDay CALLED");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY_DATE, historyForecast.dayDate);
        values.put(COLUMN_DAY_TEMP, historyForecast.dayTemp);
        values.put(COLUMN_DAY_DESCRIPTION, historyForecast.dayDescription);
        values.put(COLUMN_DAY_MIN_PREFERRED, historyForecast.minPreferred);
        values.put(COLUMN_DAY_MAX_PREFERRED, historyForecast.maxPreferred);

        // Updating row
        Log.d("DB", "Added day in database: dayDate = " + historyForecast.dayDate + ", dayTemp = "
                + historyForecast.dayTemp + ", dayDescription = " + historyForecast.dayDescription + ", minPreferred = " +
                historyForecast.minPreferred + ", maxPreferred = " + historyForecast.maxPreferred);
        return db.update(TABLE_NAME, values, COLUMN_DAY_DATE + " = ?", new String[]
                {String.valueOf(historyForecast.dayDate)});

    }

    public void deleteDay(HistoryForecast historyForecast) {
        Log.i("DB", "deleteDay CALLED");
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_DAY_DATE + " =?", new String[] {String.valueOf(historyForecast.dayDate)});
        db.close();
    }


}