package com.jk.alienplayer;

import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.network.FileDownloadingHelper;
import com.jk.alienplayer.utils.ImageLoaderUtils;
import com.jk.alienplayer.utils.UncaughtExceptionLoger;

import android.app.Application;
import android.content.Intent;
import android.os.StrictMode;

public class MainApplication extends Application {
    private static final boolean DEBUG = false;

    @Override
    public void onCreate() {
        setDetection();
        super.onCreate();
        FileDownloadingHelper.getInstance().init(this);
        UncaughtExceptionLoger.getInstance().init();
        PlayingInfoHolder.getInstance().init(this);
        ImageLoaderUtils.initImageLoader(this);
        Intent intent = new Intent(this, PlayService.class);
        startService(intent);
    }

    private void setDetection() {
        if (DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll()
                    .penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        }
    }
}
