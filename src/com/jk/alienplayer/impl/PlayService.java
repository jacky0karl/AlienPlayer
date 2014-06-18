package com.jk.alienplayer.impl;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class PlayService extends Service {

    public static final String PLAYING_COMMAND = "playing_command";
    public static final String SEEK_TIME_MSEC = "seek_time_msec";

    public static final int COMMAND_PLAY_PAUSE = 0;
    public static final int COMMAND_PLAY = 1;
    public static final int COMMAND_SEEK = 2;
    public static final int COMMAND_PREV = 3;
    public static final int COMMAND_NEXT = 4;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PlayingHelper.getInstance().openAudioEffect(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }

        int action = intent.getIntExtra(PLAYING_COMMAND, -1);
        switch (action) {
        case COMMAND_PLAY_PAUSE:
            PlayingHelper.getInstance().playOrPause();
            break;
        case COMMAND_PLAY:
            PlayingHelper.getInstance().play();
            break;
        case COMMAND_SEEK:
            int msec = intent.getIntExtra(SEEK_TIME_MSEC, 0);
            PlayingHelper.getInstance().seekTo(msec);
            break;
        case COMMAND_PREV:
            break;
        case COMMAND_NEXT:
            break;
        default:
            break;
        }
        return START_STICKY;
    }

    // private void sendBroadCast() {
    // Intent intent = new Intent(); //Itent就是我们要发送的内容
    // intent.putExtra("data",
    // "this is data from broadcast "+Calendar.getInstance().get(Calendar.SECOND));
    // intent.setAction(flag); //设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
    // sendBroadcast(intent);
    // }

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
