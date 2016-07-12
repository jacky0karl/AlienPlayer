package com.jk.alienplayer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by junjie.qu on 6/22/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackBean {
    @JsonProperty("id")
    private long id = 0;

    @JsonProperty("name")
    private String name = "";

    @JsonProperty("album")
    private AlbumBean album = null;

    @JsonProperty("artists")
    private List<ArtistBean> artists = new ArrayList<>();

    @JsonProperty("bMusic")
    private MusicBean bMusic = null;

    @JsonProperty("hMusic")
    private MusicBean hMusic = null;

    @JsonProperty("mMusic")
    private MusicBean mMusic = null;

    @JsonProperty("lMusic")
    private MusicBean lMusic = null;

    @JsonProperty("position")
    private long position = 0;

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

    @JsonProperty("album")
    public AlbumBean getAlbum() {
        return album;
    }

    @JsonProperty("album")
    public void setAlbum(AlbumBean album) {
        this.album = album;
    }

    @JsonProperty("artists")
    public List<ArtistBean> getArtists() {
        return artists;
    }

    @JsonProperty("artists")
    public void setArtists(List<ArtistBean> artists) {
        this.artists = artists;
    }

    @JsonProperty("bMusic")
    public MusicBean getbMusic() {
        return bMusic;
    }

    @JsonProperty("bMusic")
    public void setbMusic(MusicBean bMusic) {
        this.bMusic = bMusic;
    }

    @JsonProperty("hMusic")
    public MusicBean gethMusic() {
        return hMusic;
    }

    @JsonProperty("hMusic")
    public void sethMusic(MusicBean hMusic) {
        this.hMusic = hMusic;
    }

    @JsonProperty("mMusic")
    public MusicBean getmMusic() {
        return mMusic;
    }

    @JsonProperty("mMusic")
    public void setmMusic(MusicBean mMusic) {
        this.mMusic = mMusic;
    }

    @JsonProperty("lMusic")
    public MusicBean getlMusic() {
        return lMusic;
    }

    @JsonProperty("lMusic")
    public void setlMusic(MusicBean lMusic) {
        this.lMusic = lMusic;
    }

    @JsonProperty("position")
    public long getPosition() {
        return position;
    }

    @JsonProperty("position")
    public void setPosition(long position) {
        this.position = position;
    }

    public long getDfsId() {
        if (hMusic != null) {
            return hMusic.getDfsId();
        }

        if (mMusic != null) {
            return mMusic.getDfsId();
        }

        if (lMusic != null) {
            return lMusic.getDfsId();
        }

        if (bMusic != null) {
            return bMusic.getDfsId();
        }
        return 0;
    }

    public String getExtension() {
        if (hMusic != null) {
            return hMusic.getExtension();
        }

        if (mMusic != null) {
            return mMusic.getExtension();
        }

        if (lMusic != null) {
            return lMusic.getExtension();
        }

        if (bMusic != null) {
            return bMusic.getExtension();
        }
        return "mp3";
    }

    public String getShowingArtists() {
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
