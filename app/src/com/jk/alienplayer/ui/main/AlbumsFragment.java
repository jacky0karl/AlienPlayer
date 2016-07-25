package com.jk.alienplayer.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.jk.alienplayer.R;
import com.jk.alienplayer.impl.MediaScanService;
import com.jk.alienplayer.metadata.AlbumInfo;
import com.jk.alienplayer.metadata.CurrentlistInfo;
import com.jk.alienplayer.presenter.main.AlbumsPresenter;
import com.jk.alienplayer.ui.adapter.AlbumsAdapter;
import com.jk.alienplayer.ui.adapter.OnItemClickListener;
import com.jk.alienplayer.ui.artistdetail.SongsActivity;

import java.util.List;

public class AlbumsFragment extends Fragment {
    public static final String ARTIST_NAME = "artist_name";

    private String mArtistName;
    private RecyclerView mRecyclerView;
    private ProgressBar mLoading;
    private AlbumsAdapter mAdapter;
    private AlbumsPresenter mPresenter;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MediaScanService.ACTION_MEDIA_SCAN_COMPLETED)) {
                updateAlbums();
            }
        }
    };

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener<AlbumInfo>() {
        @Override
        public void onItemClick(View view, int position, AlbumInfo info) {
            if (info == null) {
                return;
            }

            Intent intent = new Intent(getActivity(), SongsActivity.class);
            intent.putExtra(SongsActivity.KEY_TYPE, CurrentlistInfo.TYPE_ALBUM);
            intent.putExtra(SongsActivity.KEY, info.id);
            intent.putExtra(SongsActivity.LABEL, info.name);
            startActivity(intent);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_albums, container, false);
        init(root);
        MediaScanService.registerScanReceiver(getActivity(), mReceiver);
        return root;
    }

    private void init(View root) {
        if (getArguments() != null) {
            mArtistName = getArguments().getString(ARTIST_NAME);
        }

        mLoading = (ProgressBar) root.findViewById(R.id.loading);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new AlbumsAdapter(getActivity(), mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);

        mPresenter = new AlbumsPresenter(this);
        updateAlbums();
    }

    @Override
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        super.onDestroyView();
    }

    private void updateAlbums() {
        mLoading.setVisibility(View.VISIBLE);
        mPresenter.getAlbums(mArtistName);
    }

    public void fetchAlbumsSucc(List<AlbumInfo> list) {
        mLoading.setVisibility(View.GONE);
        if (list != null) {
            mAdapter.setAlbums(list);
        }
    }

    public void fetchAlbumsFail() {
        mLoading.setVisibility(View.GONE);
    }
}
