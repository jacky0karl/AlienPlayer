package com.jk.alienplayer.metadata;

public class NetworkTrackInfo extends NetworkSearchResult {

    public String artists;

    public NetworkTrackInfo(long id, String name, String artists) {
        super(id, TYPE_TRACKS, name);
        this.artists = artists;
    }

}
