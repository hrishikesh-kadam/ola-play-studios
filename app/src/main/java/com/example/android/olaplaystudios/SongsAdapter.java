package com.example.android.olaplaystudios;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.olaplaystudios.data.StudiosContract.SongsEntry;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Hrishikesh Kadam on 16/12/2017
 */

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {

    public static final int LOADING_VIEW = 0;
    public static final int NORMAL_VIEW = 1;
    public static final int EMPTY_VIEW = 2;
    public static final int FAILURE_VIEW = 3;
    private static final String LOG_TAG = SongsAdapter.class.getSimpleName();
    private Context context;
    private Cursor cursor;
    private int currentViewType;

    public SongsAdapter(Context context, Cursor cursor, int currentViewType) {
        this.context = context;
        this.cursor = cursor;
        this.currentViewType = currentViewType;
    }

    public void swapData(Cursor cursor, int currentViewType) {
        Log.v(LOG_TAG, "-> swapData");

        this.cursor = cursor;
        this.currentViewType = currentViewType;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewHolder viewHolder;
        View itemView;

        switch (viewType) {

            case NORMAL_VIEW:
                itemView = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.song_details_item_layout, parent, false);
                viewHolder = new NormalViewHolder(itemView);
                break;

            case LOADING_VIEW:
                itemView = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.loading_view, parent, false);
                viewHolder = new ViewHolder(itemView);
                break;

            case FAILURE_VIEW:
                itemView = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.failure_view, parent, false);
                viewHolder = new FailureViewHolder(itemView);
                break;

            case EMPTY_VIEW:
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

        int viewType = getItemViewType(position);

        switch (viewType) {

            case NORMAL_VIEW:

                cursor.moveToPosition(position);
                NormalViewHolder normalViewHolder = (NormalViewHolder) holder;

                String coverImageUrl = cursor.getString(cursor.getColumnIndex(
                        SongsEntry.COLUMN_SONG_COVER_IMAGE))
                        .replace("http:", "https:");

                Picasso.with(context)
                        .load(coverImageUrl)
                        .placeholder(R.drawable.imageview_loading_placeholder)
                        .error(R.drawable.imageview_error_placeholder)
                        .into(normalViewHolder.imageViewCover);

                normalViewHolder.textViewSongName.setText(
                        cursor.getString(cursor.getColumnIndex(SongsEntry.COLUMN_SONG_NAME)));

                normalViewHolder.textViewArtists.setText(
                        cursor.getString(cursor.getColumnIndex(SongsEntry.COLUMN_SONG_ARTISTS)));

                float rating = cursor.getLong(cursor.getColumnIndex(
                        SongsEntry.COLUMN_SONG_FAVORITE)) == 1 ? 1.0f : 0.0f;
                normalViewHolder.ratingBar.setRating(rating);

                normalViewHolder.ratingBar.setTag(
                        cursor.getLong(cursor.getColumnIndex(SongsEntry._ID)));

                break;

            case LOADING_VIEW:
                break;

            case FAILURE_VIEW:
                break;

            case EMPTY_VIEW:
                break;
        }
    }

    @Override
    public int getItemCount() {

        if (cursor == null || cursor.getCount() == 0)
            return 1;
        else
            return cursor.getCount();
    }

    @Override
    public int getItemViewType(int position) {

        if (currentViewType == LOADING_VIEW)
            return LOADING_VIEW;
        else if (currentViewType == FAILURE_VIEW)
            return FAILURE_VIEW;
        else if (cursor == null || cursor.getCount() == 0)
            return EMPTY_VIEW;
        else
            return NORMAL_VIEW;
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

            ContentValues values = new ContentValues();
            values.put(SongsEntry.COLUMN_SONG_FAVORITE, (long) rating);

            context.getContentResolver().update(
                    SongsEntry.CONTENT_URI.buildUpon().appendPath(ratingBar.getTag().toString()).build(),
                    values, null, null);
        }
    }

    public class FailureViewHolder extends ViewHolder {

        public FailureViewHolder(View itemView) {
            super(itemView);
        }
    }
}
