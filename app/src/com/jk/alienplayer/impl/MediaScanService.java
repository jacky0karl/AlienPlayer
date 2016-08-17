package com.jk.alienplayer.impl;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

public class MediaScanService extends Service {
    public static final String SCAN_COMMAND = "scan_command";
    public static final int SCAN_ALL = 0;
    public static final int SCAN_FILE = 1;
    public static final int SCAN_FILES = 2;

    public static final String ACTION_MEDIA_SCAN_COMPLETED = "com.jk.alienplayer.MEDIA_SCAN_COMPLETED";
    public static final String FILE_PATH = "file_path";

    private int mAction = -1;
    private String mFilePath = null;
    private ArrayList<String> mFilePathList = null;
    private MediaScannerConnection mConnection = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_NOT_STICKY;
        }

        mAction = intent.getIntExtra(SCAN_COMMAND, -1);
        Log.d("#### MediaScanService", "action = " + mAction);
        switch (mAction) {
            case SCAN_ALL:
                break;
            case SCAN_FILE:
                mFilePath = intent.getStringExtra(FILE_PATH);
                if (!TextUtils.isEmpty(mFilePath)) {
                    mConnection = new MediaScannerConnection(this, mMediaScannerConnectionClient);
                    mConnection.connect();
                }
                break;
            case SCAN_FILES:
                mFilePathList = intent.getStringArrayListExtra(FILE_PATH);
                if (mFilePathList != null) {
                    mConnection = new MediaScannerConnection(this, mMediaScannerConnectionClient);
                    mConnection.connect();
                }
                break;
            default:
                break;
        }
        return START_NOT_STICKY;
    }

    private MediaScannerConnectionClient mMediaScannerConnectionClient = new MediaScannerConnectionClient() {
        @Override
        public void onMediaScannerConnected() {
            if (mAction == SCAN_FILE) {
                mConnection.scanFile(mFilePath, "audio/mpeg");
            } else {
                if (mFilePathList.size() > 0) {
                    // record the last file
                    mFilePath = mFilePathList.get(mFilePathList.size() - 1);
                    for (String path : mFilePathList) {
                        mConnection.scanFile(path, "audio/mpeg");
                    }
                }
            }
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            // wait for the last file
            if (mFilePath.equals(path)) {
                mConnection.disconnect();
                Intent intent = new Intent(ACTION_MEDIA_SCAN_COMPLETED);
                intent.putExtra(FILE_PATH, path);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                stopSelf();
            }
        }
    };

    public static void startScan(Context context, String filePath) {
        Intent intent = new Intent(context, MediaScanService.class);
        intent.putExtra(MediaScanService.SCAN_COMMAND, MediaScanService.SCAN_FILE);
        intent.putExtra(MediaScanService.FILE_PATH, filePath);
        context.startService(intent);
    }

    public static void startScan(Context context, ArrayList<String> filePathList) {
        Intent intent = new Intent(context, MediaScanService.class);
        intent.putExtra(MediaScanService.SCAN_COMMAND, MediaScanService.SCAN_FILES);
        intent.putStringArrayListExtra(MediaScanService.FILE_PATH, filePathList);
        context.startService(intent);
    }

    public static void registerScanReceiver(Context context, BroadcastReceiver receiver) {
        IntentFilter intentFilter = new IntentFilter(ACTION_MEDIA_SCAN_COMPLETED);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, intentFilter);
    }
}
