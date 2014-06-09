package com.jk.alienplayer.ui;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.data.PreferencesHelper;
import com.jk.alienplayer.data.SongInfo;
import com.jk.alienplayer.impl.PlayingHelper;
import com.jk.alienplayer.impl.PlayingHelper.PlayingProgressBarListener;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
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
    private Handler mHandler = new Handler();

    private PlayingProgressBarListener mListener = new PlayingProgressBarListener() {
        @Override
        public void onProgressStart(int duration) {
            mProgressBar.setMax(duration);
            startProgressUpdate();
        }
    };

    Runnable mUpdateTask = new Runnable() {
        @Override
        public void run() {
            int progress = PlayingHelper.getInstance().getCurrentPosition();
            mProgressBar.setProgress(progress);
            mHandler.postDelayed(mUpdateTask, 500);
        }
    };

    private OnCompletionListener mCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mPlayBtn.setImageResource(R.drawable.play);
            mHandler.removeCallbacks(mUpdateTask);
            mProgressBar.setProgress(mProgressBar.getMax());
        }
    };

    public PlaybarHelper(Activity activity) {
        mActivity = activity;
        init();
    }

    private void init() {

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
    }

    public void syncView() {
        PlayingHelper.getInstance().setOnCompletionListener(mCompletionListener);
        SharedPreferences sp = PreferencesHelper.getSharedPreferences(mActivity);
        long songId = sp.getLong(PreferencesHelper.CURRENT_SONG, -1);
        if (songId != -1) {
            SongInfo info = DatabaseHelper.getSong(mActivity, songId);
            if (info != null) {
                PlayingInfoHolder.getInstance().setCurrentSong(mActivity, info);
                setArtwork(info);
            }
        }

        if (PlayingHelper.getInstance().isPlaying()) {
            mPlayBtn.setImageResource(R.drawable.pause);
            mProgressBar.setMax(PlayingHelper.getInstance().getDuration());
            mHandler.removeCallbacks(mUpdateTask);
            startProgressUpdate();
        } else {
            mPlayBtn.setImageResource(R.drawable.play);
        }
    }

    public void setArtwork(SongInfo song) {
        Bitmap artwork = PlayingInfoHolder.getInstance().getPlaybarArtwork();
        if (artwork != null) {
            mArtwork.setImageBitmap(artwork);
        } else {
            mArtwork.setImageResource(R.drawable.ic_launcher);
        }
    }

    public void setPlayBtnImage(int resId) {
        mPlayBtn.setImageResource(resId);
    }

    public PlayingProgressBarListener getListener() {
        return mListener;
    }

    public void startProgressUpdate() {
        mHandler.post(mUpdateTask);
    }

}
