package com.jk.alienplayer.ui.fragment;

import java.util.List;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.impl.MediaScanService;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.metadata.CurrentlistInfo;
import com.jk.alienplayer.metadata.SongInfo;
import com.jk.alienplayer.ui.adapter.TracksAdapter;
import com.jk.alienplayer.ui.lib.ListMenu;
import com.jk.alienplayer.ui.lib.ListMenu.OnMenuItemClickListener;
import com.jk.alienplayer.ui.lib.TrackOperationHelper;
import com.jk.alienplayer.ui.lib.TrackOperationHelper.OnDeleteTrackListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

public class TracksFragment extends Fragment implements OnMenuItemClickListener {
    private ListView mListView;
    private TracksAdapter mAdapter;
    private ListMenu mListMenu;
    private PopupWindow mPopupWindow;
    private ProgressBar mLoading;
    private List<SongInfo> mTracks;
    private SongInfo mCurrTrack;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MediaScanService.ACTION_MEDIA_SCAN_COMPLETED)) {
                updateTracks();
            }
        }
    };

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mCurrTrack = mAdapter.getItem(position);
            if (mCurrTrack != null) {
                if (view.getId() == R.id.action) {
                    mPopupWindow.showAsDropDown(view);
                } else {
                    onSongClick(mCurrTrack);
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        init(root);
        MediaScanService.registerScanReceiver(getActivity(), mReceiver);
        return root;
    }

    @Override
    public void onDestroyView() {
        mPopupWindow.dismiss();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        super.onDestroyView();
    }

    private void init(View root) {
        mLoading = (ProgressBar) root.findViewById(R.id.loading);
        mListView = (ListView) root.findViewById(R.id.list);
        mAdapter = new TracksAdapter(getActivity(), mOnItemClickListener);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mOnItemClickListener);
        setupPopupWindow();
        updateTracks();
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

    private void updateTracks() {
        mLoading.setVisibility(View.VISIBLE);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mTracks = DatabaseHelper.getTracks(getActivity(), CurrentlistInfo.TYPE_ALL, -1);
                updateList();
            }
        });
        thread.start();
    }

    private void updateList() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.setTracks(mTracks);
                mLoading.setVisibility(View.GONE);
            }
        });
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
            deleteTrack();
        } else if (ListMenu.MEMU_ADD_TO_PLAYLIST == menuId) {
            TrackOperationHelper.addToPlaylist(getActivity(), mCurrTrack.id);
        }
    }

    private void deleteTrack() {
        OnDeleteTrackListener listener = new OnDeleteTrackListener() {
            @Override
            public void onComplete() {
                mAdapter.setTracks(DatabaseHelper.getTracks(getActivity(),
                        CurrentlistInfo.TYPE_ALL, -1));
            }
        };
        TrackOperationHelper.deleteTrack(getActivity(), mCurrTrack, listener);
    }
}
