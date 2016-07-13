package com.jk.alienplayer;

import android.app.Application;
import android.os.StrictMode;

import com.jk.alienplayer.utils.ImageLoaderUtils;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        setDetection();
        super.onCreate();
        ImageLoaderUtils.initImageLoader(this);
    }

    private void setDetection() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll()
                    .penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        }
    }
}
