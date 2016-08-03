package com.jk.alienplayer.model;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by junjie.qu on 6/22/2016.
 */

public class SearchBean extends BaseBean {
    private SearchResultBean result = null;

    public SearchResultBean getResult() {
        return result;
    }

    public void setResult(SearchResultBean result) {
        this.result = result;
    }

    public static class SearchResultBean {
        private int artistCount = 0;
        private List<ArtistBean> artists = new ArrayList<>();
        private int albumCount = 0;
        private List<AlbumBean> albums = new ArrayList<>();

        public int getArtistCount() {
            return artistCount;
        }

        public void setArtistCount(int artistCount) {
            this.artistCount = artistCount;
        }

        public List<ArtistBean> getArtists() {
            return artists;
        }

        public void setArtists(List<ArtistBean> artists) {
            this.artists = artists;
        }

        public int getAlbumCount() {
            return albumCount;
        }

        public void setAlbumCount(int albumCount) {
            this.albumCount = albumCount;
        }

        public List<AlbumBean> getAlbums() {
            return albums;
        }

        public void setAlbums(List<AlbumBean> albums) {
            this.albums = albums;
        }
    }
}
