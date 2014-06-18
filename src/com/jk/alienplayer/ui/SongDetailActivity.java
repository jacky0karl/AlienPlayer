package com.jk.alienplayer.ui;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.data.SongInfo;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.impl.PlayingHelper;
import com.jk.alienplayer.impl.PlayingHelper.OnPlayStatusChangedListener;

import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;
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

    private OnPlayStatusChangedListener mOnPlayStatusChangedListener = new OnPlayStatusChangedListener() {
        @Override
        public void onStart(int duration) {
            mPlayBtn.setImageResource(R.drawable.pause);
            mSeekBar.setMax(duration);
        }

        @Override
        public void onPause() {
            mPlayBtn.setImageResource(R.drawable.play);
        }

        @Override
        public void onStop() {
            mPlayBtn.setImageResource(R.drawable.play);
            mSeekBar.setProgress(0);
        }

        @Override
        public void onProgressUpdate(int progress) {
            mSeekBar.setProgress(progress);
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

    @Override
    protected void onDestroy() {
        PlayingHelper.getInstance().unregisterOnPlayStatusChangedListener(
                mOnPlayStatusChangedListener);
        super.onDestroy();
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
                Intent intent = PlayService.getPlayingCommandIntent(SongDetailActivity.this,
                        PlayService.COMMAND_PLAY_PAUSE);
                startService(intent);
            }
        });

        mNextBtn = (ImageButton) findViewById(R.id.next);
        mPrevBtn = (ImageButton) findViewById(R.id.prev);

        PlayingHelper.getInstance().registerOnPlayStatusChangedListener(
                mOnPlayStatusChangedListener);
    }

    private void displayAudioEffect() {
        Intent intent = PlayService.getDisplayAudioEffectIntent(this);
        if (getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
            startActivityForResult(intent, 0);
        }
    }

    OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Intent intent = PlayService.getSeekIntent(SongDetailActivity.this,
                    seekBar.getProgress());
            startService(intent);
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
