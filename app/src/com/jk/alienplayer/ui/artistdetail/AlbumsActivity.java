package com.jk.alienplayer.ui.artistdetail;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.metadata.AlbumInfo;
import com.jk.alienplayer.metadata.CurrentlistInfo;
import com.jk.alienplayer.ui.BaseActivity;
import com.jk.alienplayer.ui.adapter.AlbumsAdapter;
import com.jk.alienplayer.widget.Playbar;

import java.util.List;

public class AlbumsActivity extends BaseActivity {
    public static final String ARTIST_ID = "artist_id";
    public static final String ARTIST_NAME = "artist_name";

    private ListView mListView;
    private AlbumsAdapter mAdapter;
    private Playbar mPlaybar;
    private AlbumInfo mCurrAlbum;
    private long mArtistId;
    private String mArtistName;
    private List<AlbumInfo> mAlbums;

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mCurrAlbum = mAdapter.getItem(position);
            onAlbumClick(mCurrAlbum);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        init();
    }

    @Override
    protected void onDestroy() {
        mPlaybar.finish();
        super.onDestroy();
    }

    private void init() {
        mPlaybar = (Playbar) findViewById(R.id.playbar);
        mArtistId = getIntent().getLongExtra(ARTIST_ID, 0);
        mArtistName = getIntent().getStringExtra(ARTIST_NAME);
        setTitle(mArtistName);

        mListView = (ListView) findViewById(R.id.list);
        mAdapter = new AlbumsAdapter(this);
        mListView.setAdapter(mAdapter);
        mAlbums = DatabaseHelper.getAlbums(this, mArtistId);
        mAdapter.setAlbums(mAlbums);
        mListView.setOnItemClickListener(mOnItemClickListener);
        getAlbumArtworks();
    }

    private void getAlbumArtworks() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (AlbumInfo info : mAlbums) {
                    info.artwork = DatabaseHelper.getAlbumArtwork(AlbumsActivity.this, info.id);
                }

                AlbumsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setAlbums(mAlbums);
                    }
                });
            }
        });
        thread.start();
    }

    private void onAlbumClick(AlbumInfo info) {
        Intent intent = new Intent(this, SongsActivity.class);
        intent.putExtra(SongsActivity.KEY_TYPE, CurrentlistInfo.TYPE_ALBUM);
        intent.putExtra(SongsActivity.KEY, info.id);
        intent.putExtra(SongsActivity.LABEL, info.name);
        startActivity(intent);
    }
}
