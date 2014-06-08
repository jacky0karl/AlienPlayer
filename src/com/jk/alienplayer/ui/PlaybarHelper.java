package com.jk.alienplayer.ui;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.data.PreferencesHelper;
import com.jk.alienplayer.data.SongInfo;
import com.jk.alienplayer.impl.PlayingHelper;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

public class PlaybarHelper {
    private static int sPlaybarArtworkSize;
    private Activity mActivity;

    private ImageButton mPlayBtn;
    private ImageButton mNextBtn;
    private ImageButton mPrevBtn;
    private ImageView mArtwork;

    public PlaybarHelper(Activity activity) {
        mActivity = activity;
        init();
    }

    private void init() {
        sPlaybarArtworkSize = mActivity.getResources().getDimensionPixelOffset(
                R.dimen.playbar_artwork_size);

        mPlayBtn = (ImageButton) mActivity.findViewById(R.id.play);
        mPlayBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PlayingHelper.getInstance().playOrPause()) {
                    mPlayBtn.setImageResource(R.drawable.pause);
                } else {
                    mPlayBtn.setImageResource(R.drawable.play);
                }
            }
        });

        mNextBtn = (ImageButton) mActivity.findViewById(R.id.next);
        mPrevBtn = (ImageButton) mActivity.findViewById(R.id.prev);
        mArtwork = (ImageView) mActivity.findViewById(R.id.artwork);

        SharedPreferences sp = PreferencesHelper.getSharedPreferences(mActivity);
        long songId = sp.getLong(PreferencesHelper.CURRENT_SONG, -1);
        if (songId != -1) {
            SongInfo info = DatabaseHelper.getSong(mActivity, songId);
            if (info != null) {
                PlayingHelper.getInstance().setCurrentSong(mActivity, info);
                setArtwork(info);
            }
        }
    }

    public void setArtwork(SongInfo song) {
        Bitmap artwork = DatabaseHelper.getArtwork(mActivity, song.id, song.albumId,
                sPlaybarArtworkSize);
        if (artwork != null) {
            mArtwork.setImageBitmap(artwork);
        }
    }

    public void setPlayBtnImage(int resId) {
        mPlayBtn.setImageResource(resId);
    }
}
