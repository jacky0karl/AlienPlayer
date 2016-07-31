package com.jk.alienplayer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by junjie.qu on 6/22/2016.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchBean extends BaseBean {
    @JsonProperty("result")
    private SearchResultBean result = null;

    @JsonProperty("result")
    public SearchResultBean getResult() {
        return result;
    }

    @JsonProperty("result")
    public void setResult(SearchResultBean result) {
        this.result = result;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SearchResultBean {
        @JsonProperty("artistCount")
        private int artistCount = 0;
        @JsonProperty("artists")
        private List<ArtistBean> artists = new ArrayList<>();
        @JsonProperty("albumCount")
        private int albumCount = 0;
        @JsonProperty("albums")
        private List<AlbumBean> albums = new ArrayList<>();

        @JsonProperty("artistCount")
        public int getArtistCount() {
            return artistCount;
        }

        @JsonProperty("artistCount")
        public void setArtistCount(int artistCount) {
            this.artistCount = artistCount;
        }

        @JsonProperty("artists")
        public List<ArtistBean> getArtists() {
            return artists;
        }

        @JsonProperty("artists")
        public void setArtists(List<ArtistBean> artists) {
            this.artists = artists;
        }

        @JsonProperty("albumCount")
        public int getAlbumCount() {
            return albumCount;
        }

        @JsonProperty("albumCount")
        public void setAlbumCount(int albumCount) {
            this.albumCount = albumCount;
        }

        @JsonProperty("albums")
        public List<AlbumBean> getAlbums() {
            return albums;
        }

        @JsonProperty("albums")
        public void setAlbums(List<AlbumBean> albums) {
            this.albums = albums;
        }
    }
}
