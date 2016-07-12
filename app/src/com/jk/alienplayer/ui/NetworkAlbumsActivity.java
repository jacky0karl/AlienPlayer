package com.jk.alienplayer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jk.alienplayer.R;
import com.jk.alienplayer.model.AlbumBean;
import com.jk.alienplayer.presenter.AlbumsPresenter;
import com.jk.alienplayer.ui.adapter.NetworkAlbumsAdapter;

import java.util.List;

public class NetworkAlbumsActivity extends BaseActivity {
    public static final String LABEL = "label";
    public static final String ARTIST_ID = "artist_id";

    private TextView mNoResult;
    private ProgressBar mLoading;
    private ListView mListView;
    private NetworkAlbumsAdapter mAdapter;
    private long mArtistId;
    private AlbumsPresenter mPresenter;

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AlbumBean info = mAdapter.getItem(position);
            Intent intent = new Intent(NetworkAlbumsActivity.this, NetworkTracksActivity.class);
            intent.putExtra(NetworkTracksActivity.ALBUM_ID, info.getId());
            intent.putExtra(NetworkTracksActivity.LABEL, info.getName());
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
        mPresenter = new AlbumsPresenter(this);
        mPresenter.fetchAlbums(mArtistId);
    }

    public void fetchAlbumsSuccess(List<AlbumBean> albums) {
        mAdapter.setAlbums(albums);
        if (mAdapter.getCount() == 0) {
            mNoResult.setText(R.string.no_result);
            mNoResult.setVisibility(View.VISIBLE);
        } else {
            mNoResult.setVisibility(View.GONE);
        }
        mLoading.setVisibility(View.GONE);
    }

    public void fetchAlbumsFail() {
        mNoResult.setText(R.string.network_error);
        mNoResult.setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.GONE);
    }

}
