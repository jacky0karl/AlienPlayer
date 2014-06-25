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

import android.util.Base64;
import android.util.Log;

public class tmp {

    private static final String KEY = "3go8&$8*3*3h0k(2)2";

    void test() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String baseUrl = "http://music.163.com/api/search/get/";

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("type", "1"));
                    params.add(new BasicNameValuePair("limit", "20"));
                    params.add(new BasicNameValuePair("s", "22"));
                    params.add(new BasicNameValuePair("offset", "0"));
                    HttpEntity httpentity = new UrlEncodedFormEntity(params, "utf-8");

                    HttpPost post = new HttpPost(baseUrl);
                    post.setEntity(httpentity);
                    post.setHeader("Cookie", "appver=1.7.6;");
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpResponse response = httpClient.execute(post); // 发起GET请求
                    Log.e("#########", "resCode = " + response.getStatusLine().getStatusCode()); // 获取响应码
                    String htmlResponse = EntityUtils.toString(response.getEntity(), "utf-8");
                    // Log.e("#########", "htmlResponse = " + htmlResponse);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();

        byte[] key = "3go8&$8*3*3h0k(2)2".getBytes();
        byte[] id = "1012650209189889".getBytes();
        for (int i = 0; i < id.length; i++) {
            id[i] = (byte) (id[i] ^ key[i % key.length]);
        }

        try {
            MessageDigest msgDigest = MessageDigest.getInstance("MD5");
            msgDigest.update(id);
            byte[] md5 = msgDigest.digest();
            String base64 = Base64.encodeToString(md5, Base64.DEFAULT);
            String b64 = base64.substring(0, base64.length() - 1);
            b64 = b64.replace('/', '_');
            b64 = b64.replace('+', '-');
            Log.e("#####", "ret = " + b64);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
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

    void test1() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String baseUrl = "http://m1.music.126.net/uCNvR9xHLoQIj1kIRyzadQ==/1012650209189889.mp3";

                    HttpGet get = new HttpGet(baseUrl);

                    get.setHeader("Cookie", "appver=1.5.2;");
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpResponse response = httpClient.execute(get); // 发起GET请求
                    Log.e("#########", "resCode = " + response.getStatusLine().getStatusCode()); // 获取响应码
                    String htmlResponse = EntityUtils.toString(response.getEntity(), "utf-8");
                    Log.e("#########", "htmlResponse = " + htmlResponse);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();

    }

}
