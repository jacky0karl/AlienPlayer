package com.jk.alienplayer.ui;

import com.jk.alienplayer.R;
import com.jk.alienplayer.ui.fragment.AlbumsFragment;
import com.jk.alienplayer.ui.fragment.ArtistsFragment;
import com.jk.alienplayer.ui.fragment.PlaylistsFragment;
import com.jk.alienplayer.ui.fragment.RecentsFragment;
import com.jk.alienplayer.ui.fragment.TracksFragment;
import com.jk.alienplayer.ui.lib.ListMenu;
import com.jk.alienplayer.ui.lib.Playbar;
import com.viewpagerindicator.TabPageIndicator;

import android.content.Context;
import android.content.Intent;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupWindow;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends FragmentActivity {
    private static final int FRAGMENT_RECENTS = 0;
    private static final int FRAGMENT_PLAYLIST = 1;
    private static final int FRAGMENT_ARTISTS = 2;
    private static final int FRAGMENT_ALBUMS = 3;
    private static final int FRAGMENT_TRACKS = 4;
    private static final int FRAGMENT_COUNT = 5;

    private Playbar mPlaybar;
    private PopupWindow mPopupWindow;
    private AudioManager mAudioManager = null;
    private SeekBar mVolumeBar;
    private TabPageIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPlaybar = (Playbar) findViewById(R.id.playbar);

        FragmentPagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        mIndicator = (TabPageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(pager);
        setupVolumeBar();
    }

    private void setupVolumeBar() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mVolumeBar = (SeekBar) LayoutInflater.from(this).inflate(R.layout.volume, null);
        mVolumeBar.setMax(maxVolume);
        mVolumeBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }
        });

        int width = getResources().getDimensionPixelOffset(R.dimen.volumebar_width);
        mPopupWindow = new PopupWindow(mVolumeBar, width, LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
    }

    @Override
    protected void onDestroy() {
        mPlaybar.finish();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.action_volume) {
            showVolumeBar();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showVolumeBar() {
        int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mPopupWindow.showAtLocation(mIndicator, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);

        mVolumeBar.setProgress(volume);
        mPopupWindow.update();
    }

    class PagerAdapter extends FragmentPagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
            case FRAGMENT_RECENTS:
                return new RecentsFragment();
            case FRAGMENT_PLAYLIST:
                return new PlaylistsFragment();
            case FRAGMENT_ARTISTS:
                return new ArtistsFragment();
            case FRAGMENT_ALBUMS:
                return new AlbumsFragment();
            case FRAGMENT_TRACKS:
                return new TracksFragment();
            default:
                return new PlaylistsFragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
            case FRAGMENT_RECENTS:
                return getString(R.string.recents);
            case FRAGMENT_PLAYLIST:
                return getString(R.string.playlists);
            case FRAGMENT_ARTISTS:
                return getString(R.string.artists);
            case FRAGMENT_ALBUMS:
                return getString(R.string.albums);
            case FRAGMENT_TRACKS:
                return getString(R.string.tracks);
            default:
                return "unknown";
            }
        }

        @Override
        public int getCount() {
            return FRAGMENT_COUNT;
        }
    }
}
