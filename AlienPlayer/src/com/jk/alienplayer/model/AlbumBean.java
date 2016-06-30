package com.jk.alienplayer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by junjie.qu on 6/22/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlbumBean {
    @JsonProperty("id")
    private long id = 0;

    @JsonProperty("name")
    private String name = "";

    @JsonProperty("picUrl")
    private String picUrl = "";

    @JsonProperty("publishTime")
    private long publishTime = 0L;

    @JsonProperty("artist")
    private ArtistBean artist = null;

    @JsonProperty("id")
    public long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(long id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("picUrl")
    public String getPicUrl() {
        return picUrl;
    }

    @JsonProperty("picUrl")
    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    @JsonProperty("publishTime")
    public long getPublishTime() {
        return publishTime;
    }

    @JsonProperty("publishTime")
    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }

    @JsonProperty("artist")
    public ArtistBean getArtist() {
        return artist;
    }

    @JsonProperty("artist")
    public void setArtist(ArtistBean artist) {
        this.artist = artist;
    }
}
