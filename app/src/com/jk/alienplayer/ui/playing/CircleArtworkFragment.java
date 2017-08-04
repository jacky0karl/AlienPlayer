package com.jk.alienplayer.ui.playing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jk.alienplayer.MainApplication;
import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.metadata.SongInfo;
import com.jk.alienplayer.widget.CircleRotateDrawable;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Timer;
import java.util.TimerTask;

public class CircleArtworkFragment extends Fragment {

    private ImageView mArtwork;
    private ImageView mRepeatBtn;
    private Timer mTimer;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PlayService.ACTION_TRACK_CHANGE)) {
                syncView();
            } else if (intent.getAction().equals(PlayService.ACTION_START)) {
                startTimer();
            } else if (intent.getAction().equals(PlayService.ACTION_PAUSE)
                    || intent.getAction().equals(PlayService.ACTION_STOP)) {
                if (mTimer != null) {
                    mTimer.cancel();
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_artwork_circle, container, false);
        init(root);
        IntentFilter intentFilter = new IntentFilter(PlayService.ACTION_TRACK_CHANGE);
        intentFilter.addAction(PlayService.ACTION_START);
        intentFilter.addAction(PlayService.ACTION_PAUSE);
        intentFilter.addAction(PlayService.ACTION_STOP);
        getActivity().registerReceiver(mReceiver, intentFilter);
        return root;
    }

    @Override
    public void onDestroyView() {
        getActivity().unregisterReceiver(mReceiver);
        if (mTimer != null) {
            mTimer.cancel();
        }
        super.onDestroyView();
    }

    private void init(View root) {
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

    public void startTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }

        mTimer = new Timer(false);
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(() -> {
                    int level = mArtwork.getDrawable().getLevel();
                    level = level + 50;
                    if (level > 10000) {
                        level = level - 10000;
                    }
                    mArtwork.getDrawable().setLevel(level);
                });
            }
        }, 50, 50);
    }

    private void syncView() {
        SongInfo songInfo = PlayingInfoHolder.getInstance().getCurrentSong();
        String file = DatabaseHelper.getAlbumArtwork(songInfo.albumId);
        Picasso.with(MainApplication.app).load(file).config(Bitmap.Config.RGB_565)
                .error(R.drawable.ic_disc).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                BitmapDrawable bd = new BitmapDrawable(getResources(), bitmap);
                CircleRotateDrawable crd = new CircleRotateDrawable();
                crd.setDrawable(bd);
                mArtwork.setImageDrawable(crd);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });

        if (PlayingInfoHolder.getInstance().isPlaying()) {
            startTimer();
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
