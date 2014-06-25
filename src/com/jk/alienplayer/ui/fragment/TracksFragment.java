package com.jk.alienplayer.ui.fragment;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.metadata.CurrentlistInfo;
import com.jk.alienplayer.metadata.SongInfo;
import com.jk.alienplayer.ui.adapter.TracksAdapter;
import com.jk.alienplayer.ui.lib.ListMenu;
import com.jk.alienplayer.ui.lib.ListMenu.OnMenuItemClickListener;
import com.jk.alienplayer.ui.lib.PlaylistSeletor;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

public class TracksFragment extends Fragment implements OnMenuItemClickListener {

    private ListView mListView;
    private TracksAdapter mAdapter;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        init(root);
        return root;
    }

    @Override
    public void onDestroy() {
        mPopupWindow.dismiss();
        super.onDestroy();
    }

    private void init(View root) {
        mListView = (ListView) root.findViewById(R.id.list);
        mAdapter = new TracksAdapter(getActivity(), mOnItemClickListener);
        mListView.setAdapter(mAdapter);
        mAdapter.setTracks(DatabaseHelper.getTracks(getActivity(), CurrentlistInfo.TYPE_ALL, -1));
        mListView.setOnItemClickListener(mOnItemClickListener);
        setupPopupWindow();
    }

    private void setupPopupWindow() {
        mListMenu = new ListMenu(getActivity());
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
        PlayingInfoHolder.getInstance().setCurrentInfo(getActivity(), song, null);
        Intent intent = PlayService
                .getPlayingCommandIntent(getActivity(), PlayService.COMMAND_PLAY);
        getActivity().startService(intent);
    }

    @Override
    public void onClick(int menuId) {
        mPopupWindow.dismiss();
        if (ListMenu.MEMU_DELETE == menuId) {
            // DatabaseHelper.deletePlaylist(getActivity(), mCurrPlaylist.id);
        } else if (ListMenu.MEMU_ADD_TO_PLAYLIST == menuId) {
            mPlaylistSeletor = PlaylistSeletor.buildPlaylistSeletor(getActivity(),
                    mPlaylistSeletorClickListener);
            mPlaylistSeletor.show();
        }
    }

    OnItemClickListener mPlaylistSeletorClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mPlaylistSeletor.dismiss();
            DatabaseHelper.addMemberToPlaylist(getActivity(), id, mCurrTrack.id);
        }
    };
}