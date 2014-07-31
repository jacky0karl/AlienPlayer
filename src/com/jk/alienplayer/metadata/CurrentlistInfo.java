package com.jk.alienplayer.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class CurrentlistInfo {
    public static final int ID_ALL = -100;
    public static final int ID_RECENT = -101;

    public static final int TYPE_ALL = 0;
    public static final int TYPE_ARTIST = 1;
    public static final int TYPE_ALBUM = 2;
    public static final int TYPE_PLAYLIST = 3;
    public static final int TYPE_RECENT = 4;

    private long id;
    private int type;
    private int currentIndex = 0;
    private List<SongInfo> songList = null;
    private Stack<Integer> playedStack = null; // for shuffle

    public CurrentlistInfo(long id, int type, List<SongInfo> songList) {
        this.id = id;
        this.type = type;

        if (songList == null) {
            this.songList = new ArrayList<SongInfo>();
        } else {
            this.songList = songList;
        }
        playedStack = new Stack<Integer>();
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

    public void shuffleNext() {
        int size = songList.size();
        if (playedStack.size() >= size) {
            playedStack.clear();
        }

        Random random = new Random();
        int index = 0;
        while (true) {
            index = random.nextInt(songList.size());
            if (!playedStack.contains(index)) {
                playedStack.push(index);
                currentIndex = index;
                return;
            }
        }
    }

    public void shufflePrev() {
        if (!playedStack.empty()) {
            playedStack.pop();
            if (!playedStack.empty()) {
                currentIndex = playedStack.peek();
                return;
            }
        }
        prev();
    }
}
