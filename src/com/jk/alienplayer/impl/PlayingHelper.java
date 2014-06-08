package com.jk.alienplayer.impl;

import android.media.MediaPlayer;

import com.jk.alienplayer.data.SongInfo;

public class PlayingHelper {

    private static PlayingHelper sSelf = null;
    private MediaPlayer mMediaPlayer;
    private SongInfo mCurrentSong = null;

    public static synchronized PlayingHelper getInstance() {
        if (sSelf == null) {
            sSelf = new PlayingHelper();
        }
        return sSelf;
    }

    private PlayingHelper() {
        mMediaPlayer = new MediaPlayer();
    }

    public SongInfo getCurrentSong() {
        return mCurrentSong;
    }

    public void setCurrentSong(SongInfo currentSong) {
        mCurrentSong = currentSong;
    }

    public boolean playOrPause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        } else {
            try {
                mMediaPlayer.start();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        return mMediaPlayer.isPlaying();
    }

    public boolean play() {
        if (mCurrentSong == null) {
            return false;
        }

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }

        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(mCurrentSong.path);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void release() {
        try {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sSelf = null;
    }
}
