package com.example.plan.data;
import com.example.plan.data.PlanContract.PlanEntry;
import com.example.plan.data.PlanContract.NotesEntry;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class PlanProvider extends ContentProvider {

    private static final String LOG_TAG =PlanProvider.class.getSimpleName();

    private PlanDbHelper mDbHelper;

    private static final int PLAN = 100;
    private static final int PLAN_ID = 101;
    private static final int NOTES = 200;
    private static final int NOTE_ID = 201;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(PlanContract.CONTENT_AUTHORITY, PlanEntry.TABLE_NAME, PLAN);
        sUriMatcher.addURI(PlanContract.CONTENT_AUTHORITY, PlanEntry.TABLE_NAME + "/#", PLAN_ID);
        sUriMatcher.addURI(PlanContract.CONTENT_AUTHORITY, NotesEntry.TABLE_NAME, NOTES);
        sUriMatcher.addURI(PlanContract.CONTENT_AUTHORITY, NotesEntry.TABLE_NAME + "/#", NOTE_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new PlanDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match){
            case PLAN:
                cursor = database.query(PlanEntry.TABLE_NAME, projection, selection, selectionArgs,null,null, sortOrder);
                break;
            case PLAN_ID:
                selection = PlanEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(PlanEntry.TABLE_NAME, projection, selection, selectionArgs , null, null, sortOrder);
                break;
            case NOTES:
                cursor = database.query(NotesEntry.TABLE_NAME, projection, selection, selectionArgs,null,null, sortOrder);
                break;
            case NOTE_ID:
                selection = NotesEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(NotesEntry.TABLE_NAME, projection, selection, selectionArgs , null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PLAN:
                return PlanEntry.CONTENT_LIST_TYPE;
            case PLAN_ID:
                return PlanEntry.CONTENT_ITEM_TYPE;
            case NOTES:
                return NotesEntry.CONTENT_LIST_TYPE;
            case NOTE_ID:
                return NotesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case PLAN:
                return insertSubject(uri,values);
            case NOTES:
                return insertNote(uri,values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);

        }
    }

    private Uri insertSubject(Uri uri, ContentValues values){
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(PlanEntry.TABLE_NAME,null,values);
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertNote(Uri uri, ContentValues values){
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(NotesEntry.TABLE_NAME,null,values);
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match){
            case PLAN:
               return database.delete(PlanEntry.TABLE_NAME,selection,selectionArgs);
            case PLAN_ID:
                selection = PlanEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                int rowsDeleted = database.delete(PlanEntry.TABLE_NAME,selection,selectionArgs);

                if (rowsDeleted!=0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rowsDeleted;
            case NOTES:
                return database.delete(NotesEntry.TABLE_NAME,selection,selectionArgs);
            case NOTE_ID:
                selection = NotesEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                int rowsDeletedNotes = database.delete(NotesEntry.TABLE_NAME,selection,selectionArgs);

                if (rowsDeletedNotes!=0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rowsDeletedNotes;
            default:
                throw new IllegalArgumentException("Deleting is not supported for: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match){
            case PLAN:
                return updateSubject(uri, values, selection,selectionArgs);
            case PLAN_ID:
                selection = PlanEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                return updateSubject(uri, values, selection,selectionArgs);
            case NOTES:
            return updateNote(uri, values, selection,selectionArgs);
            case NOTE_ID:
                selection = NotesEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                return updateNote(uri, values, selection,selectionArgs);

            default:
                throw new IllegalArgumentException("Deleeting is not supported for: " + uri);
        }
    }
    private int updateSubject(Uri uri, ContentValues values, String selection, String[] selectionArgs){

        if (values.size() == 0 )
        {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(PlanEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return rowsUpdated;
    }

    private int updateNote(Uri uri, ContentValues values, String selection, String[] selectionArgs){

        if (values.size() == 0 )
        {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(NotesEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return rowsUpdated;
    }


}
