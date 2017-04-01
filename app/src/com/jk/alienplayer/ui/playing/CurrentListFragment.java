package com.jk.alienplayer.ui.playing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.metadata.SongInfo;
import com.jk.alienplayer.ui.adapter.PlayingQueueAdapter;
import com.jk.alienplayer.widget.TrackOperationHelper;

public class CurrentListFragment extends Fragment {
    private ListView mListView;
    private PlayingQueueAdapter mAdapter;
    private SongInfo mCurrTrack;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PlayService.ACTION_TRACK_CHANGE)) {
                mAdapter.setTracks(PlayingInfoHolder.getInstance().getCurrentlist());
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mCurrTrack = mAdapter.getItem(position);
            if (view.getId() == R.id.action) {
                showPopupMenu(view);
            } else {
                onSongClick(mCurrTrack);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        init(root);

        IntentFilter intentFilter = new IntentFilter(PlayService.ACTION_TRACK_CHANGE);
        intentFilter.addAction(PlayService.ACTION_PAUSE);
        intentFilter.addAction(PlayService.ACTION_STOP);
        intentFilter.addAction(PlayService.ACTION_START);
        getActivity().registerReceiver(mReceiver, intentFilter);
        return root;
    }

    @Override
    public void onDestroyView() {
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroyView();
    }

    private void init(View root) {
        mListView = (ListView) root.findViewById(R.id.list);
        mAdapter = new PlayingQueueAdapter(getActivity(), mOnItemClickListener);
        mListView.setAdapter(mAdapter);
        mAdapter.setTracks(PlayingInfoHolder.getInstance().getCurrentlist());
        mListView.setOnItemClickListener(mOnItemClickListener);
    }

    private void onSongClick(SongInfo song) {
        PlayingInfoHolder.getInstance().setCurrentSong(getActivity(), song);
        Intent intent = PlayService
                .getPlayingCommandIntent(getActivity(), PlayService.COMMAND_PLAY);
        getActivity().startService(intent);
    }

    private void showPopupMenu(View v) {
        PopupMenu menu = new PopupMenu(getActivity(), v);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                TrackOperationHelper.addToPlaylist(getActivity(), mCurrTrack.id);
                return false;
            }
        });

        menu.getMenu().add(Menu.NONE, Menu.NONE, Menu.NONE, R.string.add_to_playlist);
        menu.show();
    }

}
