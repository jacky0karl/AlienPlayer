package com.jk.alienplayer.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.jk.alienplayer.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ImageLoaderUtils {
    private static final int MAX_MEMORY_CACHE = 1024 * 1024 * 8;
    private static final int MAX_DISK_CACHE = 1024 * 1024 * 64;

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .diskCacheSize(MAX_DISK_CACHE).memoryCacheSize(MAX_MEMORY_CACHE).build();
        ImageLoader.getInstance().init(config);
    }

    public static DisplayImageOptions sOptions = new DisplayImageOptions.Builder()
            .cacheOnDisk(true).cacheInMemory(true).showImageForEmptyUri(R.drawable.disk)
            .showImageOnFail(R.drawable.disk).showImageOnLoading(R.drawable.disk)
            .bitmapConfig(Bitmap.Config.RGB_565).build();
}
