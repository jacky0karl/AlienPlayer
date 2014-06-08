package com.jk.alienplayer.data;

import android.text.TextUtils;

public class ArtistInfo {

    public String name;

    public ArtistInfo(String name) {
        if (TextUtils.isEmpty(name)) {
            this.name = "unknown";
        } else {
            this.name = name;
        }
    }

}
