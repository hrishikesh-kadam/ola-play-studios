package com.example.android.olaplaystudios.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Hrishikesh Kadam on 16/12/2017
 */

public class StudiosContract {

    public static final String AUTHORITY = "com.example.android.olaplaystudios";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_SONGS = "songs";
    public static final String PATH_NOW_PLAYING = "now-playing";

    public static final class SongsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SONGS).build();

        public static final String TABLE_NAME = "songs";

        public static final String COLUMN_SONG_NAME = "song";
        public static final String COLUMN_SONG_URL = "url";
        public static final String COLUMN_SONG_ARTISTS = "artists";
        public static final String COLUMN_SONG_COVER_IMAGE = "cover_image";
    }

    public static final class NowPlayingEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_NOW_PLAYING).build();

        public static final String TABLE_NAME = "\"now-playing\"";

        public static final String COLUMN_SONG_URL = "url";
        public static final String COLUMN_ACTION = "action";
    }
}
