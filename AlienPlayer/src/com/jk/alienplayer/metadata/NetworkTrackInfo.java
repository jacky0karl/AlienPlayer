package com.jk.alienplayer.metadata;

import java.util.Calendar;

public class NetworkTrackInfo extends NetworkSearchResult {
    public String coverUrl;
    public String artists;
    public String artistAlbum;
    public String album;
    public long dfsId = 0;
    public int position = 0;
    public int year = 0;
    public String ext;

    public NetworkTrackInfo(long id, String name, String artists) {
        super(id, TYPE_TRACKS, name);
        this.artists = artists;
    }

    public void setYear(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        year = calendar.get(Calendar.YEAR);
    }
}
