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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.jk.alienplayer.metadata.NetworkSearchResult;

import android.util.Base64;
import android.util.Log;

public class HttpHelper {

    private static final String COOKIE = "appver=1.7.6;os=Android";
    private static final String KEY = "3go8&$8*3*3h0k(2)2";
    private static final String LIMIT = "20";
    private static final String SEARCH_URL = "http://music.163.com/api/search/get/";
    private static final String GET_ALBUMS_URL = "http://music.163.com/api/artist/albums/";
    private static final String GET_TRACKS_URL = "http://music.163.com/api/album/";
    private static final String TRACK_DOWANLOAD_URL = "http://m1.music.126.net/";

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
                    params.add(new BasicNameValuePair("limit", LIMIT));
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

    public static void getAlbums(final String artistId, final HttpResponseHandler handler) {
        if (handler == null) {
            return;
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpGet get = new HttpGet(GET_ALBUMS_URL + artistId + "?offset=0&limit="
                            + LIMIT);
                    get.setHeader("Cookie", COOKIE);
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpResponse response = httpClient.execute(get);
                    int status = response.getStatusLine().getStatusCode();
                    String responseStr = EntityUtils.toString(response.getEntity(), "utf-8");
                    if (status == 200) {
                        handler.onSuccess(responseStr);
                    } else {
                        handler.onFail(status, responseStr);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                try {
                    HttpGet get = new HttpGet(GET_TRACKS_URL + albumId);
                    get.setHeader("Cookie", COOKIE);
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpResponse response = httpClient.execute(get);
                    int status = response.getStatusLine().getStatusCode();
                    String responseStr = EntityUtils.toString(response.getEntity(), "utf-8");
                    if (status == 200) {
                        handler.onSuccess(responseStr);
                    } else {
                        handler.onFail(status, responseStr);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public static void getTrack(final String trackId, final HttpResponseHandler handler) {
        // if (handler == null) {
        // return;
        // }

        //final String GET_TRACK_URL = "http://music.163.com/api/song/detail/";
        final String GET_TRACK_URL = "http://music.163.com/api/song/detail/?ids=[209932]";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpGet get = new HttpGet(GET_TRACK_URL);
                    get.setHeader("Cookie", COOKIE);
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpResponse response = httpClient.execute(get);
                    int status = response.getStatusLine().getStatusCode();
                    String responseStr = EntityUtils.toString(response.getEntity(), "utf-8");
                    Log.e("dadasdsa", responseStr);
                    if (status == 200) {
                       // handler.onSuccess(responseStr);
                    } else {
                      //  handler.onFail(status, responseStr);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public static String getDownloadTrackUrl(String dfsId) {
        String encryptedId = encrypt(dfsId);
        return TRACK_DOWANLOAD_URL + encryptedId + "/" + dfsId + ".mp3";
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
