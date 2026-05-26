package com.example.habittracker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "habits.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_HABITS = "habits";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_IS_DONE = "is_done";
    private static final String COL_STREAK = "streak";
    private static final String COL_LAST_DONE_DATE = "last_done_date";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_HABITS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_IS_DONE + " INTEGER DEFAULT 0, " +
                COL_STREAK + " INTEGER DEFAULT 0, " +
                COL_LAST_DONE_DATE + " TEXT DEFAULT '')";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABITS);
        onCreate(db);
    }

    public void addHabit(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_IS_DONE, 0);
        values.put(COL_STREAK, 0);
        values.put(COL_LAST_DONE_DATE, "");
        db.insert(TABLE_HABITS, null, values);
        db.close();
    }

    public List<Habit> getAllHabits() {
        List<Habit> habits = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_HABITS, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                boolean isDone = cursor.getInt(2) == 1;
                int streak = cursor.getInt(3);
                String lastDoneDate = cursor.getString(4);
                habits.add(new Habit(id, name, isDone, streak, lastDoneDate));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return habits;
    }

    public void deleteHabit(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HABITS, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void updateDoneStatus(int habitId, boolean isDone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_IS_DONE, isDone ? 1 : 0);
        db.update(TABLE_HABITS, values, COL_ID + "=?", new String[]{String.valueOf(habitId)});
        db.close();
    }

    public void updateStreak(int habitId, int streak, String lastDoneDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_STREAK, streak);
        values.put(COL_LAST_DONE_DATE, lastDoneDate);
        db.update(TABLE_HABITS, values, COL_ID + "=?", new String[]{String.valueOf(habitId)});
        db.close();
    }
    public void resetAllDoneStatus() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_IS_DONE, 0);
        db.update(TABLE_HABITS, values, null, null);
        db.close();
    }
}