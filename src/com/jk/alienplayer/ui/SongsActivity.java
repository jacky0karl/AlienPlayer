package com.jk.alienplayer.ui;

import java.util.List;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.data.PlaylistHelper;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.metadata.CurrentlistInfo;
import com.jk.alienplayer.metadata.SongInfo;
import com.jk.alienplayer.ui.adapter.TracksAdapter;
import com.jk.alienplayer.ui.lib.ListMenu;
import com.jk.alienplayer.ui.lib.Playbar;
import com.jk.alienplayer.ui.lib.TrackOperationHelper;
import com.jk.alienplayer.ui.lib.ListMenu.OnMenuItemClickListener;
import com.jk.alienplayer.ui.lib.TrackOperationHelper.OnDeleteTrackListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

public class SongsActivity extends Activity implements OnMenuItemClickListener {
    public static final String KEY_TYPE = "key_type";
    public static final String KEY = "key";
    public static final String LABEL = "label";

    private int mKeyType;
    private long mKey;
    private ListView mListView;
    private TracksAdapter mAdapter;
    private Playbar mPlaybar;
    private ListMenu mListMenu;
    private PopupWindow mPopupWindow;
    private SongInfo mCurrTrack;
    private List<SongInfo> mSongList = null;

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mCurrTrack = mAdapter.getItem(position);
            if (view.getId() == R.id.action) {
                mPopupWindow.showAsDropDown(view);
            } else {
                onSongClick(mCurrTrack);
            }
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
        mPopupWindow.dismiss();
        mPlaybar.finish();
        super.onDestroy();
    }

    private void init() {
        mPlaybar = (Playbar) findViewById(R.id.playbar);
        mKeyType = getIntent().getIntExtra(KEY_TYPE, CurrentlistInfo.TYPE_ARTIST);
        mKey = getIntent().getLongExtra(KEY, -1);
        setTitle(getIntent().getStringExtra(LABEL));

        mListView = (ListView) findViewById(R.id.list);
        mAdapter = new TracksAdapter(this, mOnItemClickListener);
        mListView.setAdapter(mAdapter);
        updateList();
        mListView.setOnItemClickListener(mOnItemClickListener);
        setupPopupWindow();
    }

    private void setupPopupWindow() {
        mListMenu = new ListMenu(this);
        mListMenu.setMenuItemClickListener(this);
        if (mKeyType == CurrentlistInfo.TYPE_PLAYLIST) {
            mListMenu.addMenu(ListMenu.MEMU_REMOVE, R.string.remove);
        } else {
            mListMenu.addMenu(ListMenu.MEMU_ADD_TO_PLAYLIST, R.string.add_to_playlist);
            mListMenu.addMenu(ListMenu.MEMU_DELETE, R.string.delete);
        }

        mPopupWindow = new PopupWindow(mListMenu, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
    }

    private void onSongClick(SongInfo song) {
        CurrentlistInfo currentlistInfo = new CurrentlistInfo(mKey, mKeyType, mSongList);
        PlayingInfoHolder.getInstance().setCurrentInfo(this, song, currentlistInfo);

        Intent intent = PlayService.getPlayingCommandIntent(this, PlayService.COMMAND_PLAY);
        startService(intent);
    }

    @Override
    public void onClick(int menuId) {
        mPopupWindow.dismiss();
        if (ListMenu.MEMU_REMOVE == menuId) {
            reomveTrack();
        } else if (ListMenu.MEMU_DELETE == menuId) {
            deleteTrack();
        } else if (ListMenu.MEMU_ADD_TO_PLAYLIST == menuId) {
            TrackOperationHelper.addToPlaylist(SongsActivity.this, mCurrTrack.id);
        }
    }

    private void reomveTrack() {
        PlaylistHelper.removeMemberFromPlaylist(this, mKey, mCurrTrack.id);
        updateList();
    }

    private void deleteTrack() {
        OnDeleteTrackListener listener = new OnDeleteTrackListener() {
            @Override
            public void onComplete() {
                updateList();
            }
        };
        TrackOperationHelper.deleteTrack(this, mCurrTrack, listener);
    }

    private void updateList() {
        if (mKeyType == CurrentlistInfo.TYPE_PLAYLIST) {
            mSongList = PlaylistHelper.getPlaylistMembers(this, mKey);
        } else {
            mSongList = DatabaseHelper.getTracks(this, mKeyType, mKey);
        }
        mAdapter.setTracks(mSongList);
    }
}
