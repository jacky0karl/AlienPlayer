package com.jk.alienplayer.metadata;

import com.jk.alienplayer.metadata.SearchResult.SearchResultData;

import android.text.TextUtils;

public class AlbumInfo implements SearchResultData {
    public long id;
    public String name;
    public String artist;
    public String artwork;
    public int year = 0;

    public AlbumInfo(long id, String name, String artist) {
        this.id = id;
        if (TextUtils.isEmpty(name)) {
            this.name = "unknown";
        } else {
            this.name = name;
        }

        if (TextUtils.isEmpty(artist)) {
            this.artist = "unknown";
        } else {
            this.artist = artist;
        }
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getDisplayName() {
        return name;
    }
}
