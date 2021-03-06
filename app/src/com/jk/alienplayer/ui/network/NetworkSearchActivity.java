package com.jk.alienplayer.ui.network;

import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.JsonHelper;
import com.jk.alienplayer.metadata.NetworkAlbumInfo;
import com.jk.alienplayer.metadata.NetworkSearchResult;
import com.jk.alienplayer.metadata.NetworkTrackInfo;
import com.jk.alienplayer.network.HttpHelper;
import com.jk.alienplayer.network.HttpHelper.HttpResponseHandler;
import com.jk.alienplayer.ui.BaseActivity;
import com.jk.alienplayer.ui.adapter.NetworkSearchResultsAdapter;
import com.jk.alienplayer.ui.playing.TrackInfoActivity;
import com.jk.alienplayer.widget.DialogBuilder;

import java.util.List;

public class NetworkSearchActivity extends BaseActivity implements OnItemClickListener {
    public static final String KEY = "key";
    public static final String TYPE = "type";

    public static final int TYPE_ARTWORK = 0;
    public static final int TYPE_SONG = 1;

    private InputMethodManager mIMManager;
    private TextView mNoResult;
    private ProgressBar mLoading;
    private ListView mListView;
    private NetworkSearchResultsAdapter mAdapter;
    private int mType;
    private String mQueryKey;

    public interface SearchResultHandler {
        void onSuccess(List<NetworkSearchResult> results);

        void onFail(int status, String response);
    }

    private OnQueryTextListener mQueryTextListener = new OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            doQuery(query);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return true;
        }
    };

    private SearchResultHandler mSearchResultHandler = new SearchResultHandler() {
        @Override
        public void onSuccess(List<NetworkSearchResult> results) {
            NetworkSearchActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.setResults(results);
                    mLoading.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public void onFail(int status, String response) {
            NetworkSearchActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoading.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), R.string.network_error,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private HttpResponseHandler mSearchTrackHandler = new HttpResponseHandler() {
        @Override
        public void onSuccess(String response) {
            final List<NetworkSearchResult> tracks = JsonHelper.parseSearchTracks(response);
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
            NetworkSearchActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoading.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), R.string.network_error,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getIntent().getIntExtra(TYPE, TYPE_ARTWORK);
        setContentView(R.layout.activity_network_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            doQuery(intent.getStringExtra(SearchManager.QUERY));
        }
    }

    private void init() {
        mIMManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mNoResult = (TextView) findViewById(R.id.no_result);
        mLoading = (ProgressBar) findViewById(R.id.loading);
        mListView = (ListView) findViewById(R.id.list);
        mAdapter = new NetworkSearchResultsAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    private void doQuery(String query) {
        if (TextUtils.isEmpty(query)) {
            return;
        }

        mQueryKey = query;
        mNoResult.setVisibility(View.GONE);
        mLoading.setVisibility(View.VISIBLE);
        if (mType == TYPE_ARTWORK) {
            HttpHelper.search(NetworkSearchResult.TYPE_ALBUMS, mQueryKey, mSearchResultHandler);
        } else {
            HttpHelper.search(NetworkSearchResult.TYPE_ARTISTS, mQueryKey, mSearchResultHandler);
        }
        //DiscoverSuggestionsProvider.saveRecentQuery(NetworkSearchActivity.this, mQueryKey);
        mIMManager.hideSoftInputFromWindow(mListView.getWindowToken(), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.discover, menu);
        initSearchBar(menu.findItem(R.id.search));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.download_list) {
            Intent intent = new Intent(this, DownloadListActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.clear_history) {
            //DiscoverSuggestionsProvider.clearHistory(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initSearchBar(MenuItem item) {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(mQueryTextListener);
        searchView.setIconified(false);
        if (mType == TYPE_ARTWORK) {
            searchView.setQuery(getIntent().getStringExtra(KEY), true);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NetworkSearchResult result = mAdapter.getItem(position);
        if (result.type == NetworkSearchResult.TYPE_ARTISTS) {
            Intent intent = new Intent(this, NetworkAlbumsActivity.class);
            intent.putExtra(NetworkAlbumsActivity.ARTIST_ID, result.id);
            intent.putExtra(NetworkAlbumsActivity.LABEL, result.name);
            startActivity(intent);
        } else if (result.type == NetworkSearchResult.TYPE_ALBUMS) {
            if (mType == TYPE_ARTWORK) {
                NetworkAlbumInfo album = (NetworkAlbumInfo) result;
                if (!TextUtils.isEmpty(album.avatar)) {
                    Intent data = new Intent();
                    data.putExtra(TrackInfoActivity.EXTRA_ARTWORK, album.avatar);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                }
            } else {
                Intent intent = new Intent(this, NetworkTracksActivity.class);
                intent.putExtra(NetworkTracksActivity.ALBUM_ID, result.id);
                intent.putExtra(NetworkTracksActivity.LABEL, result.name);
                startActivity(intent);
            }
        } else if (result.type == NetworkSearchResult.TYPE_TRACKS) {
            downloadTrack((NetworkTrackInfo) result);
        }
    }

    private void downloadTrack(final NetworkTrackInfo info) {
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == Dialog.BUTTON_POSITIVE) {
                    HttpHelper.getTrack(String.valueOf(info.id), mDownloadInfoHandler);
                }
            }
        };

        Dialog dialog = DialogBuilder.buildAlertDialog(this, R.string.download_track_confirm,
                listener);
        dialog.show();
    }

    private HttpResponseHandler mDownloadInfoHandler = new HttpResponseHandler() {
        @Override
        public void onSuccess(String response) {
            NetworkTrackInfo info = JsonHelper.parseTrack(response);
            if (info != null) {
                String url = HttpHelper.getDownloadTrackUrl(String.valueOf(info.dfsId), info.ext);
                //TODO FileDownloadingHelper.getInstance().requstDownloadTrack(info, url);
            }
        }

        @Override
        public void onFail(int status, String response) {
            NetworkSearchActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.download_fail,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
}
