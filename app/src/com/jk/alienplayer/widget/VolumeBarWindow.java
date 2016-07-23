package com.jk.alienplayer.widget;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.VolumeKeyHelper;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
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
    public static final int DISMISS_DELAY = 2000;
    private Context mContext;
    private AudioManager mAudioManager = null;

    private SeekBar mSeekBar;
    private ImageView mMuteBtn;
    private int mLatestVolume;
    private Handler mHandler;

    public static VolumeBarWindow createVolumeBarWindow(Context context) {
        LinearLayout volumeBar = (LinearLayout) LayoutInflater.from(context).inflate(
                R.layout.volumebar, null);
        int width = context.getResources().getDimensionPixelOffset(R.dimen.volumebar_width);
        return new VolumeBarWindow(volumeBar, width, LayoutParams.WRAP_CONTENT, true);
    }

    private VolumeBarWindow(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
        mContext = contentView.getContext();
        mHandler = new Handler();
        setOutsideTouchable(true);
        setBackgroundDrawable(mContext.getResources().getDrawable(android.R.color.transparent));
        setupVolumeBar(contentView);
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
        mHandler.postDelayed(mDismissTask, DISMISS_DELAY);
    }

    private void setupVolumeBar(View contentView) {
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mLatestVolume = maxVolume / 5;

        mSeekBar = (SeekBar) contentView.findViewById(R.id.seekBar);
        mSeekBar.setMax(maxVolume);
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mDismissTask);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int volume = seekBar.getProgress();
                VolumeKeyHelper.setSelfChangeVolume(true);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int volume = seekBar.getProgress();
                if (volume == 0) {
                    mMuteBtn.setImageResource(R.drawable.mute);
                } else {
                    mMuteBtn.setImageResource(R.drawable.volume);
                }
                mHandler.postDelayed(mDismissTask, DISMISS_DELAY);
            }
        });

        mMuteBtn = (ImageView) contentView.findViewById(R.id.mute_btn);
        mMuteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                VolumeKeyHelper.setSelfChangeVolume(true);
                if (isMuted()) {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mLatestVolume, 0);
                    mMuteBtn.setImageResource(R.drawable.volume);
                    mSeekBar.setProgress(mLatestVolume);
                } else {
                    mLatestVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                    mMuteBtn.setImageResource(R.drawable.mute);
                    mSeekBar.setProgress(0);
                }
                update();
            }
        });
    }

    private boolean isMuted() {
        int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        return volume > 0 ? false : true;
    }

    private Runnable mDismissTask = new Runnable() {
        @Override
        public void run() {
            dismiss();
        }
    };
}
