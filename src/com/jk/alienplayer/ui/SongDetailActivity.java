package com.jk.alienplayer.ui;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.data.SongInfo;
import com.jk.alienplayer.impl.PlayingHelper;
import com.jk.alienplayer.impl.PlayingHelper.PlayingProgressBarListener;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.audiofx.AudioEffect;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SongDetailActivity extends FragmentActivity {

    private static final int FRAGMENT_ARTWORK = 0;
    private static final int FRAGMENT_LYRIC = 1;
    private static final int FRAGMENT_INFO = 2;

    private SongInfo mSongInfo;
    private SeekBar mSeekBar;
    private ImageButton mPlayBtn;
    private ImageButton mNextBtn;
    private ImageButton mPrevBtn;
    private Handler mHandler = new Handler();

    private PlayingProgressBarListener mListener = new PlayingProgressBarListener() {
        @Override
        public void onProgressStart(int duration) {
            mSeekBar.setMax(duration);
            startSeekBarUpdate();
        }
    };

    private OnCompletionListener mCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mPlayBtn.setImageResource(R.drawable.play);
            mHandler.removeCallbacks(mUpdateTask);
            mSeekBar.setProgress(mSeekBar.getMax());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_detail);
        init();

        FragmentPagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.song_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.audioEffect) {
            displayAudioEffect();
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        mSongInfo = PlayingInfoHolder.getInstance().getCurrentSong();
        setTitle(mSongInfo.title);

        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);

        mPlayBtn = (ImageButton) findViewById(R.id.play);
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

        mNextBtn = (ImageButton) findViewById(R.id.next);
        mPrevBtn = (ImageButton) findViewById(R.id.prev);
    }

    public void syncView() {
        PlayingHelper.getInstance().setOnCompletionListener(mCompletionListener);
        if (PlayingHelper.getInstance().isPlaying()) {
            mPlayBtn.setImageResource(R.drawable.pause);
            mSeekBar.setMax(PlayingHelper.getInstance().getDuration());
            startSeekBarUpdate();
        } else {
            mPlayBtn.setImageResource(R.drawable.play);
        }
    }

    private void displayAudioEffect() {
        Intent intent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
        intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, PlayingHelper.getInstance()
                .getAudioSessionId());
        intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());

        if (getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
            startActivityForResult(intent, 0);
        }
    }

    private void startSeekBarUpdate() {
        mHandler.removeCallbacks(mUpdateTask);
        mHandler.post(mUpdateTask);
    }

    Runnable mUpdateTask = new Runnable() {
        @Override
        public void run() {
            int progress = PlayingHelper.getInstance().getCurrentPosition();
            mSeekBar.setProgress(progress);
            mHandler.postDelayed(mUpdateTask, 500);
        }
    };

    OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            PlayingHelper.getInstance().seekTo(seekBar.getProgress());
        }
    };

    class PagerAdapter extends FragmentPagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
            case FRAGMENT_ARTWORK:
                return new ArtworkFragment();
            case FRAGMENT_LYRIC:
                return new ArtworkFragment();
            case FRAGMENT_INFO:
                return new ArtworkFragment();
            default:
                return new ArtworkFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
