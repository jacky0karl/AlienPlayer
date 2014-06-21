package com.jk.alienplayer.ui.lib;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.impl.PlayingHelper;
import com.jk.alienplayer.impl.PlayingHelper.PlayStatus;
import com.jk.alienplayer.impl.PlayingHelper.PlayingInfo;
import com.jk.alienplayer.metadata.SongInfo;
import com.jk.alienplayer.ui.SongDetailActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(PlayService.ACTION_START)) {
                int duration = intent.getIntExtra(PlayService.TOTAL_DURATION, 0);
                mPlayBtn.setImageResource(R.drawable.pause);
                mProgressBar.setMax(duration);
            } else if (action.equals(PlayService.ACTION_TRACK_CHANGE)) {
                syncTrackInfo();
            } else if (action.equals(PlayService.ACTION_PAUSE)) {
                mPlayBtn.setImageResource(R.drawable.play);
            } else if (action.equals(PlayService.ACTION_STOP)) {
                mPlayBtn.setImageResource(R.drawable.play);
                mProgressBar.setProgress(0);
            } else if (action.equals(PlayService.ACTION_PROGRESS_UPDATE)) {
                int progress = intent.getIntExtra(PlayService.CURRENT_DURATION, 0);
                mProgressBar.setProgress(progress);
            }
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

        PlayService.registerReceiver(getContext(), mReceiver);
        syncView();
    }

    public void finish() {
        getContext().unregisterReceiver(mReceiver);
    }

    public void syncView() {
        syncTrackInfo();
        PlayingInfo info = PlayingHelper.getPlayingInfo();
        if (info.status == PlayStatus.Playing) {
            mPlayBtn.setImageResource(R.drawable.pause);
            mProgressBar.setMax(info.duration);
            mProgressBar.setProgress(info.progress);
        } else if (info.status == PlayStatus.Paused) {
            mPlayBtn.setImageResource(R.drawable.play);
            mProgressBar.setMax(info.duration);
            mProgressBar.setProgress(info.progress);
        }
    }

    public void syncTrackInfo() {
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
