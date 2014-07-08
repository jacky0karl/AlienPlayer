package com.jk.alienplayer.impl;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.audiofx.AudioEffect;
import android.os.Handler;
import android.util.Log;

import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.metadata.SongInfo;

public class PlayingHelper {
    public static final int PROGRESS_UPDATE_INTERVAL = 200;

    public static class PlayingInfo {
        public PlayStatus status = PlayStatus.Idle;
        public int duration = 0;
        public int progress = 0;
    }

    public static enum PlayStatus {
        Idle, Prepared, Playing, Paused, Stoped
    }

    private static PlayingInfo sPlayingInfo = new PlayingInfo();
    private static int sAudioSessionId = 0;

    private MediaPlayer mMediaPlayer;
    private boolean mIsProcessing = false;
    private WeakReference<PlayService> mPlayServiceWr = null;
    private Handler mHandler = new Handler();

    OnErrorListener mOnErrorListener = new OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.e("#### OnErrorListener", "error = " + what);
            return false;
        }
    };

    OnCompletionListener mOnCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            sPlayingInfo.status = PlayStatus.Stoped;
            notifyStop();
            repeat();
        }
    };

    public PlayingHelper(PlayService service) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.reset();
        mMediaPlayer.setOnErrorListener(mOnErrorListener);
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);

        sAudioSessionId = mMediaPlayer.getAudioSessionId();
        sPlayingInfo.status = PlayStatus.Idle;

        mPlayServiceWr = new WeakReference<PlayService>(service);
        openAudioEffect(service);
    }

    public static int getAudioSessionId() {
        return sAudioSessionId;
    }

    public static PlayingInfo getPlayingInfo() {
        return sPlayingInfo;
    }

    public void openAudioEffect(Context context) {
        Intent i = new Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
        i.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, sAudioSessionId);
        i.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.getPackageName());
        context.sendBroadcast(i);
    }

    public void closeAudioEffect(Context context) {
        Intent i = new Intent(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION);
        i.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, mMediaPlayer.getAudioSessionId());
        i.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.getPackageName());
        context.sendBroadcast(i);
    }

    public boolean playOrPause() {
        if (mIsProcessing) {
            return mMediaPlayer.isPlaying();
        }
        mIsProcessing = true;

        // first playing
        if (sPlayingInfo.status != PlayStatus.Playing && sPlayingInfo.status != PlayStatus.Paused) {
            mIsProcessing = false;
            play();
            return mMediaPlayer.isPlaying();
        }

        // switch play and pause
        try {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                sPlayingInfo.status = PlayStatus.Paused;
                notifyPause();
            } else {
                mMediaPlayer.start();
                sPlayingInfo.status = PlayStatus.Playing;
                notifyStart();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            mIsProcessing = false;
        }
        return mMediaPlayer.isPlaying();
    }

    public boolean play() {
        SongInfo info = PlayingInfoHolder.getInstance().getCurrentSong();
        if (info == null) {
            return false;
        }
        if (mIsProcessing) {
            return false;
        }
        mIsProcessing = true;

        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(info.path);
            mMediaPlayer.prepare();
            notifyTrackChange();
            mMediaPlayer.start();
            sPlayingInfo.status = PlayStatus.Playing;
            notifyStart();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            mIsProcessing = false;
        }
    }

    public void seekTo(int msec) {
        if (mIsProcessing) {
            return;
        }
        mIsProcessing = true;

        try {
            mMediaPlayer.seekTo(msec);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } finally {
            mIsProcessing = false;
        }
    }

    public void stop() {
        try {
            mMediaPlayer.stop();
            sPlayingInfo.status = PlayStatus.Stoped;
            notifyStop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void release() {
        try {
            mMediaPlayer.stop();
            notifyStop();
            mMediaPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notifyStart() {
        sPlayingInfo.duration = mMediaPlayer.getDuration();
        sPlayingInfo.progress = 0;
        PlayService service = mPlayServiceWr.get();
        if (service != null) {
            service.sendStartBroadCast(mMediaPlayer.getDuration());
        }
        mHandler.removeCallbacks(mUpdateTask);
        mHandler.post(mUpdateTask);
    }

    private void notifyTrackChange() {
        PlayService service = mPlayServiceWr.get();
        if (service != null) {
            SongInfo info = PlayingInfoHolder.getInstance().getCurrentSong();
            service.sendTrackChangeBroadCast(info.title, info.artist);
        }
    }

    private void notifyPause() {
        PlayService service = mPlayServiceWr.get();
        if (service != null) {
            service.sendStatusBroadCast(PlayService.ACTION_PAUSE);
        }
        mHandler.removeCallbacks(mUpdateTask);
    }

    private void notifyStop() {
        sPlayingInfo.progress = 0;
        PlayService service = mPlayServiceWr.get();
        if (service != null) {
            service.sendStatusBroadCast(PlayService.ACTION_STOP);
        }
        mHandler.removeCallbacks(mUpdateTask);
    }

    private void notifyProgressUpdate() {
        sPlayingInfo.progress = mMediaPlayer.getCurrentPosition();
        PlayService service = mPlayServiceWr.get();
        if (service != null) {
            service.sendProgressBroadCast(mMediaPlayer.getDuration(),
                    mMediaPlayer.getCurrentPosition());
        }
    }

    private Runnable mUpdateTask = new Runnable() {
        @Override
        public void run() {
            notifyProgressUpdate();
            mHandler.postDelayed(mUpdateTask, PROGRESS_UPDATE_INTERVAL);
        }
    };

    private void repeat() {
        PlayService service = mPlayServiceWr.get();
        if (service != null) {
            PlayingInfoHolder.getInstance().next(service);
            play();
        }
    }
}
