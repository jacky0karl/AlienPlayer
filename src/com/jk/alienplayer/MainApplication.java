package com.jk.alienplayer;

import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.network.FileSavingUtil;

import android.app.Application;
import android.content.Intent;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FileSavingUtil.setupRootPath(this);
        PlayingInfoHolder.getInstance().init(this);
        Intent intent = new Intent(this, PlayService.class);
        startService(intent);
    }
}
