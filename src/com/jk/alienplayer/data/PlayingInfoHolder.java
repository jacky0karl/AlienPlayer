package com.jk.alienplayer.data;

import com.jk.alienplayer.R;

import android.content.Context;
import android.graphics.Bitmap;

public class PlayingInfoHolder {
    private static PlayingInfoHolder sSelf = null;

    private TrackInfo mCurrentSong = null;
    private Bitmap mPlaybarArtwork = null;

    public static synchronized PlayingInfoHolder getInstance() {
        if (sSelf == null) {
            sSelf = new PlayingInfoHolder();
        }
        return sSelf;
    }

    private PlayingInfoHolder() {
    }

    public TrackInfo getCurrentSong() {
        return mCurrentSong;
    }

    public Bitmap getPlaybarArtwork() {
        return mPlaybarArtwork;
    }

    public void setCurrentSong(Context context, TrackInfo currentSong) {
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
