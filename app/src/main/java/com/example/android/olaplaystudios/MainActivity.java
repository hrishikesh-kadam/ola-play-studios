package com.example.android.olaplaystudios;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks, SongsAdapter.OnClickReloadListener,
        Player.EventListener, SongsAdapter.OnClickButtonPlayPauseListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static MediaSessionCompat mediaSession;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.exoPlayerView)
    SimpleExoPlayerView exoPlayerView;
    private SongsAdapter songsAdapter;
    private AdapterDataWrapper songsAdapterDataWrapper;
    private SimpleExoPlayer exoPlayer;
    private PlaybackStateCompat.Builder stateBuilder;
    private DefaultDataSourceFactory dataSourceFactory;
    private ExtractorsFactory extractorsFactory;
    private int nowPlaying = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "-> onCreate");

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initializeMediaSession();

        getSupportLoaderManager().initLoader(
                MainAsyncTaskLoader.GET_EXOPLAYER, null, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        songsAdapter = new SongsAdapter(this, new AdapterDataWrapper(ViewType.LOADING_VIEW, null));
        recyclerView.setAdapter(songsAdapter);

        getSupportLoaderManager().initLoader(
                MainAsyncTaskLoader.GET_ALL_SONGS_FROM_INTERNET, null, this);
    }

    private void initializeMediaSession() {
        Log.v(LOG_TAG, "-> initializeMediaSession");

        // Create a MediaSessionCompat.
        mediaSession = new MediaSessionCompat(this, LOG_TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mediaSession.setPlaybackState(stateBuilder.build());

        // MySessionCallback has methods that handle callbacks from a media controller.
        mediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mediaSession.setActive(true);
    }

    private void initializePlayer() {
        Log.v(LOG_TAG, "-> initializePlayer");

        exoPlayerView.setPlayer(exoPlayer);
        exoPlayerView.setControllerShowTimeoutMs(-1);
        exoPlayerView.showController();

        // Set the ExoPlayer.EventListener to this activity.
        exoPlayer.addListener(this);

        String userAgent = Util.getUserAgent(this, getString(R.string.app_name));

        // Default parameters, except allowCrossProtocolRedirects is true
        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(
                userAgent,
                null,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true);

        dataSourceFactory = new DefaultDataSourceFactory(
                this,
                null,
                httpDataSourceFactory);

        extractorsFactory = new DefaultExtractorsFactory();
    }

    public void playSong(Uri uri) {
        Log.v(LOG_TAG, "-> playSong");

        MediaSource audioSource = new ExtractorMediaSource(
                uri, dataSourceFactory, extractorsFactory, null, null);

        exoPlayer.prepare(audioSource);
        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle bundle) {
        Log.v(LOG_TAG, "-> onCreateLoader -> " + MainAsyncTaskLoader.getLoaderString(id));

        switch (id) {

            case MainAsyncTaskLoader.GET_ALL_SONGS_FROM_INTERNET:
            case MainAsyncTaskLoader.GET_EXOPLAYER:
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

            case MainAsyncTaskLoader.GET_EXOPLAYER:

                Log.v(LOG_TAG, "-> onLoadFinished -> " + MainAsyncTaskLoader.getLoaderString(loader.getId()));
                exoPlayer = (SimpleExoPlayer) data;
                initializePlayer();
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

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        String action = null;

        if ((playbackState == Player.STATE_READY) && playWhenReady) {
            Log.v(LOG_TAG, "-> onPlayerStateChanged -> PLAY");

            stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    exoPlayer.getCurrentPosition(), 1f);

            action = SongsAdapter.PLAY;
            songsAdapter.setAction(action, nowPlaying);

        } else if ((playbackState == Player.STATE_READY)) {
            Log.v(LOG_TAG, "-> onPlayerStateChanged -> PAUSE");

            stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    exoPlayer.getCurrentPosition(), 1f);

            action = SongsAdapter.PAUSE;
            songsAdapter.setAction(action, nowPlaying);
        }

        mediaSession.setPlaybackState(stateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }

    @Override
    public void onPositionDiscontinuity(int reason) {
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }

    @Override
    public void onSeekProcessed() {
    }

    private void releasePlayer() {

        exoPlayer.stop();
        exoPlayer.release();
        exoPlayer = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        exoPlayer.removeListener(this);
        mediaSession.setActive(false);
    }

    @Override
    public void onClickButtonPlayPause(String action, int position, Uri uri) {
        Log.v(LOG_TAG, "-> onClickButtonPlayPause -> " + action + " -> " + position);

        if (position == nowPlaying) {
            exoPlayer.setPlayWhenReady(action.equals(SongsAdapter.PLAY));
            return;
        }

        nowPlaying = position;
        playSong(uri);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(LOG_TAG, "-> onSaveInstanceState");

        outState.putInt("nowPlaying", nowPlaying);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.v(LOG_TAG, "-> onRestoreInstanceState");

        nowPlaying = savedInstanceState.getInt("nowPlaying");

        onPlayerStateChanged(exoPlayer.getPlayWhenReady(), exoPlayer.getPlaybackState());
    }

    /**
     * Broadcast Receiver registered to receive the MEDIA_BUTTON intent coming from clients.
     */
    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mediaSession, intent);
        }
    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            Log.v(LOG_TAG, "-> MySessionCallback -> onPlay");
            exoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            Log.v(LOG_TAG, "-> MySessionCallback -> onPause");
            exoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            Log.v(LOG_TAG, "-> MySessionCallback -> onSkipToPrevious");
            exoPlayer.seekTo(0);
        }
    }
}
