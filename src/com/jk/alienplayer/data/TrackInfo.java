package com.jk.alienplayer.data;

import android.text.TextUtils;

public class TrackInfo {
    public long id;
    public String title;
    public long duration;
    public String path;
    public long albumId;

    public TrackInfo(long id, String title, long duration, String path) {
        if (TextUtils.isEmpty(title)) {
            this.title = "unknown";
        } else {
            this.title = title;
        }

        this.id = id;
        this.duration = duration;
        this.path = path;
    }

}
