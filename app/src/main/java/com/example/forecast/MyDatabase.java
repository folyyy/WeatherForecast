package com.example.forecast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MyDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "HISTORY_FORECAST_DATABASE";
    private static final String TABLE_NAME = "Forecast_History";
    private static final String TABLE_PREFERRED = "PreferredW_History";
    private static final String COLUMN_DAY_DATE = "Day_Date";
    private static final String COLUMN_DAY_TEMP = "Day_Temp";
    private static final String COLUMN_DAY_DESCRIPTION = "Day_Description";
    private static final String COLUMN_DAY_MIN_PREFERRED = "Min_Preferred";
    private static final String COLUMN_DAY_MAX_PREFERRED = "Max_Preferred";
    private static final String COLUMN_DAY_PREFERRED_ID = "Preferred_Id";

    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase db) {
        Log.i("DB", "onCreate CALLED");
        String script = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_DAY_DATE + " String PRIMARY KEY," + COLUMN_DAY_TEMP +
                " Double," + COLUMN_DAY_DESCRIPTION + " String)";
        db.execSQL(script);
        String script2 = "CREATE TABLE " + TABLE_PREFERRED + "(" + COLUMN_DAY_PREFERRED_ID +
                " Integer PRIMARY KEY AUTOINCREMENT NOT NULL," + COLUMN_DAY_MIN_PREFERRED + " Integer," +
                COLUMN_DAY_MAX_PREFERRED + " Integer)";
        db.execSQL(script2);
    }

    // If version is changed, deleting the table
    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("DB", "onUpgrade CALLED");
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREFERRED);

        // Create tables again
        onCreate(db);
    }

    // Adding preferred weather
    public void addPreferredW(HistoryPreferredW historyPreferredW) {
        Log.i("DB", "addPreferredW CALLED");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY_MIN_PREFERRED, historyPreferredW.minPreferred);
        values.put(COLUMN_DAY_MAX_PREFERRED, historyPreferredW.maxPreferred);

        db.insert(TABLE_PREFERRED, null, values);
        db.close();
        Log.d("DB", "Added Preferred Weather in database: minPreferred = " + historyPreferredW.minPreferred +
                ", maxPreferred = " + historyPreferredW.maxPreferred);
    }

    // Updating preferred weather
    public int updatePreferredW(HistoryPreferredW historyPreferredW) {
        Log.i("DB", "updatePreferredW CALLED");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY_MIN_PREFERRED, historyPreferredW.minPreferred);
        values.put(COLUMN_DAY_MAX_PREFERRED, historyPreferredW.maxPreferred);
        // Updating row
        Log.d("DB", "Updated Preferred Weather in database: minPreferred = " + historyPreferredW.minPreferred +
                ", maxPreferred = " + historyPreferredW.maxPreferred);
        return db.update(TABLE_PREFERRED, values, COLUMN_DAY_PREFERRED_ID + " = ?", new String[]
                {String.valueOf(1)});
    }

    // Getting preferred weather values
    public HistoryPreferredW getPreferredW() {
        Log.i("DB", "getPreferredW CALLED");
        String selectQuery= "SELECT * FROM " + TABLE_PREFERRED;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null)
            cursor.moveToFirst();

        HistoryPreferredW historyPreferredW = new HistoryPreferredW(cursor.getInt(1), cursor.getInt(2));
        cursor.close();
        return historyPreferredW;
    }

    // Checking if preferred weather is empty
    public boolean preferredWisEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        String count = "SELECT * FROM " + TABLE_PREFERRED;
        Cursor cursor = db.rawQuery(count, null);
        cursor.moveToFirst();
        int num = cursor.getCount();
        if (num < 1) {
            Log.d("DB", "Table PreferredW is empty " + num);
            return true;
        }
        else {
            Log.d("DB", "Table is NOT empty " + num);
            return false;
        }
    }

    // Adding a day to database
    public void addDay(HistoryForecast historyForecast) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY_DATE, historyForecast.dayDate);
        values.put(COLUMN_DAY_TEMP, historyForecast.dayTemp);
        values.put(COLUMN_DAY_DESCRIPTION, historyForecast.dayDescription);

        // Inserting Row
        db.insert(TABLE_NAME,null, values);

        // Closing database connection
        db.close();
        Log.d("DB", "Added day in database: dayDate = " + historyForecast.dayDate + ", dayTemp = "
                + historyForecast.dayTemp + ", dayDescription = " + historyForecast.dayDescription);
    }

    // Checking if a database has a day
    public boolean hasDay(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[] {
                        COLUMN_DAY_DATE, COLUMN_DAY_TEMP, COLUMN_DAY_DESCRIPTION,}, COLUMN_DAY_DATE + "=?",
                new String[] { String.valueOf(date) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            return false;
        } else return true;
    }

    // Checking if table is empty
    public boolean isEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(count, null);
        cursor.moveToFirst();
        int num = cursor.getCount();
        if (num < 1) {
            Log.d("DB", "Table is empty");
            return true;
        }
        else {
            Log.d("DB", "Table is NOT empty");
            return false;
        }
    }

    // Getting data from day
    public HistoryForecast getDay(String date) {
        Log.i("DB", "getDay CALLED, date = " + date);
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[] {
                COLUMN_DAY_DATE, COLUMN_DAY_TEMP, COLUMN_DAY_DESCRIPTION }, COLUMN_DAY_DATE + "=?",
                new String[] { String.valueOf(date) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        HistoryForecast historyForecast = new HistoryForecast(cursor.getString(0), cursor.getDouble(1),
                cursor.getString(2));

        return historyForecast;
    }

    // Getting data from latest day
    public HistoryForecast getLatestDay() {
        Log.i("DB", "getLatestDay CALLED");
        String selectQuery= "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_DAY_DATE + " DESC LIMIT 10";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null)
            cursor.moveToLast();

            HistoryForecast historyForecast = new HistoryForecast(cursor.getString(0), cursor.getDouble(1),
                    cursor.getString(2));
        cursor.close();
        return historyForecast;
    }

    // Getting 9 days data from table
    public ArrayList<HistoryForecast> get9Days() {
        Log.i("DB", "get9Days CALLED");
        ArrayList<HistoryForecast> nineDayList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_DAY_DATE + " DESC LIMIT 9";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all 9 rows and adding to list
        if (cursor.moveToLast()) {
            do {
                HistoryForecast historyForecast = new HistoryForecast();
                historyForecast.dayDate = cursor.getString(0);
                historyForecast.dayTemp = cursor.getDouble(1);
                historyForecast.dayDescription = cursor.getString(2);
                nineDayList.add(historyForecast);
            } while (cursor.moveToPrevious());
        }
        Log.d("DB", "9 DAY HISTORY: " + nineDayList);
        return nineDayList;
    }

    // Getting all columns from table
    public ArrayList<HistoryForecast> getAllDays() {
        Log.i("DB", "getAllDays CALLED");
        ArrayList<HistoryForecast> dayList = new ArrayList<>();
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
                // Adding day to list
                dayList.add(historyForecast);
            } while (cursor.moveToNext());
        }
        Log.d("DB", "HISTORY: " + dayList);
        return dayList;
    }

    // Updating day with new data
    public int updateDay(HistoryForecast historyForecast) {
        Log.i("DB", "updateDay CALLED");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY_DATE, historyForecast.dayDate);
        values.put(COLUMN_DAY_TEMP, historyForecast.dayTemp);
        values.put(COLUMN_DAY_DESCRIPTION, historyForecast.dayDescription);

        // Updating row
        Log.d("DB", "Updated day in database: dayDate = " + historyForecast.dayDate + ", dayTemp = "
                + historyForecast.dayTemp + ", dayDescription = " + historyForecast.dayDescription);
        return db.update(TABLE_NAME, values, COLUMN_DAY_DATE + " = ?", new String[]
                {String.valueOf(historyForecast.dayDate)});
    }

    // Deleting day from database
    public void deleteDay(HistoryForecast historyForecast) {
        Log.i("DB", "deleteDay CALLED");
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_DAY_DATE + " =?", new String[] {String.valueOf(historyForecast.dayDate)});
        db.close();
    }
}