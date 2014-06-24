package com.jk.alienplayer.ui.lib;

import com.jk.alienplayer.R;

import android.content.Context;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class VolumeBarWindow extends PopupWindow {

    private Context nContext;
    private AudioManager mAudioManager = null;

    private SeekBar mSeekBar;
    private ImageView mMuteBtn;

    public static VolumeBarWindow createVolumeBarWindow(Context context) {
        LinearLayout volumeBar = (LinearLayout) LayoutInflater.from(context).inflate(
                R.layout.volumebar, null);
        int width = context.getResources().getDimensionPixelOffset(R.dimen.volumebar_width);
        return new VolumeBarWindow(volumeBar, width, LayoutParams.WRAP_CONTENT, true);
    }

    public void show(View parent, int gravity) {
        int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mSeekBar.setProgress(volume);
        if (isMuted()) {
            mMuteBtn.setImageResource(R.drawable.mute);
        } else {
            mMuteBtn.setImageResource(R.drawable.volume);
        }
        update();
        showAtLocation(parent, gravity, 0, 0);
    }

    private VolumeBarWindow(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
        nContext = contentView.getContext();
        setOutsideTouchable(true);
        setBackgroundDrawable(nContext.getResources().getDrawable(android.R.color.transparent));
        setupVolumeBar(contentView);
    }

    private void setupVolumeBar(View contentView) {
        mAudioManager = (AudioManager) nContext.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        mSeekBar = (SeekBar) contentView.findViewById(R.id.seekBar);
        mSeekBar.setMax(maxVolume);
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(), 0);
            }
        });

        mMuteBtn = (ImageView) contentView.findViewById(R.id.mute_btn);
        mMuteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMuted()) {
                    mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                    mMuteBtn.setImageResource(R.drawable.volume);
                } else {
                    mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                    mMuteBtn.setImageResource(R.drawable.mute);
                }
                update();
            }
        });
    }

    private boolean isMuted() {
        int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        return volume > 0 ? false : true;
    }

}
