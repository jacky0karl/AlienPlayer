package com.jk.alienplayer.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by junjie.qu on 6/22/2016.
 */
public class TrackBean {
    private long id = 0;

    private String name = "";

    private AlbumBean album = null;

    private List<ArtistBean> artists = new ArrayList<>();

    private MusicBean bMusic = null;

    private MusicBean hMusic = null;

    private MusicBean mMusic = null;

    private MusicBean lMusic = null;

    private long position = 0;

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

    public AlbumBean getAlbum() {
        return album;
    }

    public void setAlbum(AlbumBean album) {
        this.album = album;
    }

    public List<ArtistBean> getArtists() {
        return artists;
    }

    public void setArtists(List<ArtistBean> artists) {
        this.artists = artists;
    }

    public MusicBean getbMusic() {
        return bMusic;
    }

    public void setbMusic(MusicBean bMusic) {
        this.bMusic = bMusic;
    }

    public MusicBean gethMusic() {
        return hMusic;
    }

    public void sethMusic(MusicBean hMusic) {
        this.hMusic = hMusic;
    }

    public MusicBean getmMusic() {
        return mMusic;
    }

    public void setmMusic(MusicBean mMusic) {
        this.mMusic = mMusic;
    }

    public MusicBean getlMusic() {
        return lMusic;
    }

    public void setlMusic(MusicBean lMusic) {
        this.lMusic = lMusic;
    }

    public long getPosition() {
        return position;
    }

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
