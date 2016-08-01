package com.jk.alienplayer.ui.playing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.metadata.SongInfo;
import com.jk.alienplayer.utils.UiUtils;

public class ArtworkFragment extends Fragment {

    private int mArtworkSize;
    private ImageView mArtwork;
    private ImageView mRepeatBtn;

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
        View root = inflater.inflate(R.layout.fragment_artwork, container, false);
        init(root);
        IntentFilter intentFilter = new IntentFilter(PlayService.ACTION_TRACK_CHANGE);
        getActivity().registerReceiver(mReceiver, intentFilter);
        return root;
    }

    @Override
    public void onDestroyView() {
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroyView();
    }

    private void init(View root) {
        mArtworkSize = UiUtils.getScreenWidth(getActivity());
        mArtwork = (ImageView) root.findViewById(R.id.artwork);
        mRepeatBtn = (ImageView) root.findViewById(R.id.repeat_btn);
        mRepeatBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int repeatMode = PlayingInfoHolder.getInstance().getRepeatMode();
                if (repeatMode == PlayingInfoHolder.REPEAT_ALL) {
                    repeatMode = PlayingInfoHolder.REPEAT_ONE;
                    mRepeatBtn.setImageResource(R.drawable.repeat_one);
                } else if (repeatMode == PlayingInfoHolder.REPEAT_ONE) {
                    repeatMode = PlayingInfoHolder.REPEAT_SHUFFLE;
                    mRepeatBtn.setImageResource(R.drawable.repeat_shuffle);
                } else {
                    repeatMode = PlayingInfoHolder.REPEAT_ALL;
                    mRepeatBtn.setImageResource(R.drawable.repeat_all);
                }
                PlayingInfoHolder.getInstance().setRepeatMode(getActivity(), repeatMode);
            }
        });
        syncView();
    }

    private void syncView() {
        SongInfo songInfo = PlayingInfoHolder.getInstance().getCurrentSong();
        Bitmap bmp = DatabaseHelper.getArtworkFormFile(getActivity(), songInfo.id, songInfo.albumId, mArtworkSize);
        if (bmp == null) {
            mArtwork.setImageResource(R.drawable.disk);
        } else {
            mArtwork.setImageBitmap(bmp);
        }

        int repeatMode = PlayingInfoHolder.getInstance().getRepeatMode();
        if (repeatMode == PlayingInfoHolder.REPEAT_ALL) {
            mRepeatBtn.setImageResource(R.drawable.repeat_all);
        } else if (repeatMode == PlayingInfoHolder.REPEAT_ONE) {
            mRepeatBtn.setImageResource(R.drawable.repeat_one);
        } else {
            mRepeatBtn.setImageResource(R.drawable.repeat_shuffle);
        }
    }
}
