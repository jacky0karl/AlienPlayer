package com.jk.alienplayer.ui.fragment;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.impl.MediaScanService;
import com.jk.alienplayer.metadata.AlbumInfo;
import com.jk.alienplayer.metadata.CurrentlistInfo;
import com.jk.alienplayer.ui.SongsActivity;
import com.jk.alienplayer.ui.adapter.AlbumsAdapter;

import java.util.List;

public class AlbumsFragment extends Fragment {
    private ListView mListView;
    private AlbumsAdapter mAdapter;
    private List<AlbumInfo> mAlbums;
    private ProgressBar mLoading;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MediaScanService.ACTION_MEDIA_SCAN_COMPLETED)) {
                updateAlbums();
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

    private void init(View root) {
        mLoading = (ProgressBar) root.findViewById(R.id.loading);
        mListView = (ListView) root.findViewById(R.id.list);
        mAdapter = new AlbumsAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlbumInfo info = mAdapter.getItem(position);
                startSongsPage(info.id, info.name);
            }
        });
        updateAlbums();
    }

    @Override
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        super.onDestroyView();
    }

    private void startSongsPage(long key, String label) {
        Intent intent = new Intent(getActivity(), SongsActivity.class);
        intent.putExtra(SongsActivity.KEY_TYPE, CurrentlistInfo.TYPE_ALBUM);
        intent.putExtra(SongsActivity.KEY, key);
        intent.putExtra(SongsActivity.LABEL, label);
        startActivity(intent);
    }

    private void updateAlbums() {
        mLoading.setVisibility(View.VISIBLE);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mAlbums = DatabaseHelper.getAllAlbums(getActivity());
                for (AlbumInfo info : mAlbums) {
                    info.artwork = DatabaseHelper.getAlbumArtwork(getActivity(), info.id);
                }
                updateList();
            }
        });
        thread.start();
    }

    private void updateList() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.setAlbums(mAlbums);
                mLoading.setVisibility(View.GONE);
            }
        });
    }
}
