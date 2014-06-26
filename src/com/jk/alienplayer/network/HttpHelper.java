package com.jk.alienplayer.network;

import java.io.File;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.jk.alienplayer.metadata.NetworkSearchResult;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

public class HttpHelper {
    public static final String ROOT_PATH = Environment.getExternalStorageDirectory().getPath()
            + File.separator + "AlienPlayer" + File.separator;

    private static final String COOKIE = "appver=1.7.6;";
    private static final String KEY = "3go8&$8*3*3h0k(2)2";
    private static final String SEARCH_URL = "http://music.163.com/api/search/get/";

    private Context mContext;

    public interface HttpResponseHandler {
        void onSuccess(String response);

        void onFail(int status, String response);
    }

    public HttpHelper(Context context) {
        mContext = context;
    }

    public void search(final int type, final String key, final HttpResponseHandler handler) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String typeStr = null;
                    if (type == NetworkSearchResult.TYPE_ARTISTS) {
                        typeStr = "100";
                    } else if (type == NetworkSearchResult.TYPE_ALBUMS) {
                        typeStr = "10";
                    } else {
                        typeStr = "1";
                    }
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("type", typeStr));
                    params.add(new BasicNameValuePair("limit", "20"));
                    params.add(new BasicNameValuePair("s", key));
                    params.add(new BasicNameValuePair("offset", "0"));
                    HttpEntity httpentity = new UrlEncodedFormEntity(params, "utf-8");
                    HttpPost post = new HttpPost(SEARCH_URL);
                    post.setEntity(httpentity);
                    post.setHeader("Cookie", COOKIE);
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpResponse response = httpClient.execute(post);
                    int status = response.getStatusLine().getStatusCode();
                    String responseStr = EntityUtils.toString(response.getEntity(), "utf-8");
                    if (status == 200) {
                        handler.onSuccess(responseStr);
                    } else {
                        handler.onFail(status, responseStr);
                    }
                } catch (Exception e) {
                    handler.onFail(0, "exception");
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void getAlbums(final String artistId) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String baseUrl = "http://music.163.com/api/artist/albums/";

                    HttpGet get = new HttpGet(baseUrl + artistId + "?offset=0&limit=20");
                    get.setHeader("Cookie", COOKIE);
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpResponse response = httpClient.execute(get);
                    Log.e("#########", "resCode = " + response.getStatusLine().getStatusCode());
                    String htmlResponse = EntityUtils.toString(response.getEntity(), "utf-8");
                    Log.e("#########", "htmlResponse = " + htmlResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void getTracks(final String albumId) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String baseUrl = "http://music.163.com/api/album/";

                    HttpGet get = new HttpGet(baseUrl + albumId);
                    get.setHeader("Cookie", COOKIE);
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpResponse response = httpClient.execute(get);
                    Log.e("#########", "resCode = " + response.getStatusLine().getStatusCode());
                    String htmlResponse = EntityUtils.toString(response.getEntity(), "utf-8");
                    Log.e("#########", "htmlResponse = " + htmlResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    private String encrypt(String str) {
        byte[] key = KEY.getBytes();
        byte[] src = str.getBytes();

        for (int i = 0; i < src.length; i++) {
            src[i] = (byte) (src[i] ^ key[i % key.length]);
        }

        try {
            MessageDigest msgDigest = MessageDigest.getInstance("MD5");
            msgDigest.update(src);
            byte[] md5 = msgDigest.digest();
            String base64 = Base64.encodeToString(md5, Base64.DEFAULT);
            String result = base64.substring(0, base64.length() - 1);
            result = result.replace('/', '_');
            result = result.replace('+', '-');
            Log.e("#####", "ret = " + result);
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void downloadTrack(final String dfsId) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String encryptedId = encrypt(dfsId);
                    String baseUrl = "http://m1.music.126.net/" + encryptedId + "/" + dfsId
                            + ".mp3";

                    InputStream is = WebFileSavingUtil.getInputStream(baseUrl);
                    if (WebFileSavingUtil.saveFile(ROOT_PATH + "jk1.mp3", is)) {
                        Log.e("getTrack", "OK");
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("getTrack", "NOK");
            }
        });
        t.start();
    }
}
