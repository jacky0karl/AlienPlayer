package com.jk.alienplayer.model;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by junjie.qu on 6/22/2016.
 */
public class TracksBean extends BaseBean {
    private TracksDataBean album = null;

    public TracksDataBean getAlbum() {
        return album;
    }

    public void setAlbum(TracksDataBean album) {
        this.album = album;
    }

    public class TracksDataBean {
        private List<TrackBean> songs = new ArrayList<>();

        public List<TrackBean> getSongs() {
            return songs;
        }

        public void setSongs(List<TrackBean> songs) {
            this.songs = songs;
        }
    }
}
