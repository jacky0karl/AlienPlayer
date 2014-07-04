package com.jk.alienplayer.ui;

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
    public static final String ARTIST_ID = "artist_id";
    public static final String LABEL = "label";

    private ListView mListView;
    private AlbumsAdapter mAdapter;
    private Playbar mPlaybar;
    private AlbumInfo mCurrAlbum;
    private long mArtistId;

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
        mArtistId = getIntent().getLongExtra(ARTIST_ID, -1);
        setTitle(getIntent().getStringExtra(LABEL));

        mListView = (ListView) findViewById(R.id.list);
        mAdapter = new AlbumsAdapter(this);
        mListView.setAdapter(mAdapter);
        mAdapter.setAlbums(DatabaseHelper.getAlbums(this, mArtistId));
        mListView.setOnItemClickListener(mOnItemClickListener);

    }

    private void onAlbumClick(AlbumInfo info) {
        Intent intent = new Intent(this, SongsActivity.class);
        intent.putExtra(SongsActivity.KEY_TYPE, CurrentlistInfo.TYPE_ALBUM);
        intent.putExtra(SongsActivity.KEY, info.id);
        intent.putExtra(SongsActivity.LABEL, info.name);
        startActivity(intent);
    }
}
