package com.example.android.olaplaystudios.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.olaplaystudios.data.StudiosContract.SongsEntry;

/**
 * Created by Hrishikesh Kadam on 16/12/2017
 */

public class StudiosDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "studiosDb.db";
    private static final int VERSION = 1;

    public StudiosDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_TABLE = "CREATE TABLE " + SongsEntry.TABLE_NAME + " (" +
                SongsEntry._ID + " INTEGER PRIMARY KEY, " +
                SongsEntry.COLUMN_SONG_NAME + " TEXT NOT NULL, " +
                SongsEntry.COLUMN_SONG_URL + " TEXT, " +
                SongsEntry.COLUMN_SONG_ARTISTS + " TEXT, " +
                SongsEntry.COLUMN_SONG_COVER_IMAGE + " TEXT, " +
                SongsEntry.COLUMN_SONG_FAVORITE + " INTEGER);";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new UnsupportedOperationException("onUpgrade not yet implemented");
    }
}
