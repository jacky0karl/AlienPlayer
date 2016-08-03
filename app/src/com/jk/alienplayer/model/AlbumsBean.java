package com.jk.alienplayer.model;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by junjie.qu on 6/22/2016.
 */
public class AlbumsBean extends BaseBean {
    private List<AlbumBean> hotAlbums = new ArrayList<AlbumBean>();

    public List<AlbumBean> getHotAlbums() {
        return hotAlbums;
    }

    public void setHotAlbums(List<AlbumBean> hotAlbums) {
        this.hotAlbums = hotAlbums;
    }
}
