package com.jk.alienplayer.metadata;

import android.text.TextUtils;

import com.jk.alienplayer.metadata.SearchResult.SearchResultData;

public class SongInfo implements SearchResultData {
    public long id;
    public String title;
    public long duration;
    public String path;

    public String artist;
    public long artistId;
    public String album;
    public long albumId;

    public SongInfo(long id, String title, long duration, String path) {
        if (TextUtils.isEmpty(title)) {
            this.title = "unknown";
        } else {
            this.title = title;
        }

        this.id = id;
        this.duration = duration;
        this.path = path;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getDisplayName() {
        return title;
    }
}
