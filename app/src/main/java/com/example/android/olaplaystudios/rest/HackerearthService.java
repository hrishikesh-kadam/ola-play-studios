package com.example.android.olaplaystudios.rest;

import com.example.android.olaplaystudios.model.SongDetails;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Hrishikesh Kadam on 16/12/2017
 */

public interface HackerearthService {

    @GET("studio")
    Call<ArrayList<SongDetails>> getAllSongs();
}
