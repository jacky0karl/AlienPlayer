package com.jk.alienplayer.ui;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.data.SongInfo;
import com.jk.alienplayer.impl.PlayingHelper;
import com.jk.alienplayer.impl.PlayingHelper.PlayingProgressBarListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class SongsActivity extends Activity {

    public static final String KEY_TYPE = "key_type";
    public static final String KEY = "key";

    private int mKeyType;
    private String mKey;
    private ListView mListView;
    private SongsAdapter mAdapter;
    private PlaybarHelper mPlaybarHelper;
    private Handler mHandler = new Handler();

    private PlayingProgressBarListener mListener = new PlayingProgressBarListener() {
        @Override
        public void onProgressStart(int duration) {
            mPlaybarHelper.setMaxProgress(duration);
            startProgressUpdate();
        }
    };

    Runnable mUpdateTask = new Runnable() {
        @Override
        public void run() {
            int progress = PlayingHelper.getInstance().getCurrentPosition();
            mPlaybarHelper.setProgress(progress);
            mHandler.postDelayed(mUpdateTask, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        init();
    }

    private void init() {
        mPlaybarHelper = new PlaybarHelper(this, mListener);
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
    }

    private void onSongClick(SongInfo song) {
        PlayingHelper.getInstance().setCurrentSong(this, song);
        if (PlayingHelper.getInstance().play(mListener)) {
            mPlaybarHelper.setPlayBtnImage(R.drawable.pause);
        }
        mPlaybarHelper.setArtwork(song);
    }

    public void startProgressUpdate() {
        mHandler.post(mUpdateTask);
    }
}
