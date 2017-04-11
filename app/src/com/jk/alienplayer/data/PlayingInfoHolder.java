package com.jk.alienplayer.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;

import com.jk.alienplayer.R;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.metadata.CurrentlistInfo;
import com.jk.alienplayer.metadata.SongInfo;

import java.util.List;

public class PlayingInfoHolder extends BroadcastReceiver {
    public static final int REPEAT_ONE = 1;
    public static final int REPEAT_ALL = 2;
    public static final int REPEAT_SHUFFLE = 3;

    private static PlayingInfoHolder sSelf = null;

    private CurrentlistInfo mCurrentlistInfo = null;
    private List<SongInfo> mRecentsList = null;
    private Bitmap mPlaybarArtwork = null;
    private int mPlaybarArtworkSize;
    private int mRepeatMode = REPEAT_ALL;
    private boolean mIsPlaying = false;

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

        // init RepeatMode
        SharedPreferences sp = PreferencesHelper.getSharedPreferences(context);
        mRepeatMode = sp.getInt(PreferencesHelper.REPEAT_MODE, REPEAT_ALL);

        // init CurrentSong
        long songId = sp.getLong(PreferencesHelper.CURRENT_SONG_ID, -1);
        SongInfo currentSong = null;
        if (songId != -1) {
            SongInfo info = DatabaseHelper.getTrack(context, songId);
            if (info != null) {
                currentSong = info;
                mPlaybarArtwork = DatabaseHelper.getArtworkFormFile(context, currentSong.id,
                        currentSong.albumId, mPlaybarArtworkSize);
            }
        }

        // init recents list
        RecentsDBHelper.initRecents(context);
        mRecentsList = RecentsDBHelper.getRecentTracks(context);

        // init CurrentSongList
        long songListId = sp.getLong(PreferencesHelper.CURRENT_SONG_LIST_ID, -1);
        int songListType = sp.getInt(PreferencesHelper.CURRENT_SONG_LIST_TYPE, -1);
        List<SongInfo> currentSongList = null;
        if (songListId != -1 && songListType != -1) {
            if (songListType == CurrentlistInfo.TYPE_PLAYLIST) {
                currentSongList = PlaylistHelper.getPlaylistMembers(context, songListId);
            } else if (songListType == CurrentlistInfo.TYPE_RECENT) {
                currentSongList = mRecentsList;
            } else {
                currentSongList = DatabaseHelper.getTracks(context, songListType, songListId);
            }
        }
        mCurrentlistInfo = new CurrentlistInfo(songListId, songListType, currentSongList);

        if (!mCurrentlistInfo.setCurrentSong(currentSong)) {
            updateCurrentSongInfo(context, getCurrentSong());
        }

        IntentFilter intentFilter = new IntentFilter(PlayService.ACTION_START);
        intentFilter.addAction(PlayService.ACTION_PAUSE);
        intentFilter.addAction(PlayService.ACTION_STOP);
        intentFilter.addAction(PlayService.ACTION_EXIT);
        context.registerReceiver(this, intentFilter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(PlayService.ACTION_START)) {
            mIsPlaying = true;
        } else if (intent.getAction().equals(PlayService.ACTION_PAUSE)
                || intent.getAction().equals(PlayService.ACTION_STOP)) {
            mIsPlaying = false;
        } else if (intent.getAction().equals(PlayService.ACTION_EXIT)) {
            context.unregisterReceiver(this);
        }
    }

    public boolean isPlaying() {
        return mIsPlaying;
    }

    public int getRepeatMode() {
        return mRepeatMode;
    }

    public void setRepeatMode(Context context, int repeatMode) {
        mRepeatMode = repeatMode;
        PreferencesHelper.putIntValue(context, PreferencesHelper.REPEAT_MODE, repeatMode);
    }

    public List<SongInfo> getCurrentlist() {
        return mCurrentlistInfo.getSongList();
    }

    public void next(Context context) {
        if (mRepeatMode == PlayingInfoHolder.REPEAT_ALL) {
            mCurrentlistInfo.next();
        } else if (mRepeatMode == PlayingInfoHolder.REPEAT_SHUFFLE) {
            mCurrentlistInfo.shuffleNext();
        } else {
            return;
        }
        updateCurrentSongInfo(context, getCurrentSong());
    }

    public void prev(Context context) {
        if (mRepeatMode == PlayingInfoHolder.REPEAT_ALL) {
            mCurrentlistInfo.prev();
        } else if (mRepeatMode == PlayingInfoHolder.REPEAT_SHUFFLE) {
            mCurrentlistInfo.shufflePrev();
        } else {
            return;
        }
        updateCurrentSongInfo(context, getCurrentSong());
    }

    public SongInfo getCurrentSong() {
        return mCurrentlistInfo.getCurrentSong();
    }

    public Bitmap getPlaybarArtwork() {
        return mPlaybarArtwork;
    }

    public boolean addFileToRecents(Context context, Uri uri) {
        SongInfo info = DatabaseHelper.getSongInfoFromUri(context, uri);
        if (info != null) {
            PlayingInfoHolder.getInstance().setCurrentInfo(context, info, null);
            return true;
        }
        return false;
    }

    public void setCurrentInfo(Context context, SongInfo currentSong, CurrentlistInfo currentlistInfo) {
        // update Recents
        boolean update = isInRecents(currentSong.id);
        if (!update) {
            mRecentsList.add(currentSong);
        }
        RecentsDBHelper.addToRecents(context, currentSong.id, update);

        setCurrentlistInfo(context, currentlistInfo);
        if (mCurrentlistInfo.setCurrentSong(currentSong)) {
            updateCurrentSongInfo(context, currentSong);
        } else {
            updateCurrentSongInfo(context, getCurrentSong());
        }
    }

    public void setCurrentSong(Context context, SongInfo currentSong) {
        if (mCurrentlistInfo.setCurrentSong(currentSong)) {
            updateCurrentSongInfo(context, currentSong);
        } else {
            updateCurrentSongInfo(context, getCurrentSong());
        }
    }

    public void refresh(Context context) {
        updateCurrentSongInfo(context, getCurrentSong());
    }

    private void updateCurrentSongInfo(Context context, SongInfo currentSong) {
        if (currentSong != null) {
            PreferencesHelper.putLongValue(context, PreferencesHelper.CURRENT_SONG_ID,
                    currentSong.id);

            mPlaybarArtwork = DatabaseHelper.getArtworkFormFile(context, currentSong.id,
                    currentSong.albumId, mPlaybarArtworkSize);
        }
    }

    private void setCurrentlistInfo(Context context, CurrentlistInfo currentlistInfo) {
        if (currentlistInfo != null) {
            mCurrentlistInfo = currentlistInfo;
        } else {
            List<SongInfo> recentsList = RecentsDBHelper.getRecentTracks(context);
            mCurrentlistInfo = new CurrentlistInfo(CurrentlistInfo.ID_RECENT,
                    CurrentlistInfo.TYPE_RECENT, recentsList);
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
