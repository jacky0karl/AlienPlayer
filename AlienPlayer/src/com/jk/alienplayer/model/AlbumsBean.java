package com.jk.alienplayer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by junjie.qu on 6/22/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlbumsBean extends BaseBean {
    @JsonProperty("artist")
    private ArtistBean artist = null;

    @JsonProperty("hotAlbums")
    private List<AlbumBean> hotAlbums = new ArrayList<AlbumBean>();

    @JsonProperty("artist")
    public ArtistBean getArtist() {
        return artist;
    }

    @JsonProperty("artist")
    public void setArtist(ArtistBean artist) {
        this.artist = artist;
    }

    @JsonProperty("hotAlbums")
    public List<AlbumBean> getHotAlbums() {
        return hotAlbums;
    }

    @JsonProperty("hotAlbums")
    public void setHotAlbums(List<AlbumBean> hotAlbums) {
        this.hotAlbums = hotAlbums;
    }
}
