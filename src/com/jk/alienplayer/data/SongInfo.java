package com.jk.alienplayer.data;

import com.jk.alienplayer.data.SearchResult.SearchResultData;

import android.text.TextUtils;

public class SongInfo implements SearchResultData {
    public long id;
    public String title;
    public long duration;
    public String path;
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
    public String getDisplayName() {
        return title;
    }

}
