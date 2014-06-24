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

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView.OnQueryTextListener;

public class SearchActivity extends Activity implements OnItemClickListener {
    private ListView mListView;
    private SearchResultsAdapter mAdapter;

    private OnQueryTextListener mQueryTextListener = new OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            List<SearchResult> results = DatabaseHelper.search(SearchActivity.this, newText);
            mAdapter.setResults(results);
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
        mAdapter = new SearchResultsAdapter(this);
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
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setSearchableInfo(info);
        searchView.setOnQueryTextListener(mQueryTextListener);
        searchView.setIconified(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SearchResult result = mAdapter.getItem(position);
        if (result.type == SearchResult.TYPE_TRACKS) {
            PlayingInfoHolder.getInstance().setCurrentInfo(this, (SongInfo) result.data, null);
            Intent intent = PlayService.getPlayingCommandIntent(SearchActivity.this,
                    PlayService.COMMAND_PLAY);
            startService(intent);
        }

        Intent intent = new Intent(this, SongsActivity.class);
        switch (result.type) {
        case SearchResult.TYPE_ARTISTS:
            intent.putExtra(SongsActivity.KEY_TYPE, CurrentlistInfo.TYPE_ARTIST);
            break;
        case SearchResult.TYPE_ALBUMS:
            intent.putExtra(SongsActivity.KEY_TYPE, CurrentlistInfo.TYPE_ALBUM);
            break;
        default:
            return;
        }

        intent.putExtra(SongsActivity.KEY, result.data.getId());
        intent.putExtra(SongsActivity.LABEL, result.data.getDisplayName());
        startActivity(intent);
    }
}
