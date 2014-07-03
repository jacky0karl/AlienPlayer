package com.jk.alienplayer.metadata;

public class NetworkTrackInfo extends NetworkSearchResult {

    public String artists;
    public String artistAlbum;
    public String album;
    public long dfsId = 0;
    public int position = 0;
    public String ext;

    public NetworkTrackInfo(long id, String name, String artists) {
        super(id, TYPE_TRACKS, name);
        this.artists = artists;
    }

}
