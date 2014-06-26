package com.jk.alienplayer.network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;

public class FileSavingUtil {
    private static final int STEP_SIZE = 4 * 1024;
    public static String sRootPath;

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
    }

    public static InputStream getInputStream(String urlString) {
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

    public static boolean saveFile(String filePath, InputStream is) {
        if (is == null) {
            return false;
        }
        if (!android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return false;
        }

        File file = new File(filePath);
        ensurePath(file);
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

    public static void ensurePath(File file) {
        String filePath = file.getParent();
        File dir = new File(filePath);
        dir.mkdirs();
    }
}
