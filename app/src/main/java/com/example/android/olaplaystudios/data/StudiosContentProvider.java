package com.example.android.olaplaystudios.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.olaplaystudios.data.StudiosContract.SongsEntry;

/**
 * Created by Hrishikesh Kadam on 16/12/2017
 */

public class StudiosContentProvider extends ContentProvider {

    private static final int SONGS_ALL = 0;
    private static final int SONGS_WITH_ID = 1;
    private static final UriMatcher uriMatcher = buildUriMatcher();
    private StudiosDbHelper studiosDbHelper;

    private static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(StudiosContract.AUTHORITY, StudiosContract.PATH_SONGS, SONGS_ALL);
        uriMatcher.addURI(StudiosContract.AUTHORITY, StudiosContract.PATH_SONGS + "/#", SONGS_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        studiosDbHelper = new StudiosDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor cursor;
        final SQLiteDatabase db = studiosDbHelper.getReadableDatabase();

        switch (uriMatcher.match(uri)) {

            case SONGS_ALL:
                cursor = db.query(SongsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case SONGS_WITH_ID:
                cursor = db.query(SongsEntry.TABLE_NAME,
                        projection,
                        SongsEntry._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null,
                        null,
                        null);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri + " in query method");
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("getType not yet implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        Uri returnUri;
        final SQLiteDatabase db = studiosDbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {

            case SONGS_ALL:

                long id = db.insert(SongsEntry.TABLE_NAME, null, values);

                if (id > 0)
                    returnUri = ContentUris.withAppendedId(SongsEntry.CONTENT_URI, id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri + " in insert method");
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        int noOfRowsInserted = 0;
        final SQLiteDatabase db = studiosDbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {

            case SONGS_ALL:

                db.beginTransaction();

                for (ContentValues value : values) {

                    long id = db.insert(SongsEntry.TABLE_NAME, null, value);
                    if (id > 0)
                        noOfRowsInserted++;
                    else {
                        db.endTransaction();
                        throw new android.database.SQLException("bulkInsert transaction failed");
                    }
                }

                db.setTransactionSuccessful();

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri + " in bulkInsert method");
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return noOfRowsInserted;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        int noOfRowsDeleted;
        final SQLiteDatabase db = studiosDbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {

            case SONGS_WITH_ID:

                noOfRowsDeleted = db.delete(
                        SongsEntry.TABLE_NAME,
                        SongsEntry._ID + " = ?",
                        new String[]{uri.getLastPathSegment()});
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri + " in delete method");
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return noOfRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        int noOfRowsUpdated;
        final SQLiteDatabase db = studiosDbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {

            case SONGS_WITH_ID:
                noOfRowsUpdated = db.update(SongsEntry.TABLE_NAME,
                        values,
                        SongsEntry._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri + " in update method");
        }

        return noOfRowsUpdated;
    }
}
