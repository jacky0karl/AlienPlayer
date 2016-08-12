package com.jk.alienplayer.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.metadata.CurrentlistInfo;
import com.jk.alienplayer.metadata.SearchResult;
import com.jk.alienplayer.metadata.SongInfo;
import com.jk.alienplayer.ui.BaseActivity;
import com.jk.alienplayer.ui.adapter.SearchResultsAdapter;
import com.jk.alienplayer.ui.artistdetail.SongsActivity;
import com.jk.alienplayer.widget.TrackOperationHelper;
import com.jk.alienplayer.widget.TrackOperationHelper.OnDeleteTrackListener;

import java.util.List;

public class SearchActivity extends BaseActivity implements OnItemClickListener {
    public static final String QUERY = "query";
    public static final int MEMU_ADD_TO_PLAYLIST = 0;
    public static final int MEMU_DELETE = 1;

    private ListView mListView;
    private SearchResultsAdapter mAdapter;
    private List<SearchResult> mResults;
    private SearchResult mCurrResult = null;

    private OnQueryTextListener mQueryTextListener = new OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            mResults = DatabaseHelper.search(SearchActivity.this, newText);
            mAdapter.setResults(mResults);
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        init();
    }

    private void init() {
        mListView = (ListView) findViewById(R.id.list);
        mAdapter = new SearchResultsAdapter(this, this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        initSearchBar(menu.findItem(R.id.search));
        return true;
    }

    private void initSearchBar(MenuItem item) {
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(mQueryTextListener);
        String query = getIntent().getStringExtra(QUERY);
        if (!TextUtils.isEmpty(query)) {
            searchView.setQuery(query, false);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mCurrResult = mAdapter.getItem(position);
        if (mCurrResult != null) {
            if (view.getId() == R.id.action) {
                showPopupMenu(view);
            } else {
                handleItemClick();
            }
        }
    }

    private void showPopupMenu(View v) {
        PopupMenu menu = new PopupMenu(this, v);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case MEMU_ADD_TO_PLAYLIST:
                        TrackOperationHelper.addToPlaylist(SearchActivity.this, mCurrResult.data.getId());
                        break;
                    case MEMU_DELETE:
                        deleteTrack();
                        break;
                }
                return false;
            }
        });

        menu.getMenu().add(Menu.NONE, MEMU_ADD_TO_PLAYLIST, Menu.NONE, R.string.add_to_playlist);
        menu.getMenu().add(Menu.NONE, MEMU_DELETE, Menu.NONE, R.string.delete);
        menu.show();
    }

    private void handleItemClick() {
        if (mCurrResult.type == SearchResult.TYPE_TRACKS) {
            PlayingInfoHolder.getInstance().setCurrentInfo(this, (SongInfo) mCurrResult.data, null);
            Intent intent = PlayService.getPlayingCommandIntent(SearchActivity.this,
                    PlayService.COMMAND_PLAY);
            startService(intent);
        }

        Intent intent = new Intent(this, SongsActivity.class);
        switch (mCurrResult.type) {
        case SearchResult.TYPE_ARTISTS:
            intent.putExtra(SongsActivity.KEY_TYPE, CurrentlistInfo.TYPE_ARTIST);
            break;
        case SearchResult.TYPE_ALBUMS:
            intent.putExtra(SongsActivity.KEY_TYPE, CurrentlistInfo.TYPE_ALBUM);
            break;
        default:
            return;
        }

        intent.putExtra(SongsActivity.KEY, mCurrResult.data.getId());
        intent.putExtra(SongsActivity.LABEL, mCurrResult.data.getDisplayName());
        startActivity(intent);
    }

    private void deleteTrack() {
        final SearchResult deleteResult = mCurrResult;
        OnDeleteTrackListener listener = new OnDeleteTrackListener() {
            @Override
            public void onComplete() {
                mResults.remove(deleteResult);
                mAdapter.setResults(mResults);
            }
        };
        TrackOperationHelper.deleteTrack(this, (SongInfo) mCurrResult.data, listener);
    }
}
