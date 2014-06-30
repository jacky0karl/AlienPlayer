package com.jk.alienplayer.utils;

import android.content.Context;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ImageLoaderUtils {
    private static final int MAX_DISK_CACHE = 1024 * 1024 * 64;

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .diskCacheSize(MAX_DISK_CACHE).build();
        ImageLoader.getInstance().init(config);
    }

    public static DisplayImageOptions sOptions = new DisplayImageOptions.Builder()
            .cacheOnDisk(true).build();
}
