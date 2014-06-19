package com.jk.alienplayer.ui.lib;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.data.PreferencesHelper;
import com.jk.alienplayer.data.SongInfo;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.impl.PlayingHelper;
import com.jk.alienplayer.impl.PlayingHelper.OnPlayStatusChangedListener;
import com.jk.alienplayer.ui.SongDetailActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Playbar extends FrameLayout {

    private RelativeLayout mContentView;
    private ImageButton mPlayBtn;
    private ImageButton mNextBtn;
    private ImageButton mPrevBtn;
    private ImageView mArtwork;
    private TextView mSongLabel;
    private TextView mArtistLabel;
    private ProgressBar mProgressBar;

    private OnPlayStatusChangedListener mOnPlayStatusChangedListener = new OnPlayStatusChangedListener() {
        @Override
        public void onStart(int duration) {
            mPlayBtn.setImageResource(R.drawable.pause);
            mProgressBar.setMax(duration);
        }

        @Override
        public void onPause() {
            mPlayBtn.setImageResource(R.drawable.play);
        }

        @Override
        public void onStop() {
            mPlayBtn.setImageResource(R.drawable.play);
            mProgressBar.setProgress(0);
        }

        @Override
        public void onProgressUpdate(int progress) {
            mProgressBar.setProgress(progress);
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

        mSongLabel = (TextView) mContentView.findViewById(R.id.song);
        mArtistLabel = (TextView) mContentView.findViewById(R.id.artist);
        mProgressBar = (ProgressBar) mContentView.findViewById(R.id.progressBar);
        mPlayBtn = (ImageButton) mContentView.findViewById(R.id.play);
        mPlayBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = PlayService.getPlayingCommandIntent(getContext(),
                        PlayService.COMMAND_PLAY_PAUSE);
                getContext().startService(intent);
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

        PlayingHelper.getInstance().registerOnPlayStatusChangedListener(
                mOnPlayStatusChangedListener);
    }

    public void finish() {
        PlayingHelper.getInstance().unregisterOnPlayStatusChangedListener(
                mOnPlayStatusChangedListener);
    }

    public void syncView() {
        SongInfo info = PlayingInfoHolder.getInstance().getCurrentSong();
        if (info != null) {
            mSongLabel.setText(info.title);
            mArtistLabel.setText(info.artist);
            setArtwork();
        }
    }

    private void setArtwork() {
        Bitmap artwork = PlayingInfoHolder.getInstance().getPlaybarArtwork();
        if (artwork != null) {
            mArtwork.setImageBitmap(artwork);
        } else {
            mArtwork.setImageResource(R.drawable.disk);
        }
    }
}
