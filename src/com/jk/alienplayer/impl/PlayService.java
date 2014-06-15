package com.jk.alienplayer.impl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PlayService extends Service {

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
        return super.onStartCommand(intent, flags, startId);
    }

    
}
