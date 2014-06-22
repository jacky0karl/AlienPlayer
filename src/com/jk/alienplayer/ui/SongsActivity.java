package com.jk.alienplayer.ui;

import java.util.List;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.metadata.SongInfo;
import com.jk.alienplayer.ui.adapter.TracksAdapter;
import com.jk.alienplayer.ui.lib.ListMenu;
import com.jk.alienplayer.ui.lib.Playbar;
import com.jk.alienplayer.ui.lib.PlaylistSeletor;
import com.jk.alienplayer.ui.lib.ListMenu.OnMenuItemClickListener;

import android.app.Activity;
import android.app.Dialog;
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
    private Dialog mPlaylistSeletor = null;

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
        mKeyType = getIntent().getIntExtra(KEY_TYPE, DatabaseHelper.TYPE_ARTIST);
        mKey = getIntent().getLongExtra(KEY, -1);
        setTitle(getIntent().getStringExtra(LABEL));

        mListView = (ListView) findViewById(R.id.list);
        mAdapter = new TracksAdapter(this, mOnItemClickListener);
        mListView.setAdapter(mAdapter);

        List<SongInfo> songs = null;
        if (mKeyType == DatabaseHelper.TYPE_PLAYLIST) {
            songs = DatabaseHelper.getPlaylistMembers(this, mKey);
        } else {
            songs = DatabaseHelper.getTracks(this, mKeyType, mKey);
        }
        mAdapter.setTracks(songs);
        mListView.setOnItemClickListener(mOnItemClickListener);
        setupPopupWindow();
    }

    private void setupPopupWindow() {
        mListMenu = new ListMenu(this);
        mListMenu.setMenuItemClickListener(this);
        mListMenu.addMenu(ListMenu.MEMU_ADD_TO_PLAYLIST, R.string.add_to_playlist);
        mListMenu.addMenu(ListMenu.MEMU_DELETE, R.string.delete);
        mPopupWindow = new PopupWindow(mListMenu, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
    }

    private void onSongClick(SongInfo song) {
        PlayingInfoHolder.getInstance().setCurrentSong(this, song);
        Intent intent = PlayService.getPlayingCommandIntent(this, PlayService.COMMAND_PLAY);
        startService(intent);
    }

    @Override
    public void onClick(int menuId) {
        mPopupWindow.dismiss();
        if (ListMenu.MEMU_DELETE == menuId) {
            // DatabaseHelper.deletePlaylist(getActivity(), mCurrPlaylist.id);
        } else if (ListMenu.MEMU_ADD_TO_PLAYLIST == menuId) {
            mPlaylistSeletor = PlaylistSeletor.buildPlaylistSeletor(this, mPlaylistSeletorListener);
            mPlaylistSeletor.show();
        }
    }

    private OnItemClickListener mPlaylistSeletorListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mPlaylistSeletor.dismiss();
            DatabaseHelper.addMemberToPlaylist(SongsActivity.this, id, mCurrTrack.id);
        }
    };
}
