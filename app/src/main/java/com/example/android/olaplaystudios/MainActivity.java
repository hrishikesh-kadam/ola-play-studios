package com.example.android.olaplaystudios;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.android.olaplaystudios.data.StudiosContract;
import com.example.android.olaplaystudios.model.SongDetails;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ArrayList<SongDetails> songDetailsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "-> onCreate");

        setContentView(R.layout.activity_main);

        getSupportLoaderManager().initLoader(
                MainAsyncTaskLoader.GET_ALL_SONGS_FROM_DB, null, this);

        getSupportLoaderManager().initLoader(
                MainAsyncTaskLoader.GET_ALL_SONGS_FROM_INTERNET, null, this);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle bundle) {
        Log.v(LOG_TAG, "-> onCreateLoader");

        switch (id) {

            case MainAsyncTaskLoader.GET_ALL_SONGS_FROM_INTERNET:
                return new MainAsyncTaskLoader(this);

            case MainAsyncTaskLoader.GET_ALL_SONGS_FROM_DB:
                return new CursorLoader(
                        this, StudiosContract.SongsEntry.CONTENT_URI,
                        null, null, null, null);

            default:
                throw new UnsupportedOperationException("-> Unknown id = " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

        switch (loader.getId()) {

            case MainAsyncTaskLoader.GET_ALL_SONGS_FROM_INTERNET:

                Log.v(LOG_TAG, "-> onLoadFinished -> GET_ALL_SONGS_FROM_INTERNET");
                songDetailsList = (ArrayList<SongDetails>) data;
                Log.d(LOG_TAG, "-> onLoadFinished -> GET_ALL_SONGS_FROM_INTERNET -> " + songDetailsList);
                break;

            case MainAsyncTaskLoader.GET_ALL_SONGS_FROM_DB:

                Log.v(LOG_TAG, "-> onLoadFinished -> GET_ALL_SONGS_FROM_DB");
                Cursor cursor = (Cursor) data;
                Log.d(LOG_TAG, "-> onLoadFinished -> GET_ALL_SONGS_FROM_DB -> " + cursor.getCount());
                cursor.close();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Log.v(LOG_TAG, "-> onLoaderReset");
    }
}
