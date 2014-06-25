package com.jk.alienplayer.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CurrentlistInfo {

    public static final int TYPE_ALL = 0;
    public static final int TYPE_ARTIST = 1;
    public static final int TYPE_ALBUM = 2;
    public static final int TYPE_PLAYLIST = 3;
    public static final int TYPE_RECENT = 4;

    private long id;
    private int type;
    private int currentIndex = 0;
    private List<SongInfo> songList = null;

    public CurrentlistInfo(long id, int type, List<SongInfo> songList) {
        this.id = id;
        this.type = type;

        if (songList == null) {
            this.songList = new ArrayList<SongInfo>();
        } else {
            this.songList = songList;
        }
    }

    public boolean equals(long id, int type) {
        if (this.id == id || this.type == type) {
            return true;
        }
        return false;
    }

    public long getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public List<SongInfo> getSongList() {
        return songList;
    }

    public SongInfo getCurrentSong() {
        if (songList.size() == 0) {
            return null;
        }
        return songList.get(currentIndex);
    }

    public boolean setCurrentSong(SongInfo info) {
        if (info == null) {
            return false;
        }

        for (int i = 0; i < songList.size(); i++) {
            if (songList.get(i).id == info.id) {
                currentIndex = i;
                return true;
            }
        }
        return false;
    }

    public void next() {
        currentIndex++;
        if (currentIndex >= songList.size()) {
            currentIndex = 0;
        }
    }

    public void prev() {
        currentIndex--;
        if (currentIndex < 0) {
            currentIndex = songList.size() - 1;
        }
    }

    public void shuffle() {
        currentIndex = new Random().nextInt(songList.size());
    }
}
