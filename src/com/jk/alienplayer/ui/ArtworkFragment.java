package com.jk.alienplayer.ui;

import com.jk.alienplayer.R;

import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.metadata.SongInfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ArtworkFragment extends Fragment {

    private int mArtworkSize;
    private ImageView mArtwork;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PlayService.ACTION_TRACK_CHANGE)) {
                syncView();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_playing, container, false);
        init(root);
        IntentFilter intentFilter = new IntentFilter(PlayService.ACTION_TRACK_CHANGE);
        getActivity().registerReceiver(mReceiver, intentFilter);
        return root;
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void init(View root) {
        mArtworkSize = getActivity().getResources().getDimensionPixelSize(
                R.dimen.detail_artwork_size);
        mArtwork = (ImageView) root.findViewById(R.id.artwork);
        syncView();
    }

    private void syncView() {
        SongInfo songInfo = PlayingInfoHolder.getInstance().getCurrentSong();
        Bitmap bmp = DatabaseHelper.getArtwork(getActivity(), songInfo.id, songInfo.albumId,
                mArtworkSize);
        if (bmp == null) {
            mArtwork.setImageResource(R.drawable.disk);
        } else {
            mArtwork.setImageBitmap(bmp);
        }
    }
}
