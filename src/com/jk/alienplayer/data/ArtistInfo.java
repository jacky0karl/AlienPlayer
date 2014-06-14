package com.jk.alienplayer.data;

import com.jk.alienplayer.data.SearchResult.SearchResultData;

import android.text.TextUtils;

public class ArtistInfo implements SearchResultData {
    public long id;
    public String name;

    public ArtistInfo(long id, String name) {
        this.id = id;
        if (TextUtils.isEmpty(name)) {
            this.name = "unknown";
        } else {
            this.name = name;
        }
    }

    @Override
    public String getDisplayName() {
        return name;
    }

}
