package com.example.android.olaplaystudios.model;

/**
 * Created by Hrishikesh Kadam on 20/12/2017
 */

public class NowPlaying {

    private static NowPlaying nowPlaying;
    private String songUrl;
    private String action;
    private String lastSongUrl;
    private String lastAction;

    private NowPlaying() {
    }

    private static void init() {

        if (nowPlaying == null)
            nowPlaying = new NowPlaying();
    }

    public static void setNowPlaying(String songUrl, String action) {
        init();
        setLastPlayed(nowPlaying.songUrl, nowPlaying.action);
        nowPlaying.songUrl = songUrl;
        nowPlaying.action = action;
    }

    private static void setLastPlayed(String lastSongUrl, String lastAction) {
        nowPlaying.lastSongUrl = lastSongUrl;
        nowPlaying.lastAction = lastAction;
    }

    public static String getSongUrl() {
        init();
        return nowPlaying.songUrl;
    }

    public static String getAction() {
        init();
        return nowPlaying.action;
    }

    public static void destroy() {
        nowPlaying = null;
    }

    public static String getLastSongUrl() {
        init();
        return nowPlaying.lastSongUrl;
    }

    public static String getLastAction() {
        init();
        return nowPlaying.lastAction;
    }
}
