package com.jk.alienplayer.metadata;

import com.jk.alienplayer.metadata.SearchResult.SearchResultData;

import android.text.TextUtils;

public class PlaylistInfo implements SearchResultData {
    public long id;
    public String name;

    public PlaylistInfo(long id, String name) {
        this.id = id;
        if (TextUtils.isEmpty(name)) {
            this.name = "unknown";
        } else {
            this.name = name;
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
