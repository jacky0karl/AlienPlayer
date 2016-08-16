package com.jk.alienplayer;

import android.app.Application;
import android.os.StrictMode;

import org.jaudiotagger.tag.TagOptionSingleton;

public class MainApplication extends Application {

    public static Application app = null;

    @Override
    public void onCreate() {
        setDetection();
        super.onCreate();
        app = this;
        TagOptionSingleton.getInstance().setAndroid(true);
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
