package com.example.android.olaplaystudios;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.olaplaystudios.data.StudiosContract;
import com.example.android.olaplaystudios.data.StudiosContract.SongsEntry;
import com.example.android.olaplaystudios.model.NowPlaying;
import com.example.android.olaplaystudios.model.SongDetails;
import com.example.android.olaplaystudios.util.CustomPicasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Hrishikesh Kadam on 16/12/2017
 */

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {

    public static final String PLAY = "PLAY";
    public static final String PAUSE = "PAUSE";
    private static final String LOG_TAG = SongsAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<SongDetails> songDetailsList;
    private int dataViewType;
    private OnClickReloadListener onClickReloadListener;
    private OnClickButtonPlayPauseListener onClickButtonPlayPauseListener;

    public SongsAdapter(Context context, AdapterDataWrapper adapterDataWrapper) {

        this.context = context;
        //noinspection unchecked
        songDetailsList = (ArrayList<SongDetails>) adapterDataWrapper.data;
        dataViewType = adapterDataWrapper.dataViewType;
        onClickReloadListener = (OnClickReloadListener) context;
        onClickButtonPlayPauseListener = (OnClickButtonPlayPauseListener) context;
    }

    public void swapData(AdapterDataWrapper adapterDataWrapper) {
        Log.v(LOG_TAG, "-> swapData");

        //noinspection unchecked
        songDetailsList = (ArrayList<SongDetails>) adapterDataWrapper.data;
        dataViewType = adapterDataWrapper.dataViewType;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewHolder viewHolder;
        View itemView;

        switch (dataViewType) {

            case ViewType.NORMAL_VIEW:
                itemView = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.song_details_item_layout, parent, false);
                viewHolder = new NormalViewHolder(itemView);
                break;

            case ViewType.LOADING_VIEW:
                itemView = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.loading_view, parent, false);
                viewHolder = new ViewHolder(itemView);
                break;

            case ViewType.FAILURE_VIEW:
                itemView = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.failure_view, parent, false);
                viewHolder = new FailureViewHolder(itemView);
                break;

            case ViewType.EMPTY_VIEW:
                itemView = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.empty_view, parent, false);
                viewHolder = new ViewHolder(itemView);
                break;

            default:
                throw new UnsupportedOperationException("Unknown viewType = " + viewType);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        switch (dataViewType) {

            case ViewType.NORMAL_VIEW:

                NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
                SongDetails song = songDetailsList.get(position);

                CustomPicasso.with(context)
                        .load(song.getCoverImage())
                        .placeholder(R.drawable.imageview_loading_placeholder)
                        .error(R.drawable.imageview_error_placeholder)
                        .into(normalViewHolder.imageViewCover);

                normalViewHolder.textViewSongName.setText(song.getSong());
                normalViewHolder.textViewArtists.setText(song.getArtists());

                float rating = song.getFavorite() ? 1.0f : 0.0f;
                normalViewHolder.ratingBar.setRating(rating);

                normalViewHolder.buttonPlay.setVisibility(View.VISIBLE);
                normalViewHolder.buttonPause.setVisibility(View.INVISIBLE);

                if (NowPlaying.getSongUrl() != null
                        && NowPlaying.getSongUrl().equals(song.getUrl())
                        && NowPlaying.getAction().equals(PLAY)) {

                    normalViewHolder.buttonPlay.setVisibility(View.INVISIBLE);
                    normalViewHolder.buttonPause.setVisibility(View.VISIBLE);
                }

                break;
        }
    }

    public void playFirstSong() {
        Log.v(LOG_TAG, "-> playFirstSong");

        if (dataViewType != ViewType.NORMAL_VIEW)
            return;

        NowPlaying.setNowPlaying(songDetailsList.get(0).getUrl(), PLAY);
        notifyDataSetChanged();
        onClickButtonPlayPauseListener.onClickButtonPlayPause();
    }

    @Override
    public int getItemCount() {

        if (songDetailsList == null || songDetailsList.isEmpty())
            return 1;
        else
            return songDetailsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return dataViewType;
    }

    public interface OnClickReloadListener {
        public void onClickReload();
    }

    public interface OnClickButtonPlayPauseListener {
        public void onClickButtonPlayPause();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class NormalViewHolder extends ViewHolder implements View.OnTouchListener {

        @BindView(R.id.textViewSongName)
        TextView textViewSongName;

        @BindView(R.id.textViewArtists)
        TextView textViewArtists;

        @BindView(R.id.imageViewCover)
        ImageView imageViewCover;

        @BindView(R.id.ratingBar)
        RatingBar ratingBar;

        @BindView(R.id.buttonPlay)
        Button buttonPlay;

        @BindView(R.id.buttonPause)
        Button buttonPause;

        private int ratingAtActionDown;

        public NormalViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            initRatingBar();
        }

        public void initRatingBar() {

            LayerDrawable layerDrawable = (LayerDrawable) ratingBar.getProgressDrawable();

            DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable.getDrawable(0)),
                    ContextCompat.getColor(context, R.color.ratingBarBackground));   // Empty star

            DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable.getDrawable(1)),
                    ContextCompat.getColor(context, R.color.ratingBarProgress)); // Partial star

            DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable.getDrawable(2)),
                    ContextCompat.getColor(context, R.color.ratingBarProgress));

            ratingBar.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {

            switch (v.getId()) {

                case R.id.ratingBar:

                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                        ratingAtActionDown = (int) ratingBar.getRating();

                    else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        ratingBar.setRating(ratingAtActionDown == 0 ? 1 : 0);
                        onRatingChanged(ratingBar.getRating(), true);
                        v.performClick();
                    }

                    return true;
            }

            return false;
        }

        public void onRatingChanged(float rating, boolean fromUser) {
            Log.v(LOG_TAG, "-> onRatingChanged -> rating = " + rating + ", fromUser = " + fromUser);

            if (rating == 1.0f) {

                SongDetails song = songDetailsList.get(getAdapterPosition());
                ContentValues values = new ContentValues();
                values.put(SongsEntry.COLUMN_SONG_NAME, song.getSong());
                values.put(SongsEntry.COLUMN_SONG_URL, song.getUrl());
                values.put(SongsEntry.COLUMN_SONG_ARTISTS, song.getArtists());
                values.put(SongsEntry.COLUMN_SONG_COVER_IMAGE, song.getCoverImage());

                Uri uri = context.getContentResolver().insert(SongsEntry.CONTENT_URI, values);

                if (uri == null)
                    Log.e(LOG_TAG, "-> onRatingChanged -> row insertion failed");
                else {
                    Log.v(LOG_TAG, "-> onRatingChanged -> uri : " + uri + " inserted");
                    song.setDatabaseId(Long.valueOf(uri.getLastPathSegment()));
                    song.setFavorite(true);
                }

            } else if (rating == 0.0f) {

                SongDetails song = songDetailsList.get(getAdapterPosition());
                Long databaseId = song.getDatabaseId();
                Uri uri = StudiosContract.SongsEntry.CONTENT_URI.buildUpon()
                        .appendPath(String.valueOf(databaseId)).build();

                int noOfRowsDeleted = context.getContentResolver().delete(uri, null, null);

                if (noOfRowsDeleted > 0) {
                    Log.v(LOG_TAG, "-> onRatingChanged -> uri : " + uri + " deleted");
                    song.setDatabaseId(null);
                    song.setFavorite(false);
                } else
                    Log.e(LOG_TAG, "-> onRatingChanged -> no rows deleted");

            } else
                Log.w(LOG_TAG, "-> onRatingChanged -> Unknown rating = " + rating);
        }

        @OnClick({R.id.buttonPlay, R.id.buttonPause})
        public void onClickButtonPlayPause(Button button) {
            Log.v(LOG_TAG, "-> onClickButtonPlayPause");

            String action = null;
            String songUrl;

            if (button.getId() == R.id.buttonPlay)
                action = PLAY;
            else if (button.getId() == R.id.buttonPause)
                action = PAUSE;

            SongDetails song = songDetailsList.get(getAdapterPosition());
            songUrl = song.getUrl();

            /*ContentValues values = new ContentValues();
            values.put(NowPlayingEntry.COLUMN_SONG_URL, songUrl);
            values.put(NowPlayingEntry.COLUMN_ACTION, action);

            int noOfRowsDeleted = context.getContentResolver().delete(
                    NowPlayingEntry.CONTENT_URI, null, null);
            Log.v(LOG_TAG, "-> onClickButtonPlayPause -> noOfRowsDeleted = " + noOfRowsDeleted);

            Uri uri = context.getContentResolver().insert(NowPlayingEntry.CONTENT_URI, values);
            Log.v(LOG_TAG, "-> onClickButtonPlayPause -> uri = " + uri);*/

            NowPlaying.setNowPlaying(songUrl, action);
            notifyDataSetChanged();
            onClickButtonPlayPauseListener.onClickButtonPlayPause();
        }
    }

    public class FailureViewHolder extends ViewHolder {

        @BindView(R.id.imageViewReload)
        ImageView imageViewReload;

        public FailureViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.imageViewReload)
        public void onClickReload() {
            Log.v(LOG_TAG, "-> onClickReload");
            onClickReloadListener.onClickReload();
        }
    }
}
