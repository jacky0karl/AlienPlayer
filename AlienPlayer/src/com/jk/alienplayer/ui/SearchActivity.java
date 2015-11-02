package com.jk.alienplayer.ui;

import java.util.List;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.metadata.CurrentlistInfo;
import com.jk.alienplayer.metadata.SearchResult;
import com.jk.alienplayer.metadata.SongInfo;
import com.jk.alienplayer.ui.adapter.SearchResultsAdapter;
import com.jk.alienplayer.ui.lib.ListMenu;
import com.jk.alienplayer.ui.lib.TrackOperationHelper;
import com.jk.alienplayer.ui.lib.ListMenu.OnMenuItemClickListener;
import com.jk.alienplayer.ui.lib.TrackOperationHelper.OnDeleteTrackListener;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

public class SearchActivity extends BaseActivity implements OnItemClickListener {
    public static final String QUERY = "query";

    private ListView mListView;
    private SearchResultsAdapter mAdapter;
    private List<SearchResult> mResults;
    private SearchResult mCurrResult = null;
    private ListMenu mListMenu;
    private PopupWindow mPopupWindow;

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
        setupPopupWindow();
    }

    private void setupPopupWindow() {
        mListMenu = new ListMenu(this);
        mListMenu.setMenuItemClickListener(mOnMenuItemClickListener);
        mListMenu.addMenu(ListMenu.MEMU_ADD_TO_PLAYLIST, R.string.add_to_playlist);
        mListMenu.addMenu(ListMenu.MEMU_DELETE, R.string.delete);
        mPopupWindow = new PopupWindow(mListMenu, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
    }

    @Override
    public void onDestroy() {
        mPopupWindow.dismiss();
        super.onDestroy();
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
                mPopupWindow.showAsDropDown(view);
            } else {
                handleItemClick();
            }
        }
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

    private OnMenuItemClickListener mOnMenuItemClickListener = new OnMenuItemClickListener() {
        @Override
        public void onClick(int menuId) {
            mPopupWindow.dismiss();
            if (ListMenu.MEMU_DELETE == menuId) {
                deleteTrack();
            } else if (ListMenu.MEMU_ADD_TO_PLAYLIST == menuId) {
                TrackOperationHelper.addToPlaylist(SearchActivity.this, mCurrResult.data.getId());
            }
        }
    };

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
