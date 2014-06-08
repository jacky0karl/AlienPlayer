package com.jk.alienplayer.data;

import android.text.TextUtils;

public class AlbumInfo {
    public long id;
    public String name;

    public AlbumInfo(long id, String name) {
        this.id = id;
        if (TextUtils.isEmpty(name)) {
            this.name = "unknown";
        } else {
            this.name = name;
        }
    }

}
