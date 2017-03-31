package com.jk.alienplayer.ui.playing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.impl.PlayingHelper;
import com.jk.alienplayer.impl.PlayingHelper.PlayStatus;
import com.jk.alienplayer.impl.PlayingHelper.PlayingInfo;
import com.jk.alienplayer.metadata.CurrentlistInfo;
import com.jk.alienplayer.metadata.SongInfo;
import com.jk.alienplayer.ui.BaseActivity;
import com.jk.alienplayer.ui.artistdetail.ArtistDetailActivity;
import com.jk.alienplayer.ui.artistdetail.SongsActivity;
import com.jk.alienplayer.utils.PlayingTimeUtils;
import com.jk.alienplayer.widget.PlayPauseButton;
import com.jk.alienplayer.widget.VolumeBarWindow;

public class PlayingActivity extends BaseActivity {
    private static final int FRAGMENT_ARTWORK = 0;
    private static final int FRAGMENT_LYRIC = 1;
    private static final int FRAGMENT_CURR_LIST = 2;
    private static final int FRAGMENT_COUNT = 3;

    private SeekBar mSeekBar;
    private PlayPauseButton mPlayBtn;
    private ImageButton mNextBtn;
    private ImageButton mPrevBtn;
    private TextView mProgress;
    private TextView mDuration;
    private TextView mSeekTime;
    private PopupWindow mPopupWindow;
    private VolumeBarWindow mVolumeBar;
    private ViewPager mContent;

    private boolean mIsTrackingTouch = false;
    private int mTimetagOffset;
    private int mSeekBarW = 0;

    private OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                float ratio = (float) progress / (float) seekBar.getMax();
                mSeekTime.setText(PlayingTimeUtils.toDisplayTime(progress));
                mPopupWindow.update(seekBar, (int) (mSeekBarW * ratio), -mTimetagOffset, -1, -1);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mIsTrackingTouch = true;
            mSeekBarW = mSeekBar.getWidth();
            mPopupWindow.showAsDropDown(seekBar);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mIsTrackingTouch = false;
            mPopupWindow.dismiss();

