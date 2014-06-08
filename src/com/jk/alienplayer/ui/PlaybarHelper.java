package com.jk.alienplayer.ui;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.data.PreferencesHelper;
import com.jk.alienplayer.data.SongInfo;
import com.jk.alienplayer.impl.PlayingHelper;
import com.jk.alienplayer.impl.PlayingHelper.PlayingProgressBarListener;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class PlaybarHelper {
    private static int sPlaybarArtworkSize;
    private Activity mActivity;

    private ImageButton mPlayBtn;
    private ImageButton mNextBtn;
    private ImageButton mPrevBtn;
    private ImageView mArtwork;
    private ProgressBar mProgressBar;
    private PlayingProgressBarListener mListener = null;
    

    public PlaybarHelper(Activity activity, PlayingProgressBarListener listener) {
        mActivity = activity;
        mListener = listener;
        init();
    }

    private void init() {
        sPlaybarArtworkSize = mActivity.getResources().getDimensionPixelOffset(
                R.dimen.playbar_artwork_size);
        mProgressBar = (ProgressBar) mActivity.findViewById(R.id.progressBar);

        mPlayBtn = (ImageButton) mActivity.findViewById(R.id.play);
        mPlayBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PlayingHelper.getInstance().playOrPause(mListener)) {
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

    public void setProgress(int progress) {
        mProgressBar.setProgress(progress);
    }

    public void setMaxProgress(int max) {
        mProgressBar.setMax(max);
    }

    
}
