package com.jk.alienplayer.network;

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
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.jk.alienplayer.metadata.NetworkSearchResult;

import android.util.Base64;

public class HttpHelper {

    private static final String COOKIE = "appver=1.7.6;os=Android";
    private static final String KEY = "3go8&$8*3*3h0k(2)2";
    private static final String SEARCH_LIMIT = "50";
    private static final String ALBUM_LIMIT = "100";
    private static final String SEARCH_URL = "http://music.163.com/api/search/get/";
    private static final String GET_ALBUMS_URL = "http://music.163.com/api/artist/albums/";
    private static final String GET_TRACKS_URL = "http://music.163.com/api/album/";
    private static final String GET_TRACK_URL = "http://music.163.com/api/song/detail/";
    private static final String TRACK_DOWANLOAD_URL = "http://m1.music.126.net/";
    private static final String GET_LYRIC_URL = "http://music.163.com/api/song/lyric?id=";

    public interface HttpResponseHandler {
        void onSuccess(String response);

        void onFail(int status, String response);
    }

    public static void search(final int type, final String key, final HttpResponseHandler handler) {
        if (handler == null) {
            return;
        }

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
                    params.add(new BasicNameValuePair("limit", SEARCH_LIMIT));
                    params.add(new BasicNameValuePair("s", key));
                    params.add(new BasicNameValuePair("offset", "0"));
                    HttpEntity httpentity = new UrlEncodedFormEntity(params, "utf-8");
                    HttpPost post = new HttpPost(SEARCH_URL);
                    post.setEntity(httpentity);
                    handleRequest(post, handler);
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.onFail(-1, e.getMessage());
                }
            }
        });
        thread.start();
    }

    public static void getAlbums(final String artistId, final HttpResponseHandler handler) {
        if (handler == null) {
            return;
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGet get = new HttpGet(GET_ALBUMS_URL + artistId + "?offset=0&limit="
                        + ALBUM_LIMIT);
                handleRequest(get, handler);
            }
        });
        thread.start();
    }

    public static void getTracks(final String albumId, final HttpResponseHandler handler) {
        if (handler == null) {
            return;
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGet get = new HttpGet(GET_TRACKS_URL + albumId);
                handleRequest(get, handler);
            }
        });
        thread.start();
    }

    public static void getTrack(final String trackId, final HttpResponseHandler handler) {
        if (handler == null) {
            return;
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String url = GET_TRACK_URL + "?ids=[" + trackId + "]";
                HttpGet get = new HttpGet(url);
                handleRequest(get, handler);
            }
        });
        thread.start();
    }

    public static String getDownloadTrackUrl(String dfsId, String ext) {
        String encryptedId = encrypt(dfsId);
        return TRACK_DOWANLOAD_URL + encryptedId + "/" + dfsId + "." + ext;
    }

    public static void getLyric(final String trackId, final HttpResponseHandler handler) {
        if (handler == null) {
            return;
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String url = GET_LYRIC_URL + trackId + "&lv=-1";
                HttpGet get = new HttpGet(url);
                handleRequest(get, handler);
            }
        });
        thread.start();
    }

    private static void handleRequest(HttpUriRequest request, HttpResponseHandler handler) {
        try {
            request.setHeader("Cookie", COOKIE);
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            int status = response.getStatusLine().getStatusCode();
            String responseStr = EntityUtils.toString(response.getEntity(), "utf-8");
            if (status == 200) {
                handler.onSuccess(responseStr);
            } else {
                handler.onFail(status, responseStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            handler.onFail(-1, e.getMessage());
        }
    }

    private static String encrypt(String str) {
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
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}
