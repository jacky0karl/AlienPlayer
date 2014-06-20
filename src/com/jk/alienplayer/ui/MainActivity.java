package com.jk.alienplayer.ui;

import com.jk.alienplayer.R;
import com.jk.alienplayer.ui.lib.Playbar;
import com.viewpagerindicator.TabPageIndicator;

import android.content.Intent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {
    private static final int FRAGMENT_RECENTS = 0;
    private static final int FRAGMENT_ARTISTS = 0;
    private static final int FRAGMENT_ALBUMS = 1;
    private static final int FRAGMENT_TRACKS = 2;
    private static final int FRAGMENT_PLAYLIST = 3;

    private Playbar mPlaybar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPlaybar = (Playbar) findViewById(R.id.playbar);

        FragmentPagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);
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
        }
        return super.onOptionsItemSelected(item);
    }

    public Playbar getPlaybarHelper() {
        return mPlaybar;
    }

    class PagerAdapter extends FragmentPagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
            case FRAGMENT_ARTISTS:
                return new ArtistsFragment();
            case FRAGMENT_ALBUMS:
                return new AlbumsFragment();
            case FRAGMENT_TRACKS:
                return new TracksFragment();
            default:
                return new ArtistsFragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
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
            return 3;
        }
    }
}
