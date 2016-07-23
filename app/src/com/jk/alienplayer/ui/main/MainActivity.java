package com.jk.alienplayer.ui.main;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.network.FileDownloadingHelper;
import com.jk.alienplayer.ui.AboutActivity;
import com.jk.alienplayer.ui.BaseActivity;
import com.jk.alienplayer.ui.network.NetworkSearchActivity;
import com.jk.alienplayer.widget.Playbar;
import com.jk.alienplayer.widget.VolumeBarWindow;
import com.jk.alienplayer.utils.UncaughtExceptionLoger;
import com.tbruyelle.rxpermissions.RxPermissions;

public class MainActivity extends BaseActivity {
    //private static final int FRAGMENT_ALBUM_ARTISTS = 0;
    private static final int FRAGMENT_PLAYLIST = 0;
    private static final int FRAGMENT_ARTISTS = 1;
    private static final int FRAGMENT_ALBUMS = 2;
    //private static final int FRAGMENT_RECENTS = 4;
    private static final int FRAGMENT_COUNT = 3;

    private Playbar mPlaybar;
    private VolumeBarWindow mVolumeBar;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxPermissions.getInstance(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        init();
                    } else {
                        finish();
                    }
                });
    }

    private void init() {
        FileDownloadingHelper.getInstance().init(this);
        UncaughtExceptionLoger.getInstance().init();
        PlayingInfoHolder.getInstance().init(this);
        Intent intent = new Intent(this, PlayService.class);
        startService(intent);

        setContentView(R.layout.activity_main);
        mPlaybar = (Playbar) findViewById(R.id.playbar);
        mVolumeBar = VolumeBarWindow.createVolumeBarWindow(this);

        FragmentPagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(pager);
        pager.setCurrentItem(FRAGMENT_ARTISTS);
    }

    @Override
    protected void onDestroy() {
        if (mVolumeBar != null) {
            mVolumeBar.dismiss();
        }
        if (mPlaybar != null) {
            mPlaybar.finish();
        }

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
        } else if (item.getItemId() == R.id.discover) {
            Intent intent = new Intent(this, NetworkSearchActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.action_volume) {
            mVolumeBar.show(mTabLayout, Gravity.CENTER);
        } else if (item.getItemId() == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.action_exit) {
            exit();
        }
        return super.onOptionsItemSelected(item);
    }

    private void exit() {
        Intent intent = new Intent(this, PlayService.class);
        stopService(intent);
        finish();
    }

    class PagerAdapter extends FragmentPagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                //case FRAGMENT_RECENTS:
                //    return new RecentsFragment();
                case FRAGMENT_PLAYLIST:
                    return new PlaylistsFragment();
                case FRAGMENT_ARTISTS:
                    return new ArtistsFragment();
                //case FRAGMENT_ALBUM_ARTISTS:
                //    return ArtistsFragment.newInstance(ArtistsFragment.TYPE_ALBUM_ARTISTS);
                case FRAGMENT_ALBUMS:
                    return new AlbumsFragment();
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                //case FRAGMENT_RECENTS:
                //    return getString(R.string.recents);
                case FRAGMENT_PLAYLIST:
                    return getString(R.string.playlists);
                case FRAGMENT_ARTISTS:
                    return getString(R.string.artists);
                //case FRAGMENT_ALBUM_ARTISTS:
                //    return getString(R.string.album_artists);
                case FRAGMENT_ALBUMS:
                    return getString(R.string.albums);
                default:
                    return "";
            }
        }

        @Override
        public int getCount() {
            return FRAGMENT_COUNT;
        }
    }
}
