package com.jk.alienplayer.ui.lib;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.data.PreferencesHelper;
import com.jk.alienplayer.data.SongInfo;
import com.jk.alienplayer.impl.PlayingHelper;
import com.jk.alienplayer.impl.PlayingHelper.PlayingProgressBarListener;
import com.jk.alienplayer.ui.SongDetailActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class Playbar extends FrameLayout {

    private RelativeLayout mContentView;
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

    public Playbar(Context context) {
        super(context);
        init();
    }

    public Playbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Playbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mContentView = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.playbar,
                null);
        addView(mContentView);

        mProgressBar = (ProgressBar) mContentView.findViewById(R.id.progressBar);
        mPlayBtn = (ImageButton) mContentView.findViewById(R.id.play);
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

        mNextBtn = (ImageButton) mContentView.findViewById(R.id.next);
        mPrevBtn = (ImageButton) mContentView.findViewById(R.id.prev);
        mArtwork = (ImageView) mContentView.findViewById(R.id.artwork);
        mArtwork.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PlayingInfoHolder.getInstance().getCurrentSong() != null) {
                    Intent intent = new Intent(getContext(), SongDetailActivity.class);
                    getContext().startActivity(intent);
                }
            }
        });
    }

    public void syncView() {
        PlayingHelper.getInstance().setOnCompletionListener(mCompletionListener);
        SharedPreferences sp = PreferencesHelper.getSharedPreferences(getContext());
        long songId = sp.getLong(PreferencesHelper.CURRENT_SONG, -1);
        if (songId != -1) {
            SongInfo info = DatabaseHelper.getSong(getContext(), songId);
            if (info != null) {
                PlayingInfoHolder.getInstance().setCurrentSong(getContext(), info);
                setArtwork(info);
            }
        }

        if (PlayingHelper.getInstance().isPlaying()) {
            mPlayBtn.setImageResource(R.drawable.pause);
            mProgressBar.setMax(PlayingHelper.getInstance().getDuration());
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
        mHandler.removeCallbacks(mUpdateTask);
        mHandler.post(mUpdateTask);
    }

}
