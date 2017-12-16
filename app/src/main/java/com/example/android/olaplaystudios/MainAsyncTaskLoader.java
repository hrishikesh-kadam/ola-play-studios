package com.example.android.olaplaystudios;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

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

    public static final int GET_ALL_SONGS_LOADER = 0;
    private static final String LOG_TAG = MainAsyncTaskLoader.class.getSimpleName();

    public MainAsyncTaskLoader(Context context) {
        super(context);
        Log.v(LOG_TAG, "-> constructor");
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        Log.v(LOG_TAG, "-> onStartLoading");

        forceLoad();
    }

    @Override
    public Object loadInBackground() {
        Log.v(LOG_TAG, "-> loadInBackground");

        if (getId() == GET_ALL_SONGS_LOADER)
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
        else
            return songDetailsResponse.body();
    }
}
