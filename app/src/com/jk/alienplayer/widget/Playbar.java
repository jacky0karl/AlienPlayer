package com.jk.alienplayer.widget;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.impl.PlayingHelper;
import com.jk.alienplayer.impl.PlayingHelper.PlayStatus;
import com.jk.alienplayer.impl.PlayingHelper.PlayingInfo;
import com.jk.alienplayer.metadata.SongInfo;
import com.jk.alienplayer.ui.playing.PlayingActivity;

public class Playbar extends RelativeLayout {

    private PlayPauseButton mPlayBtn;
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
                mPlayBtn.transformToPause(true);
                mProgressBar.setMax(duration);
            } else if (action.equals(PlayService.ACTION_TRACK_CHANGE)) {
                syncTrackInfo();
            } else if (action.equals(PlayService.ACTION_PAUSE)) {
                mPlayBtn.transformToPlay(true);
            } else if (action.equals(PlayService.ACTION_STOP)) {
                mPlayBtn.transformToPlay(true);
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
        int padding = getResources().getDimensionPixelOffset(R.dimen.normal_padding);
        int height = getResources().getDimensionPixelOffset(R.dimen.playbar_height);
        LayoutInflater.from(getContext()).inflate(R.layout.playbar, this);
        setBackgroundColor(getResources().getColor(R.color.primary));
        setPadding(padding, padding, padding, padding);
        ViewGroup.LayoutParams lps = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, height);
        setLayoutParams(lps);

        mSongLabel = (TextView) findViewById(R.id.song);
        mArtistLabel = (TextView) findViewById(R.id.artist);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mPlayBtn = (PlayPauseButton) findViewById(R.id.playBtn);
        mPlayBtn.setOnClickListener(v -> {
            Intent intent = PlayService.getPlayingCommandIntent(getContext(),
                    PlayService.COMMAND_PLAY_PAUSE);
            getContext().startService(intent);
        });

        mNextBtn = (ImageButton) findViewById(R.id.next);
        mNextBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = PlayService.getPlayingCommandIntent(getContext(),
                        PlayService.COMMAND_NEXT);
                getContext().startService(intent);
            }
        });

        mPrevBtn = (ImageButton) findViewById(R.id.prev);
        mPrevBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = PlayService.getPlayingCommandIntent(getContext(),
                        PlayService.COMMAND_PREV);
                getContext().startService(intent);
            }
        });

        mArtwork = (ImageView) findViewById(R.id.artwork);
        mArtwork.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PlayingInfoHolder.getInstance().getCurrentSong() != null) {
                    ActivityOptions opts = ActivityOptions.makeCustomAnimation(getContext(), R.anim.slide_up, 0);
                    Intent intent = new Intent(getContext(), PlayingActivity.class);
                    getContext().startActivity(intent, opts.toBundle());
                }
            }
        });

        PlayService.registerReceiver(getContext(), mReceiver);
        syncView();
    }

    public void finish() {
        getContext().unregisterReceiver(mReceiver);
    }

    private void syncView() {
        syncTrackInfo();
        PlayingInfo info = PlayingHelper.getPlayingInfo();
        if (info.status == PlayStatus.Playing) {
            mPlayBtn.transformToPause(false);
            mProgressBar.setMax(info.duration);
            mProgressBar.setProgress(info.progress);
        } else if (info.status == PlayStatus.Paused) {
            mPlayBtn.transformToPlay(false);
            mProgressBar.setMax(info.duration);
            mProgressBar.setProgress(info.progress);
        }
    }

    public void syncTrackInfo() {
        SongInfo info = PlayingInfoHolder.getInstance().getCurrentSong();
        if (info != null) {
            setVisibility(View.VISIBLE);
            mSongLabel.setText(info.title);
            mArtistLabel.setText(info.artist);
            setArtwork();
        } else {
            setVisibility(View.GONE);
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
