package com.example.android.olaplaystudios.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Hrishikesh Kadam on 16/12/2017
 */

public class RetrofitSingleton {

    private static Retrofit retrofit;
    private static HackerearthService hackerearthService;

    public static Retrofit getRetrofit() {

        if (retrofit == null) {
            return new Retrofit.Builder()
                    .baseUrl("http://starlord.hackerearth.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        } else
            return retrofit;
    }

    public static HackerearthService getHackerearthService() {

        return getRetrofit().create(HackerearthService.class);
    }
}
