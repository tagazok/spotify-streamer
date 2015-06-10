package com.example.android.spotifystreamer;

/**
 * Created by olivier on 08/06/15.
 */
public class Track {
    private String id;
    private String title;
    private String imgUrl;
    private String album;

    public Track(String id, String title, String imgUrl, String album) {
        this.id = id;
        this.title = title;
        this.imgUrl = imgUrl;
        this.album = album;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getTitle() {
        return title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return title;
    }
}
