package com.jk.alienplayer.impl;

import com.jk.alienplayer.data.PlayingInfoHolder;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.audiofx.AudioEffect;
import android.os.IBinder;
import android.view.KeyEvent;

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

    public static final String ACTION_START = "com.jk.alienplayer.start";
    public static final String ACTION_PAUSE = "com.jk.alienplayer.pause";
    public static final String ACTION_STOP = "com.jk.alienplayer.stop";
    public static final String ACTION_PROGRESS_UPDATE = "com.jk.alienplayer.progress_update";
    public static final String ACTION_TRACK_CHANGE = "com.jk.alienplayer.track_change";

    private static final int MEDIA_BUTTON_PRIORITY = 999;
    private PlayingHelper mPlayingHelper;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
                KeyEvent key = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                if (handleMediaButton(key)) {
                    abortBroadcast();
                }
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayingHelper = new PlayingHelper(this);
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
        intentFilter.setPriority(MEDIA_BUTTON_PRIORITY);
        registerReceiver(mReceiver, intentFilter);
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
        default:
            break;
        }
        return START_STICKY;
    }

    private boolean handleMediaButton(KeyEvent key) {
        if (key.getAction() == KeyEvent.ACTION_UP) {
            switch (key.getKeyCode()) {
            case KeyEvent.KEYCODE_MEDIA_PLAY:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                mPlayingHelper.playOrPause();
                return true;
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                PlayingInfoHolder.getInstance().next(PlayService.this);
                mPlayingHelper.play();
                return true;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                PlayingInfoHolder.getInstance().prev(PlayService.this);
                mPlayingHelper.play();
                return true;
            default:
                return false;
            }
        } else {
            // Any Broadcast about those key events should be aborted
            int code = key.getKeyCode();
            if (code == KeyEvent.KEYCODE_MEDIA_PLAY || code == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                    || code == KeyEvent.KEYCODE_MEDIA_NEXT
                    || code == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
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
