package com.jk.alienplayer.network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WebFileSavingUtil {
    private static final int STEP_SIZE = 4 * 1024;

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
