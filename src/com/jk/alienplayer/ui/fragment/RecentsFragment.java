package com.jk.alienplayer.ui.fragment;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.metadata.SongInfo;
import com.jk.alienplayer.ui.adapter.TracksAdapter;

import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class RecentsFragment extends Fragment {

    private ListView mListView;
    private TracksAdapter mAdapter;

    private ContentObserver mContentObserver = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.setTracks(DatabaseHelper.getRecentTracks(getActivity()));
                }
            });
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        init(root);
        getActivity().getContentResolver().registerContentObserver(
                DatabaseHelper.getRecentsUri(getActivity()), true, mContentObserver);
        return root;
    }

    @Override
    public void onDestroy() {
        getActivity().getContentResolver().unregisterContentObserver(mContentObserver);
        super.onDestroy();
    }

    private void init(View root) {
        mListView = (ListView) root.findViewById(R.id.list);
        mAdapter = new TracksAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mAdapter.setTracks(DatabaseHelper.getRecentTracks(getActivity()));

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SongInfo info = mAdapter.getItem(position);
                onSongClick(info);
            }
        });
    }

    private void onSongClick(SongInfo song) {
        PlayingInfoHolder.getInstance().setCurrentSong(getActivity(), song);
        Intent intent = PlayService
                .getPlayingCommandIntent(getActivity(), PlayService.COMMAND_PLAY);
        getActivity().startService(intent);
    }
}
