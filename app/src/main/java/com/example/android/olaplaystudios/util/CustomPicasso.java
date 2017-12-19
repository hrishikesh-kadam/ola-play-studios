package com.example.android.olaplaystudios.util;

import android.content.Context;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

/**
 * Created by Hrishikesh Kadam on 19/12/2017
 */

public class CustomPicasso {

    public static Picasso getPicasso(Context context) {

        return new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(context))
                .build();
    }
}
