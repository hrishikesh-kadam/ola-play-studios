package com.example.android.olaplaystudios;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks, SongsAdapter.OnClickReloadListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private SongsAdapter songsAdapter;
    private AdapterDataWrapper songsAdapterDataWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "-> onCreate");

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        songsAdapter = new SongsAdapter(this, new AdapterDataWrapper(ViewType.LOADING_VIEW, null));
        recyclerView.setAdapter(songsAdapter);

        getSupportLoaderManager().initLoader(
                MainAsyncTaskLoader.GET_ALL_SONGS_FROM_INTERNET, null, this);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle bundle) {

        switch (id) {

            case MainAsyncTaskLoader.GET_ALL_SONGS_FROM_INTERNET:
                Log.v(LOG_TAG, "-> onCreateLoader -> " + MainAsyncTaskLoader.getLoaderString(id));
                return new MainAsyncTaskLoader(this);

            default:
                throw new UnsupportedOperationException("-> Unknown loader id = " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

        switch (loader.getId()) {

            case MainAsyncTaskLoader.GET_ALL_SONGS_FROM_INTERNET:

                songsAdapterDataWrapper = (AdapterDataWrapper) data;
                Log.v(LOG_TAG, "-> onLoadFinished -> " + MainAsyncTaskLoader.getLoaderString(loader.getId()) + " -> " + songsAdapterDataWrapper.getViewTypeString());
                songsAdapter.swapData(songsAdapterDataWrapper);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Log.v(LOG_TAG, "-> onLoaderReset -> " + MainAsyncTaskLoader.getLoaderString(loader.getId()));
    }

    @Override
    public void onClickReload() {
        Log.v(LOG_TAG, "-> onClickReload");

        songsAdapterDataWrapper = new AdapterDataWrapper(ViewType.LOADING_VIEW, null);
        songsAdapter.swapData(songsAdapterDataWrapper);

        getSupportLoaderManager().restartLoader(
                MainAsyncTaskLoader.GET_ALL_SONGS_FROM_INTERNET, null, this);
    }
}
