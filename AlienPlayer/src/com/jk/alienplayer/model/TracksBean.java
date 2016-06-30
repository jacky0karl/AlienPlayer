package com.jk.alienplayer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by junjie.qu on 6/22/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TracksBean extends BaseBean {
    @JsonProperty("album")
    private TracksDataBean album = null;

    @JsonProperty("album")
    public TracksDataBean getAlbum() {
        return album;
    }

    @JsonProperty("album")
    public void setAlbum(TracksDataBean album) {
        this.album = album;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class TracksDataBean {
        @JsonProperty("songs")
        private List<TrackBean> songs = new ArrayList<>();

        @JsonProperty("songs")
        public List<TrackBean> getSongs() {
            return songs;
        }

        @JsonProperty("songs")
        public void setSongs(List<TrackBean> songs) {
            this.songs = songs;
        }
    }
}
