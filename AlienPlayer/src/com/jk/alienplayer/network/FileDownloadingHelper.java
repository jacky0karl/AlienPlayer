package com.jk.alienplayer.network;

import android.content.Context;
import android.text.TextUtils;

import com.jk.alienplayer.data.JsonHelper;
import com.jk.alienplayer.data.Mp3TagsHelper;
import com.jk.alienplayer.impl.MediaScanService;
import com.jk.alienplayer.metadata.FileDownloadingInfo;
import com.jk.alienplayer.metadata.FileDownloadingInfo.Status;
import com.jk.alienplayer.metadata.NetworkTrackInfo;
import com.jk.alienplayer.network.HttpHelper.HttpResponseHandler;
import com.jk.alienplayer.utils.FileSavingUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileDownloadingHelper {
    public static final String LYRIC_EXT = "lyr";
    private static final int MAX_TASK_COUNT = 3;
    private static final int STEP_SIZE = 4 * 1024;

    private static FileDownloadingHelper sSelf = null;
    private ExecutorService mExecutor;
    private List<FileDownloadingInfo> mFileDownloadingList;
    private Context mContext = null;

    public static synchronized FileDownloadingHelper getInstance() {
        if (sSelf == null) {
            sSelf = new FileDownloadingHelper();
        }
        return sSelf;
    }

    private FileDownloadingHelper() {
        mExecutor = Executors.newFixedThreadPool(MAX_TASK_COUNT);
        mFileDownloadingList = new ArrayList<FileDownloadingInfo>();
    }

    public void init(Context context) {
        mContext = context;
        FileSavingUtils.setupRootPath(mContext);
    }

    public void requstDownloadTrack(NetworkTrackInfo trackInfo, String url) {
        if (trackInfo == null || TextUtils.isEmpty(url)) {
            return;
        }

        FileDownloadingInfo info = new FileDownloadingInfo(trackInfo);
        info.url = url;
        mFileDownloadingList.add(info);
        DownloadTask task = new DownloadTask(info);
        mExecutor.execute(task);
    }

    public void retryDownloadTrack(FileDownloadingInfo info) {
        if (info == null) {
            return;
        }

        if (info.status == Status.CANCELED || info.status == Status.FAILED) {
            info.status = Status.PENDING;
            info.progress = 0;
            DownloadTask task = new DownloadTask(info);
            mExecutor.execute(task);
        }
    }

    public void abortDownloadTrack(FileDownloadingInfo info) {
        if (info == null) {
            return;
        }

        if (info.status == Status.PENDING || info.status == Status.DOWALOADING) {
            info.status = Status.CANCELED;
        }
    }

    public List<FileDownloadingInfo> getFileDownloadingList() {
        return mFileDownloadingList;
    }

    public boolean isAnyTaskUpdating() {
        for (FileDownloadingInfo info : mFileDownloadingList) {
            if (info.status == Status.DOWALOADING) {
                return true;
            }
        }
        return false;
    }

    public void clearDone() {
        List<FileDownloadingInfo> tmpList = new ArrayList<FileDownloadingInfo>();
        for (FileDownloadingInfo info : mFileDownloadingList) {
            if (info.status != Status.COMPLETED) {
                tmpList.add(info);
            }
        }
        mFileDownloadingList = tmpList;
    }

    public void removeRecord(FileDownloadingInfo info) {
        mFileDownloadingList.remove(info);
    }

    private class DownloadTask implements Runnable {
        private FileDownloadingInfo info;

        public DownloadTask(FileDownloadingInfo info) {
            this.info = info;
        }

        @Override
        public void run() {
            if (info.status == Status.CANCELED) {
                return;
            }

            downloadTrack(info);
            downloadLyric(info);
        }
    };

    private void downloadLyric(final FileDownloadingInfo info) {
        if (info.status == Status.CANCELED) {
            return;
        }

        HttpResponseHandler handler = new HttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                if (info.status == Status.CANCELED) {
                    return;
                }

                String lyric = JsonHelper.parseLyric(response);
                if (!TextUtils.isEmpty(lyric)) {
                    saveLyric(info.trackInfo, lyric);
                }
            }

            @Override
            public void onFail(int status, String response) {
            }
        };
        HttpHelper.getLyric(String.valueOf(info.trackInfo.id), handler);
    }

    private void saveLyric(NetworkTrackInfo info, String lyric) {
        String filePath = buildLyricPath(info);
        try {
            File file = new File(filePath);
            if (FileSavingUtils.ensurePath(file)) {
                file.createNewFile();
            } else {
                return;
            }

            FileWriter filerWriter = new FileWriter(file, false);
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(lyric.toCharArray());
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadTrack(FileDownloadingInfo info) {
        String filePath = buildFilePath(info.trackInfo);
        File file = new File(filePath);
        if (!FileSavingUtils.ensurePath(file)) {
            FileSavingUtils.logToFile("ensurePath fail");
            info.status = FileDownloadingInfo.Status.FAILED;
            return;
        }

        info.status = FileDownloadingInfo.Status.DOWALOADING;
        InputStream inputStream = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(info.url);
            connection = (HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                info.size = connection.getContentLength();
            } else {
                info.status = FileDownloadingInfo.Status.FAILED;
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileSavingUtils.logThrowable(e);
            info.status = FileDownloadingInfo.Status.FAILED;
            return;
        }

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[STEP_SIZE];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                if (info.status == Status.CANCELED) {
                    file.delete();
                    return;
                }
                outputStream.write(buffer, 0, len);
                info.progress += len;
            }
            outputStream.flush();

            if (mContext != null) {
                processDownloadFile(info.trackInfo, filePath);
            }
            info.status = FileDownloadingInfo.Status.COMPLETED;
        } catch (Exception e) {
            e.printStackTrace();
            FileSavingUtils.logThrowable(e);
            info.status = FileDownloadingInfo.Status.FAILED;
        } finally {
            connection.disconnect();
            try {
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                FileSavingUtils.logThrowable(e);
            }
        }
    }

    private void processDownloadFile(NetworkTrackInfo info, final String filePath) {
        if (info.ext.equalsIgnoreCase("mp3")) {
            Mp3TagsHelper.writeMp3Tags(new Mp3TagsHelper.OnMP3AddListener() {
                @Override
                public void onMP3Added() {
                    MediaScanService.startScan(mContext, filePath);
                }
            }, info, filePath);
        }
    }

    private String buildFilePath(NetworkTrackInfo info) {
        return buildPath(info) + info.ext;
    }

    private String buildLyricPath(NetworkTrackInfo info) {
        return buildPath(info) + LYRIC_EXT;
    }

    private String buildPath(NetworkTrackInfo info) {
        StringBuilder sb = new StringBuilder();
        sb.append(FileSavingUtils.sRootPath);
        sb.append(FileSavingUtils.removeIllegalChar(info.artistAlbum));
        sb.append(File.separator);
        sb.append(FileSavingUtils.removeIllegalChar(info.album));
        sb.append(File.separator);
        sb.append(FileSavingUtils.removeIllegalChar(info.name));
        sb.append(".");
        return sb.toString();
    }
}
