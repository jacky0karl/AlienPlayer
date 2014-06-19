package com.jk.alienplayer.data;

import com.jk.alienplayer.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

public class PlayingInfoHolder {
    private static PlayingInfoHolder sSelf = null;

    private SongInfo mCurrentSong = null;
    private Bitmap mPlaybarArtwork = null;

    public static synchronized PlayingInfoHolder getInstance() {
        if (sSelf == null) {
            sSelf = new PlayingInfoHolder();
        }
        return sSelf;
    }

    private PlayingInfoHolder() {
    }

    public void initCurrentSong(Context context) {
        SharedPreferences sp = PreferencesHelper.getSharedPreferences(context);
        long songId = sp.getLong(PreferencesHelper.CURRENT_SONG, -1);
        if (songId != -1) {
            SongInfo info = DatabaseHelper.getSong(context, songId);
            if (info != null) {
                PlayingInfoHolder.getInstance().setCurrentSong(context, info);
            }
        }
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

            int playbarArtworkSize = context.getResources().getDimensionPixelOffset(
                    R.dimen.playbar_artwork_size);
            mPlaybarArtwork = DatabaseHelper.getArtwork(context, mCurrentSong.id,
                    mCurrentSong.albumId, playbarArtworkSize);
        }
    }
}
