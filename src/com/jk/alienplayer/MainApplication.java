package com.jk.alienplayer;

import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.network.FileDownloadingHelper;
import com.jk.alienplayer.utils.ImageLoaderUtils;

import android.app.Application;
import android.content.Intent;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FileDownloadingHelper.getInstance().setupRootPath(this);
        PlayingInfoHolder.getInstance().init(this);
        ImageLoaderUtils.initImageLoader(this);
        Intent intent = new Intent(this, PlayService.class);
        startService(intent);
    }

}
