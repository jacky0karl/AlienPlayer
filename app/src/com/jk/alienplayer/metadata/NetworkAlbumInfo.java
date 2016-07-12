package com.jk.alienplayer.metadata;

public class NetworkAlbumInfo extends NetworkSearchResult {

    public String avatar;
    public String artist;
    public long publishTime;

    public NetworkAlbumInfo(long id, String name, String avatar, String artist, long publishTime) {
        super(id, TYPE_ALBUMS, name);
        this.avatar = avatar;
        this.artist = artist;
        this.publishTime = publishTime;
    }

}
