package com.jk.alienplayer.metadata;

public class NetworkTrackInfo extends NetworkSearchResult {

    public String artists;
    public int position = 0;
    public long dfsId = 0;
    public boolean downloaded = false;

    public NetworkTrackInfo(long id, String name, String artists) {
        super(id, TYPE_TRACKS, name);
        this.artists = artists;
    }

}
