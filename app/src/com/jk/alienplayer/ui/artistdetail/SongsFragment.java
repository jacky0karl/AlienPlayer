package com.jk.alienplayer.ui.artistdetail;

import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Playlists;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.data.PlaylistHelper;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.metadata.CurrentlistInfo;
import com.jk.alienplayer.metadata.SongInfo;
import com.jk.alienplayer.ui.adapter.OnItemClickListener;
import com.jk.alienplayer.ui.adapter.SongsAdapter;
import com.jk.alienplayer.widget.DividerItemDecoration;
import com.jk.alienplayer.widget.TrackOperationHelper;
import com.jk.alienplayer.widget.TrackOperationHelper.OnDeleteTrackListener;

import java.util.List;

public class SongsFragment extends Fragment {
    public static final String KEY_TYPE = "key_type";
    public static final String KEY = "key";
    public static final String LABEL = "label";

    public static final int MEMU_ADD_TO_PLAYLIST = 0;
    public static final int MEMU_DELETE = 1;
    public static final int MEMU_REMOVE = 2;

    private int mKeyType;
    private long mKey;
    private String mLabel;
    private RecyclerView mRecyclerView;
    private SongsAdapter mAdapter;
    private SongInfo mCurrTrack = null;
    private List<SongInfo> mSongList = null;

    private ContentObserver mContentObserver = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) {
            getActivity().runOnUiThread(() -> updateList());
        }
    };

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener<SongInfo>() {
        @Override
        public void onItemClick(View view, int position, SongInfo obj) {
            if (obj == null) {
                return;
            }

            mCurrTrack = obj;
            if (view.getId() == R.id.menu) {
                showPopupMenu(view);
            } else {
                onSongClick(mCurrTrack);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_songs, container, false);
        init(root);
        return root;
    }

    @Override
    public void onDestroyView() {
        if (mKeyType == CurrentlistInfo.TYPE_PLAYLIST) {
            getActivity().getContentResolver().unregisterContentObserver(mContentObserver);
        }
        super.onDestroyView();
    }

    public void updateList() {
        if (!isAdded()) {
            return;
        }

        if (mKeyType == CurrentlistInfo.TYPE_PLAYLIST) {
            mSongList = PlaylistHelper.getPlaylistMembers(getActivity(), mKey);
        } else if (mKeyType == CurrentlistInfo.TYPE_ARTIST) {
            mSongList = DatabaseHelper.getTracks(getActivity(), mLabel);
        } else {
            mSongList = DatabaseHelper.getTracks(getActivity(), mKeyType, mKey);
        }
        mAdapter.setTracks(mSongList);
    }

    private void init(View root) {
        if (getArguments() != null) {
            mKeyType = getArguments().getInt(KEY_TYPE, CurrentlistInfo.TYPE_ARTIST);
            mKey = getArguments().getLong(KEY, -1);
            mLabel = getArguments().getString(LABEL);
        }

        if (mKeyType == CurrentlistInfo.TYPE_PLAYLIST) {
            getActivity().getContentResolver().registerContentObserver(Playlists.EXTERNAL_CONTENT_URI,
                    true, mContentObserver);
        }

        mRecyclerView = (RecyclerView) root.findViewById(R.id.list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new SongsAdapter(getActivity(), mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
        updateList();
    }

    private void onSongClick(SongInfo song) {
        CurrentlistInfo currentlistInfo = new CurrentlistInfo(mKey, mKeyType, mSongList);
        PlayingInfoHolder.getInstance().setCurrentInfo(getActivity(), song, currentlistInfo);

        Intent intent = PlayService.getPlayingCommandIntent(getActivity(), PlayService.COMMAND_PLAY);
        getActivity().startService(intent);
    }

    private void showPopupMenu(View v) {
        PopupMenu menu = new PopupMenu(getActivity(), v);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case MEMU_ADD_TO_PLAYLIST:
                        TrackOperationHelper.addToPlaylist(getActivity(), mCurrTrack.id);
                        break;
                    case MEMU_DELETE:
                        deleteTrack();
                        break;
                    case MEMU_REMOVE:
                        reomveTrack();
                        break;
                }
                return false;
            }
        });

        if (mKeyType == CurrentlistInfo.TYPE_PLAYLIST) {
            menu.getMenu().add(Menu.NONE, MEMU_REMOVE, Menu.NONE, R.string.remove);
        } else {
            menu.getMenu().add(Menu.NONE, MEMU_ADD_TO_PLAYLIST, Menu.NONE, R.string.add_to_playlist);
            menu.getMenu().add(Menu.NONE, MEMU_DELETE, Menu.NONE, R.string.delete);
        }
        menu.show();
    }

    private void reomveTrack() {
        PlaylistHelper.removeMemberFromPlaylist(getActivity(), mKey, mCurrTrack.id);
        updateList();
    }

    private void deleteTrack() {
        OnDeleteTrackListener listener = () -> updateList();
        TrackOperationHelper.deleteTrack(getActivity(), mCurrTrack, listener);
    }

}