            Intent intent = PlayService.getSeekIntent(PlayingActivity.this, seekBar.getProgress());
            startService(intent);
        }
    };
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(PlayService.ACTION_START)) {
                int duration = intent.getIntExtra(PlayService.TOTAL_DURATION, 0);
                mPlayBtn.transformToPause(true);
                mSeekBar.setMax(duration);
            } else if (action.equals(PlayService.ACTION_TRACK_CHANGE)) {
                String song = intent.getStringExtra(PlayService.SONG_NAME);
                String artist = intent.getStringExtra(PlayService.ARTIST_NAME);
                setTitle(song);
                getSupportActionBar().setSubtitle(artist);
            } else if (action.equals(PlayService.ACTION_PAUSE)) {
                mPlayBtn.transformToPlay(true);
            } else if (action.equals(PlayService.ACTION_STOP)) {
                mPlayBtn.transformToPlay(true);
                mSeekBar.setProgress(0);
            } else if (action.equals(PlayService.ACTION_PROGRESS_UPDATE)) {
                int progress = intent.getIntExtra(PlayService.CURRENT_DURATION, 0);
                if (!mIsTrackingTouch) {
                    mSeekBar.setProgress(progress);
                }
                mProgress.setText(PlayingTimeUtils.toDisplayTime(progress));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);
        init();

        FragmentPagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.playing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_info) {
            gotoSongsInfo();
        } else if (item.getItemId() == R.id.action_volume) {
            mVolumeBar.show(mContent, Gravity.CENTER);
        } else if (item.getItemId() == R.id.audio_effect) {
            displayAudioEffect();
        } else if (item.getItemId() == R.id.goto_artist) {
            gotoArtist();
        } else if (item.getItemId() == R.id.goto_album) {
            gotoAlbum();
        }
        return true;
    }

    private void init() {
        SongInfo song = PlayingInfoHolder.getInstance().getCurrentSong();
        setTitle(song.title);
        getSupportActionBar().setSubtitle(song.artist);
        mProgress = (TextView) findViewById(R.id.progress);
        mDuration = (TextView) findViewById(R.id.duration);
        mProgress.setText(PlayingTimeUtils.toDisplayTime(0));
        mDuration.setText(PlayingTimeUtils.toDisplayTime(song.duration));

        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);

        mPlayBtn = (PlayPauseButton) findViewById(R.id.playBtn);
        mPlayBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = PlayService.getPlayingCommandIntent(PlayingActivity.this,
                        PlayService.COMMAND_PLAY_PAUSE);
                startService(intent);
            }
        });

        mNextBtn = (ImageButton) findViewById(R.id.next);
        mNextBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = PlayService.getPlayingCommandIntent(PlayingActivity.this,
                        PlayService.COMMAND_NEXT);
                PlayingActivity.this.startService(intent);
            }
        });

        mPrevBtn = (ImageButton) findViewById(R.id.prev);
        mPrevBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = PlayService.getPlayingCommandIntent(PlayingActivity.this,
                        PlayService.COMMAND_PREV);
                PlayingActivity.this.startService(intent);
            }
        });

        PlayService.registerReceiver(this, mReceiver);
        setupPopupWindow();

        mContent = (ViewPager) findViewById(R.id.pager);
        mVolumeBar = VolumeBarWindow.createVolumeBarWindow(this);
        syncView();
    }

    private void setupPopupWindow() {
        mTimetagOffset = getResources().getDimensionPixelOffset(R.dimen.timetag_offset);
        mSeekTime = new TextView(this);
        int padding = getResources().getDimensionPixelOffset(R.dimen.normal_padding);
        mSeekTime.setPadding(padding, padding, padding, padding);
        mSeekTime.setBackgroundColor(getResources().getColor(R.color.half_transparent));
        mSeekTime.setTextColor(getResources().getColor(android.R.color.white));
        mPopupWindow = new PopupWindow(mSeekTime, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, false);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_down);
    }

    @Override
    protected void onDestroy() {
        mVolumeBar.dismiss();
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    public void syncView() {
        PlayingInfo info = PlayingHelper.getPlayingInfo();
        if (info.status == PlayStatus.Playing) {
            mPlayBtn.transformToPause(false);
            mSeekBar.setMax(info.duration);
            mSeekBar.setProgress(info.progress);
        } else if (info.status == PlayStatus.Paused) {
            mPlayBtn.transformToPlay(false);
            mSeekBar.setMax(info.duration);
            mSeekBar.setProgress(info.progress);
        }
    }

    private void displayAudioEffect() {
        Intent intent = PlayService.getDisplayAudioEffectIntent(this);
        if (getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
            startActivityForResult(intent, 0);
        }
    }

    private void gotoSongsInfo() {
        SongInfo song = PlayingInfoHolder.getInstance().getCurrentSong();
        Intent intent = new Intent(this, TrackInfoActivity.class);
        intent.putExtra(TrackInfoActivity.EXTRA_MODE, TrackInfoActivity.MODE_SINGLE);
        intent.putExtra(TrackInfoActivity.EXTRA_ID, song.id);
        startActivity(intent);
    }

    private void gotoArtist() {
        SongInfo song = PlayingInfoHolder.getInstance().getCurrentSong();
        Intent intent = new Intent(this, ArtistDetailActivity.class);
        intent.putExtra(ArtistDetailActivity.ARTIST_ID, song.artistId);
        intent.putExtra(ArtistDetailActivity.ARTIST_NAME, song.artist);
        startActivity(intent);
    }

    private void gotoAlbum() {
        SongInfo song = PlayingInfoHolder.getInstance().getCurrentSong();
        Intent intent = new Intent(this, SongsActivity.class);
        intent.putExtra(SongsActivity.KEY_TYPE, CurrentlistInfo.TYPE_ALBUM);
        intent.putExtra(SongsActivity.KEY, song.albumId);
        intent.putExtra(SongsActivity.LABEL, song.album);
        startActivity(intent);
    }

    class PagerAdapter extends FragmentPagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case FRAGMENT_ARTWORK:
                    return new ArtworkFragment();
                case FRAGMENT_CURR_LIST:
                    return new CurrentListFragment();
                case FRAGMENT_LYRIC:
                    return new LyricFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return FRAGMENT_COUNT;
        }
    }
}
