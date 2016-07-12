package com.jk.alienplayer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

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

    @JsonProperty("artists")
    private List<ArtistBean> artists = new ArrayList<>();

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

    @JsonProperty("artists")
    public List<ArtistBean> getArtists() {
        return artists;
    }

    @JsonProperty("artists")
    public void setArtists(List<ArtistBean> artists) {
        this.artists = artists;
    }

    public String getShowingArtist() {
        if (artists == null) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        for (ArtistBean bean : artists) {
            sb.append(bean.getName());
            sb.append("&");
        }
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }
}
