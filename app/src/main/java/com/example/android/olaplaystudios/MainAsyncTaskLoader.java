package com.example.android.olaplaystudios;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.android.olaplaystudios.data.StudiosContract.SongsEntry;
import com.example.android.olaplaystudios.model.SongDetails;
import com.example.android.olaplaystudios.rest.HackerearthService;
import com.example.android.olaplaystudios.rest.RetrofitSingleton;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Hrishikesh Kadam on 16/12/2017
 */

public class MainAsyncTaskLoader extends AsyncTaskLoader {

    public static final int GET_ALL_SONGS_FROM_INTERNET = 0;
    public static final int GET_ALL_SONGS_FROM_DB = 1;
    private static final String LOG_TAG = MainAsyncTaskLoader.class.getSimpleName();
    private Object cachedData;

    public MainAsyncTaskLoader(Context context) {
        super(context);
        Log.v(LOG_TAG, "-> constructor");
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        Log.v(LOG_TAG, "-> onStartLoading");

        if (cachedData == null)
            forceLoad();
        else
            deliverResult(cachedData);
    }

    @Override
    public Object loadInBackground() {
        Log.v(LOG_TAG, "-> loadInBackground");

        if (getId() == GET_ALL_SONGS_FROM_INTERNET)
            return getAllSongs();

        return null;
    }

    private Object getAllSongs() {
        Log.v(LOG_TAG, "-> loadInBackground -> getAllSongs");

        HackerearthService hackerearthService = RetrofitSingleton.getHackerearthService();
        Call<ArrayList<SongDetails>> songDetailsCall = hackerearthService.getAllSongs();

        Response<ArrayList<SongDetails>> songDetailsResponse = null;

        try {
            songDetailsResponse = songDetailsCall.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (songDetailsResponse == null)
            return null;

        ArrayList<SongDetails> songDetailsList = songDetailsResponse.body();

        // Add only the new songs to the database

        if (songDetailsList == null)
            return null;

        for (SongDetails song : songDetailsList) {

            String selection = SongsEntry.COLUMN_SONG_NAME + " = ? AND " +
                    SongsEntry.COLUMN_SONG_URL + " = ? AND " +
                    SongsEntry.COLUMN_SONG_ARTISTS + " = ? AND " +
                    SongsEntry.COLUMN_SONG_COVER_IMAGE + " = ?";

            String[] selectionArgs = {song.getSong(), song.getUrl(),
                    song.getArtists(), song.getCoverImage()};

            Cursor cursor = getContext().getContentResolver().query(
                    SongsEntry.CONTENT_URI, null, selection, selectionArgs, null);

            if (cursor == null)
                continue;

            if (cursor.getCount() == 0) {

                ContentValues values = new ContentValues();
                values.put(SongsEntry.COLUMN_SONG_NAME, song.getSong());
                values.put(SongsEntry.COLUMN_SONG_URL, song.getUrl());
                values.put(SongsEntry.COLUMN_SONG_ARTISTS, song.getArtists());
                values.put(SongsEntry.COLUMN_SONG_COVER_IMAGE, song.getCoverImage());

                getContext().getContentResolver().insert(SongsEntry.CONTENT_URI, values);
            }

            cursor.close();
        }

        cachedData = songDetailsList;
        return songDetailsList;
    }

//    private Object getAllSongs() {
//        Log.v(LOG_TAG, "-> loadInBackground -> getAllSongs");
//
//        HackerearthService hackerearthService = RetrofitSingleton.getHackerearthService();
//        Call<ArrayList<SongDetails>> songDetailsCall = hackerearthService.getAllSongs();
//
//        Response<ArrayList<SongDetails>> songDetailsResponse = null;
//
//        try {
//            songDetailsResponse = songDetailsCall.execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if (songDetailsResponse == null)
//            return null;
//
//        ArrayList<SongDetails> songDetailsList = songDetailsResponse.body();
//
//        // Add only the new songs to the database
//
//        if (songDetailsList == null)
//            return null;
//
//        ArrayList<ContentValues> valuesArrayList = new ArrayList<>();
//
//        for (SongDetails song : songDetailsList) {
//
//            String selection = SongsEntry.COLUMN_SONG_NAME + " = ? AND " +
//                    SongsEntry.COLUMN_SONG_URL + " = ? AND " +
//                    SongsEntry.COLUMN_SONG_ARTISTS + " = ? AND " +
//                    SongsEntry.COLUMN_SONG_COVER_IMAGE + " = ?";
//
//            String[] selectionArgs = {song.getSong(), song.getUrl(),
//                    song.getArtists(), song.getCoverImage()};
//
//            Cursor cursor = getContext().getContentResolver().query(
//                    SongsEntry.CONTENT_URI, null, selection, selectionArgs, null);
//
//            if (cursor == null)
//                continue;
//
//            if (cursor.getCount() == 0) {
//
//                ContentValues values = new ContentValues();
//                values.put(SongsEntry.COLUMN_SONG_NAME, song.getSong());
//                values.put(SongsEntry.COLUMN_SONG_URL, song.getUrl());
//                values.put(SongsEntry.COLUMN_SONG_ARTISTS, song.getArtists());
//                values.put(SongsEntry.COLUMN_SONG_COVER_IMAGE, song.getCoverImage());
//                valuesArrayList.add(values);
//            }
//
//            cursor.close();
//        }
//
//        ContentValues[] valuesArray = valuesArrayList.toArray(new ContentValues[valuesArrayList.size()]);
//        int noOfRowsInserted = getContext().getContentResolver().bulkInsert(SongsEntry.CONTENT_URI, valuesArray);
//
//        Log.d(LOG_TAG, "-> loadInBackground -> getAllSongs -> " + noOfRowsInserted);
//
//        return songDetailsList;
//    }
}
