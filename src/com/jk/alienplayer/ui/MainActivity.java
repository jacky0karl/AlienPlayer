package com.jk.alienplayer.ui;

import com.jk.alienplayer.R;
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
    private static final int FRAGMENT_SONGS = 2;
    private static final int FRAGMENT_PLAYLIST = 3;

    private PlaybarHelper mPlaybarHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentPagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);

        mPlaybarHelper = new PlaybarHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlaybarHelper.syncView();
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
            default:
                return new ArtistsFragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
            case FRAGMENT_ARTISTS:
                return "Artists";
            case FRAGMENT_ALBUMS:
                return "Albums";
            default:
                return "Artists";
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
