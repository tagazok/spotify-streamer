package com.example.android.spotifystreamer;

/**
 * Created by olivier on 08/06/15.
 */
public class Track {
    public String title;
    public String imgUrl;

    public Track(String title, String imgUrl) {
        this.title = title;
        this.imgUrl = imgUrl;
    }
    @Override
    public String toString() {
        return title;
    }
}
