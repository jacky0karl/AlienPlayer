package com.jk.alienplayer.ui.fragment;

import java.util.List;

import com.jk.alienplayer.R;

import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.metadata.LyricInfo;
import com.jk.alienplayer.metadata.SongInfo;
import com.jk.alienplayer.metadata.LyricInfo.Sentence;
import com.jk.alienplayer.ui.adapter.LyricAdapter;
import com.jk.alienplayer.ui.lib.TrackOperationHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class LyricFragment extends Fragment {
    private TextView mNoLyric;
    private ListView mListView;
    private LyricAdapter mLyricAdapter;
    private LyricInfo mLyricInfo;
    private int mOffset;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PlayService.ACTION_TRACK_CHANGE)) {
                updateLyricInfo();
            } else if (intent.getAction().equals(PlayService.ACTION_PROGRESS_UPDATE)) {
                int progress = intent.getIntExtra(PlayService.CURRENT_DURATION, 0);
                updateHighlightPos(progress);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lyric, container, false);
        init(root);
        IntentFilter intentFilter = new IntentFilter(PlayService.ACTION_TRACK_CHANGE);
        intentFilter.addAction(PlayService.ACTION_PROGRESS_UPDATE);
        getActivity().registerReceiver(mReceiver, intentFilter);
        return root;
    }

    @Override
    public void onDestroyView() {
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroyView();
    }

    private void init(View root) {
        mNoLyric = (TextView) root.findViewById(R.id.no_lyric);
        mListView = (ListView) root.findViewById(R.id.list);
        mLyricAdapter = new LyricAdapter(getActivity());
        mListView.setAdapter(mLyricAdapter);
        mOffset = getActivity().getResources().getDimensionPixelOffset(R.dimen.lyric_offset);
        updateLyricInfo();
    }

    private void updateLyricInfo() {
        SongInfo songInfo = PlayingInfoHolder.getInstance().getCurrentSong();
        if (songInfo != null) {
            mLyricInfo = new LyricInfo(TrackOperationHelper.getLyricPath(songInfo.path));
        } else {
            mLyricInfo = new LyricInfo("");
        }

        mLyricAdapter.setLyric(mLyricInfo.getLyric());
        if (mLyricInfo.hasLyric()) {
            mNoLyric.setVisibility(View.GONE);
        } else {
            mNoLyric.setVisibility(View.VISIBLE);
        }
    }

    private void updateHighlightPos(int progress) {
        List<Sentence> list = mLyricInfo.getSentences();
        for (int i = list.size() - 1; i >= 0; i--) {
            if (progress > list.get(i).startTime) {
                if (mLyricAdapter.getHighlightPos() != i) {
                    mLyricAdapter.setHighlightPos(i);
                    mListView.setSelectionFromTop(i, mOffset);
                }
                return;
            }
        }
    }
}
