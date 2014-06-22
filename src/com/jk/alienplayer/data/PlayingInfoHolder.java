package com.jk.alienplayer.data;

import java.util.List;

import com.jk.alienplayer.R;
import com.jk.alienplayer.metadata.SongInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

public class PlayingInfoHolder {    
    private static PlayingInfoHolder sSelf = null;

    private SongInfo mCurrentSong = null;
    private List<SongInfo> mRecentsList = null;
    private Bitmap mPlaybarArtwork = null;
    private int mPlaybarArtworkSize;

    public static synchronized PlayingInfoHolder getInstance() {
        if (sSelf == null) {
            sSelf = new PlayingInfoHolder();
        }
        return sSelf;
    }

    private PlayingInfoHolder() {

    }

    public void init(Context context) {
        mPlaybarArtworkSize = context.getResources().getDimensionPixelOffset(
                R.dimen.playbar_artwork_size);

        // init CurrentSong
        SharedPreferences sp = PreferencesHelper.getSharedPreferences(context);
        long songId = sp.getLong(PreferencesHelper.CURRENT_SONG, -1);
        if (songId != -1) {
            SongInfo info = DatabaseHelper.getSong(context, songId);
            if (info != null) {
                mCurrentSong = info;
                mPlaybarArtwork = DatabaseHelper.getArtwork(context, mCurrentSong.id,
                        mCurrentSong.albumId, mPlaybarArtworkSize);
            }
        }

        // init RecentsList
        RecentsDBHelper.initRecents(context);
        mRecentsList = RecentsDBHelper.getRecentTracks(context);
    }

    public SongInfo getCurrentSong() {
        return mCurrentSong;
    }

    public Bitmap getPlaybarArtwork() {
        return mPlaybarArtwork;
    }

    public void setCurrentSong(Context context, SongInfo currentSong) {
        if (currentSong != null) {
            mCurrentSong = currentSong;
            PreferencesHelper.putLongValue(context, PreferencesHelper.CURRENT_SONG, currentSong.id);

            mPlaybarArtwork = DatabaseHelper.getArtwork(context, mCurrentSong.id,
                    mCurrentSong.albumId, mPlaybarArtworkSize);

            // update Recents
            boolean update = isInRecents(currentSong.id);
            if (!update) {
                mRecentsList.add(currentSong);
            }
            RecentsDBHelper.addToRecents(context, currentSong.id, update);
        }
    }

    private boolean isInRecents(long newId) {
        for (int i = 0; i < mRecentsList.size(); i++) {
            if (mRecentsList.get(i).id == newId) {
                return true;
            }
        }
        return false;
    }
}
