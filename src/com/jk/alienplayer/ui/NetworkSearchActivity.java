package com.jk.alienplayer.ui;

import java.util.List;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.JsonHelper;
import com.jk.alienplayer.metadata.NetworkSearchResult;
import com.jk.alienplayer.network.HttpHelper;
import com.jk.alienplayer.network.HttpHelper.HttpResponseHandler;
import com.jk.alienplayer.ui.adapter.NetworkSearchResultsAdapter;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

public class NetworkSearchActivity extends Activity implements OnItemClickListener {

    private TextView mNoResult;
    private ProgressBar mLoading;
    private ListView mListView;
    private NetworkSearchResultsAdapter mAdapter;
    private HttpHelper mHttpHelper;
    private String mQueryKey;

    private OnQueryTextListener mQueryTextListener = new OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            if (TextUtils.isEmpty(query)) {
                return true;
            }

            mQueryKey = query;
            mLoading.setVisibility(View.VISIBLE);
            mHttpHelper.search(NetworkSearchResult.TYPE_ARTISTS, mQueryKey, mSearchArtistHandler);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return true;
        }
    };

    private HttpResponseHandler mSearchArtistHandler = new HttpResponseHandler() {
        @Override
        public void onSuccess(String response) {
            mHttpHelper.search(NetworkSearchResult.TYPE_ALBUMS, mQueryKey, mSearchAlbumHandler);
            final List<NetworkSearchResult> artists = JsonHelper.parseArtists(response);
            NetworkSearchActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.setResults(artists);
                }
            });
        }

        @Override
        public void onFail(int status, String response) {
            mLoading.setVisibility(View.GONE);
        }
    };

    private HttpResponseHandler mSearchAlbumHandler = new HttpResponseHandler() {
        @Override
        public void onSuccess(String response) {
            mHttpHelper.search(NetworkSearchResult.TYPE_TRACKS, mQueryKey, mSearchTrackHandler);
            final List<NetworkSearchResult> albums = JsonHelper.parseAlbums(response);
            NetworkSearchActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.addResults(albums);
                }
            });
        }

        @Override
        public void onFail(int status, String response) {
            mLoading.setVisibility(View.GONE);
        }
    };

    private HttpResponseHandler mSearchTrackHandler = new HttpResponseHandler() {
        @Override
        public void onSuccess(String response) {
            final List<NetworkSearchResult> tracks = JsonHelper.parseTracks(response);
            NetworkSearchActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.addResults(tracks);
                    if (mAdapter.getCount() == 0) {
                        mNoResult.setVisibility(View.VISIBLE);
                    } else {
                        mNoResult.setVisibility(View.GONE);
                    }
                    mLoading.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public void onFail(int status, String response) {
            mLoading.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_search);
        init();
    }

    private void init() {
        mNoResult = (TextView) findViewById(R.id.no_result);
        mLoading = (ProgressBar) findViewById(R.id.loading);
        mListView = (ListView) findViewById(R.id.list);
        mAdapter = new NetworkSearchResultsAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mHttpHelper = new HttpHelper(this);
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
        searchView.setSubmitButtonEnabled(true);
        searchView.setIconified(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NetworkSearchResult result = mAdapter.getItem(position);

    }
}
