package com.jk.alienplayer.impl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PlayService extends Service {

    public static final String ACTION = "action";
    public static final int ACTION_PLAY_PAUSE = 0;
    public static final int ACTION_PREV = 1;
    public static final int ACTION_NEXT = 2;

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

        int action = intent.getIntExtra(ACTION, -1);
        switch (action) {
        case ACTION_PLAY_PAUSE:
            PlayingHelper.getInstance().playOrPause(null);
            break;
        case ACTION_PREV:
            break;
        case ACTION_NEXT:
            break;
        default:
            break;
        }
        return START_STICKY;
    }

}
