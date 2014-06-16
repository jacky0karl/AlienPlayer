package com.jk.alienplayer.ui;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.data.SongInfo;
import com.jk.alienplayer.impl.PlayingHelper;
import com.jk.alienplayer.ui.adapter.TracksAdapter;
import com.jk.alienplayer.ui.lib.Playbar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class TracksFragment extends Fragment {

    private ListView mListView;
    private TracksAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        init(root);
        return root;
    }

    private void init(View root) {
        mListView = (ListView) root.findViewById(R.id.list);
        mAdapter = new TracksAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mAdapter.setTracks(DatabaseHelper.getTracks(getActivity(), DatabaseHelper.TYPE_ALL, null));

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
        Playbar helper = ((MainActivity) getActivity()).getPlaybarHelper();
        if (PlayingHelper.getInstance().play(helper.getListener())) {
            helper.setPlayBtnImage(R.drawable.pause);
        }
        helper.setArtwork(song);
    }
}
