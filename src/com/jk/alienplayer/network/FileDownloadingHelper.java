package com.jk.alienplayer.network;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jk.alienplayer.data.JsonHelper;
import com.jk.alienplayer.data.Mp3TagsHelper;
import com.jk.alienplayer.impl.MediaScanService;
import com.jk.alienplayer.metadata.FileDownloadingInfo;
import com.jk.alienplayer.metadata.FileDownloadingInfo.Status;
import com.jk.alienplayer.metadata.NetworkTrackInfo;
import com.jk.alienplayer.network.HttpHelper.HttpResponseHandler;
import com.jk.alienplayer.utils.FileSavingUtils;

import android.content.Context;
import android.text.TextUtils;

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
        FileDownloadingInfo info = new FileDownloadingInfo(trackInfo);
        mFileDownloadingList.add(info);
        DownloadTask task = new DownloadTask(info, url);
        mExecutor.execute(task);
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

    public InputStream getInputStream(String urlString) {
        InputStream inputStream = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            inputStream = urlConnection.getInputStream();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    public boolean saveFile(String filePath, InputStream is) {
        if (is == null) {
            return false;
        }
        if (!android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return false;
        }

        File file = new File(filePath);
        FileSavingUtils.ensurePath(file);
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[STEP_SIZE];
            int len;
            while ((len = is.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
            outputStream.close();
            is.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private class DownloadTask implements Runnable {
        private FileDownloadingInfo info;
        private String url;

        public DownloadTask(FileDownloadingInfo info, String url) {
            this.info = info;
            this.url = url;
        }

        @Override
        public void run() {
            downloadTrack(info, url);
            downloadLyric(info.trackInfo);
        }
    };

    private void downloadLyric(final NetworkTrackInfo info) {
        HttpResponseHandler handler = new HttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                String lyric = JsonHelper.parseLyric(response);
                if (!TextUtils.isEmpty(lyric)) {
                    saveLyric(info, lyric);
                }
            }

            @Override
            public void onFail(int status, String response) {
            }
        };
        HttpHelper.getLyric(String.valueOf(info.id), handler);
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

    private void downloadTrack(FileDownloadingInfo info, String urlString) {
        String filePath = buildFilePath(info.trackInfo);
        File file = new File(filePath);
        if (!FileSavingUtils.ensurePath(file)) {
            FileSavingUtils.logToFile("ensurePath fail");
            info.status = FileDownloadingInfo.Status.FAILED;
            return;
        }

        info.status = FileDownloadingInfo.Status.DOWALOADING;
        InputStream inputStream = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            inputStream = urlConnection.getInputStream();
            info.size = urlConnection.getContentLength();
        } catch (Exception e) {
            e.printStackTrace();
            FileSavingUtils.logThrowable(e);
            info.status = FileDownloadingInfo.Status.FAILED;
            return;
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[STEP_SIZE];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
                info.progress += len;
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            if (mContext != null) {
                dealDownloadFile(info.trackInfo, filePath);
            }
            info.status = FileDownloadingInfo.Status.COMPLETED;
        } catch (Exception e) {
            e.printStackTrace();
            FileSavingUtils.logThrowable(e);
            info.status = FileDownloadingInfo.Status.FAILED;
        }
    }

    private void dealDownloadFile(NetworkTrackInfo info, String filePath) {
        if (info.ext.equalsIgnoreCase("mp3")) {
            Mp3TagsHelper.writeMp3Tags(info, filePath);
        }
        MediaScanService.startScan(mContext, filePath);
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
        sb.append(info.artistAlbum);
        sb.append(File.separator);
        sb.append(info.album);
        sb.append(File.separator);
        sb.append(info.name);
        sb.append(".");
        return sb.toString();
    }
}
