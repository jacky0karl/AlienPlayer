package com.jk.alienplayer;

import com.jk.alienplayer.impl.PlayService;

import android.app.Application;
import android.content.Intent;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent = new Intent(this, PlayService.class);
        startService(intent);
    }
}
