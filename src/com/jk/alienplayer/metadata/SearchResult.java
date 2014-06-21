package com.jk.alienplayer.metadata;

public class SearchResult {
    public static final int TYPE_ARTISTS = 0;
    public static final int TYPE_ALBUMS = 1;
    public static final int TYPE_TRACKS = 2;

    public interface SearchResultData {
        long getId();

        String getDisplayName();
    }

    public int type;
    public SearchResultData data;

    public SearchResult(int type, SearchResultData data) {
        this.type = type;
        this.data = data;
    }
}
