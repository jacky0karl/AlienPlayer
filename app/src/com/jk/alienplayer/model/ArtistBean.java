package com.jk.alienplayer.model;


/**
 * Created by junjie.qu on 6/22/2016.
 */
public class ArtistBean {
    private long id = 0;

    private String name = "";

    private String picUrl = "";

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
