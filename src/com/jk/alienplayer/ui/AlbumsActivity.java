package com.jk.alienplayer.ui;

import java.util.List;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.metadata.AlbumInfo;
import com.jk.alienplayer.metadata.CurrentlistInfo;
import com.jk.alienplayer.ui.adapter.AlbumsAdapter;
import com.jk.alienplayer.ui.lib.Playbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class AlbumsActivity extends Activity {
    public static final String ALBUM_ARTIST = "album_artist";

    private ListView mListView;
    private AlbumsAdapter mAdapter;
    private Playbar mPlaybar;
    private AlbumInfo mCurrAlbum;
    private String mAlbumArtist;
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
        mAlbumArtist = getIntent().getStringExtra(ALBUM_ARTIST);
        setTitle(mAlbumArtist);

        mListView = (ListView) findViewById(R.id.list);
        mAdapter = new AlbumsAdapter(this);
        mListView.setAdapter(mAdapter);
        mAlbums = DatabaseHelper.getAlbums(this, mAlbumArtist);
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
