package com.jk.alienplayer.impl;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.audiofx.AudioEffect;

import com.jk.alienplayer.data.PlayingInfoHolder;

public class PlayingHelper {

    private static PlayingHelper sSelf = null;
    private MediaPlayer mMediaPlayer;

    public interface PlayingProgressBarListener {
        void onProgressStart(int duration);
    }

    public static synchronized PlayingHelper getInstance() {
        if (sSelf == null) {
            sSelf = new PlayingHelper();
        }
        return sSelf;
    }

    private PlayingHelper() {
        mMediaPlayer = new MediaPlayer();
    }

    public void openAudioEffect(Context context) {
        Intent i = new Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
        i.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, mMediaPlayer.getAudioSessionId());
        i.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.getPackageName());
        context.sendBroadcast(i);
    }

    public void closeAudioEffect(Context context) {
        Intent i = new Intent(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION);
        i.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, mMediaPlayer.getAudioSessionId());
        i.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.getPackageName());
        context.sendBroadcast(i);
    }

    public void setOnCompletionListener(OnCompletionListener listener) {
        mMediaPlayer.setOnCompletionListener(listener);
    }

    public int getAudioSessionId() {
        return mMediaPlayer.getAudioSessionId();
    }

    public boolean playOrPause(PlayingProgressBarListener listener) {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        } else {
            try {
                mMediaPlayer.start();
                if (mMediaPlayer.isPlaying()) {
                    if (listener != null) {
                        listener.onProgressStart(getDuration());
                    }
                } else {
                    play(listener);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return mMediaPlayer.isPlaying();
    }

    public boolean play(PlayingProgressBarListener listener) {
        if (PlayingInfoHolder.getInstance().getCurrentSong() == null) {
            return false;
        }

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }

        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(PlayingInfoHolder.getInstance().getCurrentSong().path);
            mMediaPlayer.prepare();
            if (listener != null) {
                listener.onProgressStart(getDuration());
            }
            mMediaPlayer.start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public void seekTo(int msec) {
        try {
            mMediaPlayer.seekTo(msec);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void release() {
        try {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sSelf = null;
    }
}
