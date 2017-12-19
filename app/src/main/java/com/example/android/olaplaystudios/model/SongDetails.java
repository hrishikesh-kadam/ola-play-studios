package com.example.android.olaplaystudios.model;

/**
 * Created by Hrishikesh Kadam on 16/12/2017
 */

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SongDetails implements Parcelable {

    public final static Parcelable.Creator<SongDetails> CREATOR = new Creator<SongDetails>() {

        @SuppressWarnings({
                "unchecked"
        })
        public SongDetails createFromParcel(Parcel in) {
            return new SongDetails(in);
        }

        public SongDetails[] newArray(int size) {
            return (new SongDetails[size]);
        }

    };

    @SerializedName("song")
    @Expose
    private String song;

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("artists")
    @Expose
    private String artists;

    @SerializedName("cover_image")
    @Expose
    private String coverImage;

    private Boolean favorite;

    private Long databaseId;

    protected SongDetails(Parcel in) {
        this.song = ((String) in.readValue((String.class.getClassLoader())));
        this.url = ((String) in.readValue((String.class.getClassLoader())));
        this.artists = ((String) in.readValue((String.class.getClassLoader())));
        this.coverImage = ((String) in.readValue((String.class.getClassLoader())));
        this.favorite = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.databaseId = (Long) in.readValue(Long.class.getClassLoader());
    }

    public SongDetails() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(song);
        dest.writeValue(url);
        dest.writeValue(artists);
        dest.writeValue(coverImage);
        dest.writeValue(favorite);
        dest.writeValue(databaseId);
    }

    public int describeContents() {
        return 0;
    }

    public String getSong() {
        return TextUtils.isEmpty(song) ? "NA" : song;
    }

    public String getUrl() {
        return TextUtils.isEmpty(url) ? "NA" : url;
    }

    public String getArtists() {
        return TextUtils.isEmpty(artists) ? "NA" : artists;
    }

    public String getCoverImage() {
        return TextUtils.isEmpty(coverImage) ? "NA" : coverImage;
    }

    public Boolean getFavorite() {
        return favorite == null ? false : favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public Long getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(Long databaseId) {
        this.databaseId = databaseId;
    }

    @Override
    public String toString() {
        return "SongDetails{" +
                "song='" + song + '\'' +
                ", url='" + url + '\'' +
                ", artists='" + artists + '\'' +
                ", coverImage='" + coverImage + '\'' +
                ", favorite=" + favorite +
                ", databaseId=" + databaseId +
                '}';
    }
}
