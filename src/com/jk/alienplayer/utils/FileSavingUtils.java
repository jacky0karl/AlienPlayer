package com.jk.alienplayer.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;

public class FileSavingUtils {
    private static final int LOG_SIZE = 1024 * 512;

    public static String sRootPath;
    private static String sLogPath;
    private static String slogFile = "Log.txt";
    private static SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void setupRootPath(Context context) {
        StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        String systemPath = null;
        try {
            String[] paths = (String[]) sm.getClass().getMethod("getVolumePaths", null)
                    .invoke(sm, null);
            for (int i = 0; i < paths.length; i++) {
                String status = (String) sm.getClass().getMethod("getVolumeState", String.class)
                        .invoke(sm, paths[i]);
                if (status.equals(android.os.Environment.MEDIA_MOUNTED)) {
                    systemPath = paths[i];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            systemPath = Environment.getExternalStorageDirectory().getPath();
        }

        sRootPath = systemPath + File.separator + "AlienPlayer" + File.separator;
        sLogPath = sRootPath + "Log" + File.separator;
    }

    public static void logToFile(String content) {
        Date nowtime = new Date();
        String needWriteMessage = sDateFormat.format(nowtime) + ":" + content;

        try {
            File file = new File(sLogPath, slogFile);
            if (!file.exists()) {
                ensurePath(file);
                file.createNewFile();
            }

            FileWriter filerWriter = new FileWriter(file, true);
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(needWriteMessage);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();

            FileInputStream fis = new FileInputStream(file);
            if (fis.available() > LOG_SIZE) {
                file.delete();
            }
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logThrowable(Throwable t) {
        if (t == null) {
            return;
        }

        logToFile("Exception: " + t.getClass().getName());
        logToFile("Message: " + t.getMessage());
        StackTraceElement[] array = t.getStackTrace();
        for (StackTraceElement ste : array) {
            logToFile("  " + ste.getClassName() + "." + ste.getMethodName() + "("
                    + ste.getFileName() + ":" + ste.getLineNumber() + ")");
        }

        // recursive call if it has cause
        logThrowable(t.getCause());
    }

    public static synchronized boolean ensurePath(File file) {
        String filePath = file.getParent();
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.exists();
    }
}
