package com.jk.alienplayer.metadata;

import android.text.TextUtils;

public class TrackTagInfo {
    private String title;
    private String artists;
    private String album;
    private String artistAlbum;
    private String track;
    private String year;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            this.title = "";
        } else {
            this.title = title;
        }
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        if (TextUtils.isEmpty(artists)) {
            this.artists = "";
        } else {
            this.artists = artists;
        }
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        if (TextUtils.isEmpty(album)) {
            this.album = "";
        } else {
            this.album = album;
        }
    }

    public String getArtistAlbum() {
        return artistAlbum;
    }

    public void setArtistAlbum(String artistAlbum) {
        if (TextUtils.isEmpty(artistAlbum)) {
            this.artistAlbum = "";
        } else {
            this.artistAlbum = artistAlbum;
        }
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        if (TextUtils.isEmpty(track)) {
            this.track = "";
        } else {
            this.track = track;
        }
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        if (TextUtils.isEmpty(year)) {
            this.year = "";
        } else {
            this.year = year;
        }
    }
}
