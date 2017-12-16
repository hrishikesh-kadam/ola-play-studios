package com.example.android.olaplaystudios;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
                MainAsyncTaskLoader.GET_ALL_SONGS_LOADER, null, this);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle bundle) {
        Log.v(LOG_TAG, "-> onCreateLoader");

        if (id == MainAsyncTaskLoader.GET_ALL_SONGS_LOADER)
            return new MainAsyncTaskLoader(this);

        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

        if (loader.getId() == MainAsyncTaskLoader.GET_ALL_SONGS_LOADER) {
            Log.v(LOG_TAG, "-> onLoadFinished -> GET_ALL_SONGS_LOADER");

            songDetailsList = (ArrayList<SongDetails>) data;

            Log.d(LOG_TAG, "-> onLoadFinished -> GET_ALL_SONGS_LOADER -> " + songDetailsList);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Log.v(LOG_TAG, "-> onLoaderReset");
    }
}