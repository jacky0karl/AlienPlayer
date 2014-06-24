package com.jk.alienplayer.data;

import java.util.List;

import com.jk.alienplayer.R;
import com.jk.alienplayer.metadata.CurrentlistInfo;
import com.jk.alienplayer.metadata.SongInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

public class PlayingInfoHolder {
    private static PlayingInfoHolder sSelf = null;

    private CurrentlistInfo mCurrentlistInfo = null;

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
        long songId = sp.getLong(PreferencesHelper.CURRENT_SONG_ID, -1);
        SongInfo currentSong = null;
        if (songId != -1) {
            SongInfo info = DatabaseHelper.getSong(context, songId);
            if (info != null) {
                currentSong = info;
                mPlaybarArtwork = DatabaseHelper.getArtwork(context, currentSong.id,
                        currentSong.albumId, mPlaybarArtworkSize);
            }
        }

        // init RecentsList
        RecentsDBHelper.initRecents(context);
        mRecentsList = RecentsDBHelper.getRecentTracks(context);

        // init CurrentSongList
        long songListId = sp.getLong(PreferencesHelper.CURRENT_SONG_LIST_ID, -1);
        int songListType = sp.getInt(PreferencesHelper.CURRENT_SONG_LIST_TYPE, -1);
        List<SongInfo> currentSongList = null;
        if (songListId != -1 && songListType != -1) {
            if (songListType == CurrentlistInfo.TYPE_PLAYLIST) {
                currentSongList = DatabaseHelper.getPlaylistMembers(context, songListId);
            } else if (songListType == CurrentlistInfo.TYPE_RECENT) {
                currentSongList = mRecentsList;
            } else {
                currentSongList = DatabaseHelper.getTracks(context, songListType, songListId);
            }
        }
        mCurrentlistInfo = new CurrentlistInfo(songListId, songListType, currentSongList);

        if (!mCurrentlistInfo.setCurrentSong(currentSong)) {
            setCurrentSong(context, getCurrentSong());
        }
    }

    public SongInfo getCurrentSong() {
        return mCurrentlistInfo.getCurrentSong();
    }

    public Bitmap getPlaybarArtwork() {
        return mPlaybarArtwork;
    }

    public void setCurrentInfo(Context context, SongInfo currentSong,
            CurrentlistInfo currentlistInfo) {
        // update Recents
        boolean update = isInRecents(currentSong.id);
        if (!update) {
            mRecentsList.add(currentSong);
        }
        RecentsDBHelper.addToRecents(context, currentSong.id, update);

        setCurrentlistInfo(context, currentlistInfo);
        if (mCurrentlistInfo.setCurrentSong(currentSong)) {
            setCurrentSong(context, currentSong);
        } else {
            setCurrentSong(context, getCurrentSong());
        }
    }

    private void setCurrentSong(Context context, SongInfo currentSong) {
        if (currentSong != null) {
            PreferencesHelper.putLongValue(context, PreferencesHelper.CURRENT_SONG_ID,
                    currentSong.id);

            mPlaybarArtwork = DatabaseHelper.getArtwork(context, currentSong.id,
                    currentSong.albumId, mPlaybarArtworkSize);
        }
    }

    private void setCurrentlistInfo(Context context, CurrentlistInfo currentlistInfo) {
        if (currentlistInfo != null) {
            mCurrentlistInfo = currentlistInfo;
        } else {
            List<SongInfo> recentsList = RecentsDBHelper.getRecentTracks(context);
            mCurrentlistInfo = new CurrentlistInfo(0, CurrentlistInfo.TYPE_RECENT, recentsList);
        }

        PreferencesHelper.putLongValue(context, PreferencesHelper.CURRENT_SONG_LIST_ID,
                mCurrentlistInfo.getId());
        PreferencesHelper.putIntValue(context, PreferencesHelper.CURRENT_SONG_LIST_TYPE,
                mCurrentlistInfo.getType());
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
