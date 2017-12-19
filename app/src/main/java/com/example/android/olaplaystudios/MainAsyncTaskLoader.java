package com.example.android.olaplaystudios;

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
    private static final String LOG_TAG = MainAsyncTaskLoader.class.getSimpleName();
    private Object cachedData;
    private String loaderString;

    public MainAsyncTaskLoader(Context context) {
        super(context);

        loaderString = getLoaderString(getId());
        Log.v(LOG_TAG, "-> constructor -> " + loaderString);
    }

    public static String getLoaderString(int id) {

        switch (id) {

            case GET_ALL_SONGS_FROM_INTERNET:
                return "GET_ALL_SONGS_FROM_INTERNET";

            default:
                throw new UnsupportedOperationException("Unknown loader id = " + id);
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        Log.v(LOG_TAG, "-> onStartLoading -> " + loaderString);

        if (cachedData == null)
            forceLoad();
        else
            deliverResult(cachedData);
    }

    @Override
    public Object loadInBackground() {
        Log.v(LOG_TAG, "-> loadInBackground -> " + loaderString);

        if (getId() == GET_ALL_SONGS_FROM_INTERNET)
            return getAllSongs();

        return null;
    }

    private Object getAllSongs() {
        Log.v(LOG_TAG, "-> loadInBackground -> getAllSongs -> " + loaderString);

        HackerearthService hackerearthService = RetrofitSingleton.getHackerearthService();
        Call<ArrayList<SongDetails>> songDetailsCall = hackerearthService.getAllSongs();

        Response<ArrayList<SongDetails>> songDetailsResponse = null;

        try {
            songDetailsResponse = songDetailsCall.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AdapterDataWrapper adapterDataWrapper;
        ArrayList<SongDetails> songDetailsList = new ArrayList<>();

        if (songDetailsResponse == null || !songDetailsResponse.isSuccessful()) {

            String code = songDetailsResponse != null ? String.valueOf(songDetailsResponse.code()) : "null";
            Log.e(LOG_TAG, "-> loadInBackground -> getAllSongs -> " + loaderString + " -> Failure -> " + code);
            adapterDataWrapper = new AdapterDataWrapper(ViewType.FAILURE_VIEW, null);

        } else {

            Log.v(LOG_TAG, "-> loadInBackground -> " + loaderString + " -> Success");
            songDetailsList = songDetailsResponse.body();

            if (songDetailsList == null || songDetailsList.isEmpty())
                adapterDataWrapper = new AdapterDataWrapper(ViewType.EMPTY_VIEW, songDetailsList);
            else
                adapterDataWrapper = new AdapterDataWrapper(ViewType.NORMAL_VIEW, songDetailsList);
        }

        cachedData = adapterDataWrapper;

        if (adapterDataWrapper.dataViewType != ViewType.NORMAL_VIEW)
            return adapterDataWrapper;

        for (SongDetails song : songDetailsList) {

            String selection = SongsEntry.COLUMN_SONG_NAME + " = ? AND " +
                    SongsEntry.COLUMN_SONG_URL + " = ? AND " +
                    SongsEntry.COLUMN_SONG_ARTISTS + " = ? AND " +
                    SongsEntry.COLUMN_SONG_COVER_IMAGE + " = ?";

            String[] selectionArgs = {song.getSong(), song.getUrl(),
                    song.getArtists(), song.getCoverImage()};

            Cursor cursor = getContext().getContentResolver().query(
                    SongsEntry.CONTENT_URI,
                    new String[]{SongsEntry._ID},
                    selection, selectionArgs, null);

            if (cursor == null || cursor.getCount() == 0)
                continue;

            song.setFavorite(true);
            cursor.moveToFirst();
            song.setDatabaseId((long) cursor.getInt(cursor.getColumnIndex(SongsEntry._ID)));
            cursor.close();
        }

        return adapterDataWrapper;
    }
}