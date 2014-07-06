package com.jk.alienplayer.impl;

import com.jk.alienplayer.MediaButtonReceiver;
import com.jk.alienplayer.data.PlayingInfoHolder;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.audiofx.AudioEffect;
import android.os.IBinder;

public class PlayService extends Service {
    public static final String BROADCAST_ACTION = "broadCast_action";
    public static final String PLAYING_COMMAND = "playing_command";
    public static final String SEEK_TIME_MSEC = "seek_time_msec";
    public static final String TOTAL_DURATION = "total_duration";
    public static final String CURRENT_DURATION = "current_duration";
    public static final String SONG_NAME = "song_name";
    public static final String ARTIST_NAME = "artist_name";

    public static final int COMMAND_PLAY_PAUSE = 0;
    public static final int COMMAND_PLAY = 1;
    public static final int COMMAND_SEEK = 2;
    public static final int COMMAND_PREV = 3;
    public static final int COMMAND_NEXT = 4;
    public static final int COMMAND_EXIT = 5;

    public static final String ACTION_START = "com.jk.alienplayer.start";
    public static final String ACTION_PAUSE = "com.jk.alienplayer.pause";
    public static final String ACTION_STOP = "com.jk.alienplayer.stop";
    public static final String ACTION_EXIT = "com.jk.alienplayer.EXIT";
    public static final String ACTION_PROGRESS_UPDATE = "com.jk.alienplayer.progress_update";
    public static final String ACTION_TRACK_CHANGE = "com.jk.alienplayer.track_change";

    private PlayingHelper mPlayingHelper;
    private AudioManager mAudioManager;
    ComponentName mMediaButtonReceiver = new ComponentName(MediaButtonReceiver.class.getPackage()
            .getName(), MediaButtonReceiver.class.getName());

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayingHelper = new PlayingHelper(this);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.registerMediaButtonEventReceiver(mMediaButtonReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }

        int action = intent.getIntExtra(PLAYING_COMMAND, -1);
        switch (action) {
        case COMMAND_PLAY_PAUSE:
            mPlayingHelper.playOrPause();
            break;
        case COMMAND_PLAY:
            mPlayingHelper.play();
            break;
        case COMMAND_SEEK:
            int msec = intent.getIntExtra(SEEK_TIME_MSEC, 0);
            mPlayingHelper.seekTo(msec);
            break;
        case COMMAND_PREV:
            PlayingInfoHolder.getInstance().prev(this);
            mPlayingHelper.play();
            break;
        case COMMAND_NEXT:
            PlayingInfoHolder.getInstance().next(this);
            mPlayingHelper.play();
            break;
        case COMMAND_EXIT:
            stopSelf();
            break;
        default:
            break;
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mAudioManager.unregisterMediaButtonEventReceiver(mMediaButtonReceiver);
        mPlayingHelper.release();
        sendStatusBroadCast(ACTION_EXIT);
        super.onDestroy();
    }

    public void sendStatusBroadCast(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        sendBroadcast(intent);
    }

    public void sendStartBroadCast(int duration) {
        Intent intent = new Intent();
        intent.putExtra(TOTAL_DURATION, duration);
        intent.setAction(ACTION_START);
        sendBroadcast(intent);
    }

    public void sendTrackChangeBroadCast(String song, String artist) {
        Intent intent = new Intent();
        intent.setAction(ACTION_TRACK_CHANGE);
        intent.putExtra(SONG_NAME, song);
        intent.putExtra(ARTIST_NAME, artist);
        sendBroadcast(intent);
    }

    public void sendProgressBroadCast(int duration, int progress) {
        Intent intent = new Intent();
        intent.putExtra(TOTAL_DURATION, duration);
        intent.putExtra(CURRENT_DURATION, progress);
        intent.setAction(ACTION_PROGRESS_UPDATE);
        sendBroadcast(intent);
    }

    public static void registerReceiver(Context context, BroadcastReceiver receiver) {
        IntentFilter intentFilter = new IntentFilter(ACTION_TRACK_CHANGE);
        intentFilter.addAction(ACTION_START);
        intentFilter.addAction(ACTION_PAUSE);
        intentFilter.addAction(ACTION_STOP);
        intentFilter.addAction(ACTION_PROGRESS_UPDATE);
        context.registerReceiver(receiver, intentFilter);
    }

    public static Intent getDisplayAudioEffectIntent(Context context) {
        Intent intent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
        intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, PlayingHelper.getAudioSessionId());
        intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.getPackageName());
        return intent;
    }

    public static Intent getPlayingCommandIntent(Context context, int command) {
        Intent intent = new Intent(context, PlayService.class);
        intent.putExtra(PlayService.PLAYING_COMMAND, command);
        return intent;
    }

    public static Intent getSeekIntent(Context context, int msec) {
        Intent intent = new Intent(context, PlayService.class);
        intent.putExtra(PlayService.PLAYING_COMMAND, COMMAND_SEEK);
        intent.putExtra(SEEK_TIME_MSEC, msec);
        return intent;
    }
}
