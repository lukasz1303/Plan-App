package com.example.plan.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.plan.data.PlanContract.PlanEntry;
import com.example.plan.data.PlanContract.NotesEntry;

public class PlanDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "plan.db";
    private static final String SQL_CREATE_PLAN_TABLE = "CREATE TABLE " + PlanEntry.TABLE_NAME + " (" +
            PlanEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PlanEntry.COLUMN_START_TIME + " TEXT NOT NULL, " +
            PlanEntry.COLUMN_END_TIME + " TEXT NOT NULL, " +
            PlanEntry.COLUMN_SUBJECT + " TEXT NOT NULL, " +
            PlanEntry.COLUMN_LECTURER + " TEXT, " +
            PlanEntry.COLUMN_TYPE_OF_CLASSES + " INTEGER NOT NULL DEFAULT 0, " +
            PlanEntry.COLUMN_ROOM + " TEXT NOT NULL," +
            PlanEntry.COLUMN_COLOR + " INTEGER NOT NULL DEFAULT 0, " +
            PlanEntry.COLUMN_DAY + " INTEGER NOT NULL)";

    private static final String SQL_CREATE_NOTES_TABLE = "CREATE TABLE " + NotesEntry.TABLE_NAME + " (" +
            NotesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NotesEntry.COLUMN_ID_OF_SUBJECT + " INTEGER NOT NULL, " +
            NotesEntry.COLUMN_TITLE + " TEXT, " +
            NotesEntry.COLUMN_CONTENT + " TEXT)";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + PlanEntry.TABLE_NAME;

    public PlanDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PLAN_TABLE);
        db.execSQL(SQL_CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}