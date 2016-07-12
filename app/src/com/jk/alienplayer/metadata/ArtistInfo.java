package com.jk.alienplayer.metadata;

import java.util.Comparator;

import com.jk.alienplayer.metadata.SearchResult.SearchResultData;

import android.text.TextUtils;

public class ArtistInfo implements SearchResultData {
    public static final long ALBUM_ARTIST_ID = -1;

    public long id;
    public String name;
    public String sortKey;

    public ArtistInfo(long id, String name) {
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

    public static class ArtistComparator implements Comparator<ArtistInfo> {
        @Override
        public int compare(ArtistInfo lhs, ArtistInfo rhs) {
            return lhs.sortKey.compareToIgnoreCase(rhs.sortKey);
        }
    }
}
