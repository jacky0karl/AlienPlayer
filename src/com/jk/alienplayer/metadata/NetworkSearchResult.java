package com.jk.alienplayer.metadata;

public class NetworkSearchResult {
    public static final int TYPE_ARTISTS = 0;
    public static final int TYPE_ALBUMS = 1;
    public static final int TYPE_TRACKS = 2;

    public long id;
    public int type;
    public String name;

    public NetworkSearchResult(long id, int type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }
}
