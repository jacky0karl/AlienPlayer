package com.jk.alienplayer.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.impl.MediaScanService;
import com.jk.alienplayer.metadata.ArtistInfo;
import com.jk.alienplayer.ui.artistdetail.AlbumsActivity;
import com.jk.alienplayer.ui.adapter.ArtistsAdapter;
import com.jk.alienplayer.widget.ListSeekBar;
import com.jk.alienplayer.widget.ListSeekBar.OnIndicatorChangedListener;
import com.jk.alienplayer.utils.PinyinUtils;

import java.util.Collections;
import java.util.List;

public class ArtistsFragment extends Fragment {
    private ListView mListView;
    private ArtistsAdapter mAdapter;
    private ListSeekBar mListSeekBar;
    private TextView mIndicator;
    private ProgressBar mLoading;
    private List<ArtistInfo> mArtists;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MediaScanService.ACTION_MEDIA_SCAN_COMPLETED)) {
                updateArtists();
            }
        }
    };
    private OnIndicatorChangedListener mIndicatorChangedListener = new OnIndicatorChangedListener() {
        @Override
        public void onIndicatorShow() {
            mIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        public void onIndicatorDismiss() {
            mIndicator.setVisibility(View.GONE);
        }

        @Override
        public void onIndicatorChange(String indicator) {
            if (mArtists == null) {
                return;
            }
            mIndicator.setText(indicator);

            char indiChar = indicator.toLowerCase().charAt(0);
            if (indiChar < 'a' || indiChar > 'z') {
                mListView.setSelection(0);
                return;
            }

            for (int i = 0; i < mArtists.size(); i++) {
                char ch = mArtists.get(i).sortKey.charAt(0);
                if (indiChar == ch) {
                    mListView.setSelection(i);
                    return;
                }
            }
        }
    };
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ArtistInfo info = mAdapter.getItem(position);
            if (info != null) {
                startAlbumsActivity(info.id, info.name);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list_seekbar, container, false);
        init(root);
        MediaScanService.registerScanReceiver(getActivity(), mReceiver);
        return root;
    }

    @Override
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        super.onDestroyView();
    }

    private void init(View root) {
        mListSeekBar = (ListSeekBar) root.findViewById(R.id.listSeekBar);
        mIndicator = (TextView) root.findViewById(R.id.indicator);
        mListSeekBar.setOnIndicatorChangedListener(mIndicatorChangedListener);

        mLoading = (ProgressBar) root.findViewById(R.id.loading);
        mListView = (ListView) root.findViewById(R.id.list);
        mAdapter = new ArtistsAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        updateArtists();
        mListView.setOnItemClickListener(mOnItemClickListener);
    }

    private void startAlbumsActivity(long id, String name) {
        Intent intent = new Intent(getActivity(), AlbumsActivity.class);
        intent.putExtra(AlbumsActivity.ARTIST_ID, id);
        intent.putExtra(AlbumsActivity.ARTIST_NAME, name);
        startActivity(intent);
    }

    private void updateArtists() {
        mLoading.setVisibility(View.VISIBLE);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mArtists = DatabaseHelper.getArtists(getActivity());
                //mArtists = DatabaseHelper.getAlbumArtists(getActivity());
                sortList();
                updateList();
            }
        });
        thread.start();
    }

    private void sortList() {
        for (ArtistInfo info : mArtists) {
            info.sortKey = PinyinUtils.getPinyinString(info.name);
            if (TextUtils.isEmpty(info.sortKey)) {
                info.sortKey = "#";
            }
        }
        Collections.sort(mArtists, new ArtistInfo.ArtistComparator());
    }

    private void updateList() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.setArtists(mArtists);
                mLoading.setVisibility(View.GONE);
            }
        });
    }
}
