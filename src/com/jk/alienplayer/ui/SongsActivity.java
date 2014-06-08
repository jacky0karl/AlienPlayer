package com.jk.alienplayer.ui;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.data.SongInfo;
import com.jk.alienplayer.impl.PlayingHelper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class SongsActivity extends Activity {

    public static final String KEY_TYPE = "key_type";
    public static final String KEY = "key";

    private int mKeyType;
    private String mKey;
    private ListView mListView;
    private SongsAdapter mAdapter;
    private ImageButton mPlayBtn;
    private ImageButton mNextBtn;
    private ImageButton mPrevBtn;
    private ImageView mArtwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        init();
    }

    private void init() {
        mKeyType = getIntent().getIntExtra(KEY_TYPE, DatabaseHelper.TYPE_ARTIST);
        mKey = getIntent().getStringExtra(KEY);

        mListView = (ListView) findViewById(R.id.list);
        mAdapter = new SongsAdapter(this);
        mListView.setAdapter(mAdapter);
        mAdapter.setSongs(DatabaseHelper.getSongs(this, mKeyType, mKey));
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onSongClick(mAdapter.getItem(position));
            }
        });

        mPlayBtn = (ImageButton) findViewById(R.id.play);
        mPlayBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PlayingHelper.getInstance().playOrPause()) {
                    mPlayBtn.setImageResource(R.drawable.pause);
                } else {
                    mPlayBtn.setImageResource(R.drawable.play);
                }
            }
        });
        mNextBtn = (ImageButton) findViewById(R.id.next);
        mPrevBtn = (ImageButton) findViewById(R.id.prev);
        mArtwork = (ImageView) findViewById(R.id.artwork);
    }

    private void onSongClick(SongInfo song) {
        PlayingHelper.getInstance().setCurrentSong(song);
        if (PlayingHelper.getInstance().play()) {
            mPlayBtn.setImageResource(R.drawable.pause);
        }

        int size = getResources().getDimensionPixelOffset(R.dimen.playbar_artwork_size);
        Bitmap artwork = DatabaseHelper.getArtwork(SongsActivity.this, song.id, song.albumId, size);
        if (artwork != null) {
            mArtwork.setImageBitmap(artwork);
        }
    }
}
