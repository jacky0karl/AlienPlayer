package com.jk.alienplayer.ui.fragment;

import java.util.Collections;
import java.util.List;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.impl.MediaScanService;
import com.jk.alienplayer.metadata.ArtistInfo;
import com.jk.alienplayer.metadata.CurrentlistInfo;
import com.jk.alienplayer.ui.AlbumsActivity;
import com.jk.alienplayer.ui.SongsActivity;
import com.jk.alienplayer.ui.adapter.ArtistsAdapter;
import com.jk.alienplayer.ui.lib.ListSeekBar;
import com.jk.alienplayer.ui.lib.ListSeekBar.OnIndicatorChangedListener;
import com.jk.alienplayer.utils.PinyinUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ArtistsFragment extends Fragment {
    public static final String TYPE = "type";
    public static final int TYPE_ARTISTS = 0;
    public static final int TYPE_ALBUM_ARTISTS = 1;

    private int mType;
    private ListView mListView;
    private ArtistsAdapter mAdapter;
    private ListSeekBar mListSeekBar;
    private TextView mIndicator;
    private ProgressBar mLoading;
    private List<ArtistInfo> mArtists;

    public static ArtistsFragment newInstance(int type) {
        ArtistsFragment fragment = new ArtistsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MediaScanService.ACTION_MEDIA_SCAN_COMPLETED)) {
                updateArtists();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mType = getArguments().getInt(TYPE, TYPE_ARTISTS);
        View root = inflater.inflate(R.layout.fragment_list_seekbar, container, false);
        init(root);
        MediaScanService.registerScanReceiver(getActivity(), mReceiver);
        return root;
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
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
                startSubPage(info.id, info.name);
            }
        }
    };

    private void startSubPage(long key, String label) {
        Intent intent = null;
        if (mType == TYPE_ARTISTS) {
            intent = new Intent(getActivity(), SongsActivity.class);
            intent.putExtra(SongsActivity.KEY_TYPE, CurrentlistInfo.TYPE_ARTIST);
            intent.putExtra(SongsActivity.KEY, key);
            intent.putExtra(SongsActivity.LABEL, label);
        } else {
            intent = new Intent(getActivity(), AlbumsActivity.class);
            intent.putExtra(AlbumsActivity.ALBUM_ARTIST, label);
        }
        startActivity(intent);
    }

    private void updateArtists() {
        mLoading.setVisibility(View.VISIBLE);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (mType == TYPE_ARTISTS) {
                    mArtists = DatabaseHelper.getArtists(getActivity());
                } else {
                    mArtists = DatabaseHelper.getAlbumArtists(getActivity());
                }
                sortList();
                updateList();
            }
        });
        thread.start();
    }

    private void sortList() {
        PinyinUtils pu = new PinyinUtils();
        for (ArtistInfo info : mArtists) {
            info.sortKey = pu.getPinyinString(info.name);
        }
        Collections.sort(mArtists, new ArtistInfo.ArtistComparator());
        updateList();
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
