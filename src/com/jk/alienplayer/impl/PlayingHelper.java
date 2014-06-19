package com.jk.alienplayer.impl;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.audiofx.AudioEffect;
import android.os.Handler;
import android.util.Log;

import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.data.SongInfo;

public class PlayingHelper {

    public static enum PlayStatus {
        Idle, Prepared, Playing, Paused, Stoped
    }

    public interface OnPlayStatusChangedListener {
        void onStart(int duration);

        void onPause();

        void onStop();

        void onProgressUpdate(int progress);
    }

    private static PlayingHelper sSelf = null;
    private MediaPlayer mMediaPlayer;
    PlayStatus mPlayStatus = PlayStatus.Idle;
    private boolean mIsProcessing = false;
    private List<WeakReference<OnPlayStatusChangedListener>> mListenerList;
    private Handler mHandler = new Handler();

    public static synchronized PlayingHelper getInstance() {
        if (sSelf == null) {
            sSelf = new PlayingHelper();
        }
        return sSelf;
    }

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
            mPlayStatus = PlayStatus.Stoped;
            notifyStop();
        }
    };

    private PlayingHelper() {
        mListenerList = new ArrayList<WeakReference<OnPlayStatusChangedListener>>();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.reset();
        mMediaPlayer.setOnErrorListener(mOnErrorListener);
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
    }

    public void registerOnPlayStatusChangedListener(OnPlayStatusChangedListener l) {
        if (l != null) {
            WeakReference<OnPlayStatusChangedListener> wr = new WeakReference<OnPlayStatusChangedListener>(
                    l);
            mListenerList.add(wr);

            if (mPlayStatus == PlayStatus.Playing) {
                l.onStart(mMediaPlayer.getDuration());
            } else if (mPlayStatus == PlayStatus.Paused) {
                l.onPause();
            } else {
                l.onStop();
            }
        }
    }

    public void unregisterOnPlayStatusChangedListener(OnPlayStatusChangedListener l) {
        if (l != null) {
            WeakReference<OnPlayStatusChangedListener> wr = new WeakReference<OnPlayStatusChangedListener>(
                    l);
            mListenerList.remove(wr);
        }
    }

    // FIXME
    WeakReference<PlayService> mPlayServiceWr = null;

    public void openAudioEffect(Context context) {
        mPlayServiceWr = new WeakReference<PlayService>((PlayService) context);
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

    public int getAudioSessionId() {
        return mMediaPlayer.getAudioSessionId();
    }

    public boolean playOrPause() {
        if (mIsProcessing) {
            return mMediaPlayer.isPlaying();
        }
        mIsProcessing = true;

        // first playing
        if (mPlayStatus == PlayStatus.Idle) {
            mIsProcessing = false;
            play();
            return mMediaPlayer.isPlaying();
        }

        // switch play and pause
        try {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mPlayStatus = PlayStatus.Paused;
                notifyPause();
            } else {
                mMediaPlayer.start();
                mPlayStatus = PlayStatus.Playing;
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

        // stop current playing song
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mPlayStatus = PlayStatus.Stoped;
            notifyStop();
        }

        // play the new one
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(info.path);
            mMediaPlayer.prepare();
            notifyTrackChange();
            mMediaPlayer.start();
            mPlayStatus = PlayStatus.Playing;
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

    public void release() {
        try {
            mMediaPlayer.stop();
            notifyStop();
            mMediaPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sSelf = null;
    }

    private void notifyStart() {
        for (WeakReference<OnPlayStatusChangedListener> wr : mListenerList) {
            if (wr.get() != null) {
                wr.get().onStart(mMediaPlayer.getDuration());
            }
        }

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
        for (WeakReference<OnPlayStatusChangedListener> wr : mListenerList) {
            if (wr.get() != null) {
                wr.get().onPause();
            }
        }

        PlayService service = mPlayServiceWr.get();
        if (service != null) {
            service.sendStatusBroadCast(PlayService.ACTION_PAUSE);
        }
        mHandler.removeCallbacks(mUpdateTask);
    }

    private void notifyStop() {
        for (WeakReference<OnPlayStatusChangedListener> wr : mListenerList) {
            if (wr.get() != null) {
                wr.get().onStop();
            }
        }

        PlayService service = mPlayServiceWr.get();
        if (service != null) {
            service.sendStatusBroadCast(PlayService.ACTION_STOP);
        }
        mHandler.removeCallbacks(mUpdateTask);
    }

    private void notifyProgressUpdate() {
        for (WeakReference<OnPlayStatusChangedListener> wr : mListenerList) {
            if (wr.get() != null) {
                wr.get().onProgressUpdate(mMediaPlayer.getCurrentPosition());
            }
        }

        PlayService service = mPlayServiceWr.get();
        if (service != null) {
            service.sendProgressBroadCast(mMediaPlayer.getDuration(),
                    mMediaPlayer.getCurrentPosition());
        }
    }

    Runnable mUpdateTask = new Runnable() {
        @Override
        public void run() {
            notifyProgressUpdate();
            mHandler.postDelayed(mUpdateTask, 500);
        }
    };
}
