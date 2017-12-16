package com.example.android.olaplaystudios;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    public class NormalViewHolder extends ViewHolder {

        @BindView(R.id.textViewSongName)
        TextView textViewSongName;

        @BindView(R.id.textViewArtists)
        TextView textViewArtists;

        @BindView(R.id.imageViewCover)
        ImageView imageViewCover;

        public NormalViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class FailureViewHolder extends ViewHolder {

        public FailureViewHolder(View itemView) {
            super(itemView);
        }
    }
}
