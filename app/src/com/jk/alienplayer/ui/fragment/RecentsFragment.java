package com.jk.alienplayer.ui.fragment;

import java.util.List;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.PlayingInfoHolder;

import com.jk.alienplayer.data.RecentsDBHelper;
import com.jk.alienplayer.impl.MediaScanService;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.metadata.SongInfo;
import com.jk.alienplayer.ui.adapter.TracksAdapter;
import com.jk.alienplayer.ui.lib.ListMenu;
import com.jk.alienplayer.ui.lib.TrackOperationHelper;
import com.jk.alienplayer.ui.lib.ListMenu.OnMenuItemClickListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

public class RecentsFragment extends Fragment implements OnMenuItemClickListener {
    private ListView mListView;
    private TracksAdapter mAdapter;
    private ListMenu mListMenu;
    private PopupWindow mPopupWindow;
    private SongInfo mCurrTrack;

    private ContentObserver mContentObserver = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) {
            final List<SongInfo> recentsList = RecentsDBHelper.getRecentTracks(getActivity());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.setTracks(recentsList);
                }
            });
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MediaScanService.ACTION_MEDIA_SCAN_COMPLETED)) {
                List<SongInfo> recentsList = RecentsDBHelper.getRecentTracks(getActivity());
                mAdapter.setTracks(recentsList);
            }
        }
    };

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
        MediaScanService.registerScanReceiver(getActivity(), mReceiver);
        getActivity().getContentResolver().registerContentObserver(
                RecentsDBHelper.getRecentsUri(getActivity()), true, mContentObserver);
        return root;
    }

    @Override
    public void onDestroyView() {
        mPopupWindow.dismiss();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        getActivity().getContentResolver().unregisterContentObserver(mContentObserver);
        super.onDestroyView();
    }

    private void init(View root) {
        mListView = (ListView) root.findViewById(R.id.list);
        mAdapter = new TracksAdapter(getActivity(), mOnItemClickListener);
        mListView.setAdapter(mAdapter);
        mAdapter.setTracks(RecentsDBHelper.getRecentTracks(getActivity()));
        mListView.setOnItemClickListener(mOnItemClickListener);
        setupPopupWindow();
    }

    private void setupPopupWindow() {
        mListMenu = new ListMenu(getActivity());
        mListMenu.setMenuItemClickListener(this);
        mListMenu.addMenu(ListMenu.MEMU_ADD_TO_PLAYLIST, R.string.add_to_playlist);
        mListMenu.addMenu(ListMenu.MEMU_REMOVE, R.string.remove);
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
        if (ListMenu.MEMU_REMOVE == menuId) {
            reomveTrack();
        } else if (ListMenu.MEMU_ADD_TO_PLAYLIST == menuId) {
            TrackOperationHelper.addToPlaylist(getActivity(), mCurrTrack.id);
        }
    }

    private void reomveTrack() {
        RecentsDBHelper.removeFromRecents(getActivity(), mCurrTrack.id);
        mAdapter.setTracks(RecentsDBHelper.getRecentTracks(getActivity()));
    }
}
