package com.jk.alienplayer.ui;

import java.util.List;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.JsonHelper;
import com.jk.alienplayer.metadata.NetworkAlbumInfo;
import com.jk.alienplayer.network.HttpHelper;
import com.jk.alienplayer.network.HttpHelper.HttpResponseHandler;
import com.jk.alienplayer.ui.adapter.NetworkAlbumsAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class NetworkAlbumsActivity extends Activity {
    public static final String LABEL = "label";
    public static final String ARTIST_ID = "artist_id";

    private TextView mNoResult;
    private ProgressBar mLoading;
    private ListView mListView;
    private NetworkAlbumsAdapter mAdapter;
    private long mArtistId;

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            NetworkAlbumInfo info = mAdapter.getItem(position);
            Intent intent = new Intent(NetworkAlbumsActivity.this, NetworkTracksActivity.class);
            intent.putExtra(NetworkTracksActivity.ALBUM_ID, info.id);
            intent.putExtra(NetworkTracksActivity.LABEL, info.name);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_list);
        init();
    }

    private void init() {
        setTitle(getIntent().getStringExtra(LABEL));
        mArtistId = getIntent().getLongExtra(ARTIST_ID, -1);
        mNoResult = (TextView) findViewById(R.id.no_result);
        mLoading = (ProgressBar) findViewById(R.id.loading);
        mListView = (ListView) findViewById(R.id.list);
        mAdapter = new NetworkAlbumsAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mOnItemClickListener);

        mLoading.setVisibility(View.VISIBLE);
        mNoResult.setVisibility(View.GONE);
        HttpHelper.getAlbums(String.valueOf(mArtistId), mResponseHandler);
    }

    private HttpResponseHandler mResponseHandler = new HttpResponseHandler() {
        @Override
        public void onSuccess(String response) {
            final List<NetworkAlbumInfo> albums = JsonHelper.parseAlbums(response);
            NetworkAlbumsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.setAlbums(albums);
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
}
