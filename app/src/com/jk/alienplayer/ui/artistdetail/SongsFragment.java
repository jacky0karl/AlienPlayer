package com.jk.alienplayer.ui.artistdetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.data.PlaylistHelper;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.metadata.CurrentlistInfo;
import com.jk.alienplayer.metadata.SongInfo;
import com.jk.alienplayer.ui.adapter.TracksAdapter;
import com.jk.alienplayer.widget.ListMenu;
import com.jk.alienplayer.widget.ListMenu.OnMenuItemClickListener;
import com.jk.alienplayer.widget.TrackOperationHelper;
import com.jk.alienplayer.widget.TrackOperationHelper.OnDeleteTrackListener;

import java.util.List;

public class SongsFragment extends Fragment implements OnMenuItemClickListener {
    public static final String KEY_TYPE = "key_type";
    public static final String KEY = "key";
    public static final String LABEL = "label";

    private int mKeyType;
    private long mKey;
    private String mLabel;
    private ListView mListView;
    private TracksAdapter mAdapter;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_songs, container, false);
        init(root);
        return root;
    }

    @Override
    public void onDestroyView() {
        mPopupWindow.dismiss();
        super.onDestroy();
    }

    private void init(View root) {
        if (getArguments() !=null) {
            mKeyType = getArguments().getInt(KEY_TYPE, CurrentlistInfo.TYPE_ARTIST);
            mKey = getArguments().getLong(KEY, -1);
            mLabel = getArguments().getString(LABEL);
        }

        mListView = (ListView) root.findViewById(R.id.list);
        mAdapter = new TracksAdapter(getActivity(), mOnItemClickListener);
        mListView.setAdapter(mAdapter);
        updateList();
        mListView.setOnItemClickListener(mOnItemClickListener);
        setupPopupWindow();
    }

    private void setupPopupWindow() {
        mListMenu = new ListMenu(getActivity());
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
        PlayingInfoHolder.getInstance().setCurrentInfo(getActivity(), song, currentlistInfo);

        Intent intent = PlayService.getPlayingCommandIntent(getActivity(), PlayService.COMMAND_PLAY);
        getActivity().startService(intent);
    }

    @Override
    public void onClick(int menuId) {
        mPopupWindow.dismiss();
        if (ListMenu.MEMU_REMOVE == menuId) {
            reomveTrack();
        } else if (ListMenu.MEMU_DELETE == menuId) {
            deleteTrack();
        } else if (ListMenu.MEMU_ADD_TO_PLAYLIST == menuId) {
            TrackOperationHelper.addToPlaylist(getActivity(), mCurrTrack.id);
        }
    }

    private void reomveTrack() {
        PlaylistHelper.removeMemberFromPlaylist(getActivity(), mKey, mCurrTrack.id);
        updateList();
    }

    private void deleteTrack() {
        OnDeleteTrackListener listener = new OnDeleteTrackListener() {
            @Override
            public void onComplete() {
                updateList();
            }
        };
        TrackOperationHelper.deleteTrack(getActivity(), mCurrTrack, listener);
    }

    private void updateList() {
        if (mKeyType == CurrentlistInfo.TYPE_PLAYLIST) {
            mSongList = PlaylistHelper.getPlaylistMembers(getActivity(), mKey);
        } else if (mKeyType == CurrentlistInfo.TYPE_ARTIST) {
            mSongList = DatabaseHelper.getTracks(getActivity(), mLabel);
        } else {
            mSongList = DatabaseHelper.getTracks(getActivity(), mKeyType, mKey);
        }
        mAdapter.setTracks(mSongList);
    }
}
