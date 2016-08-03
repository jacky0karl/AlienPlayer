package com.jk.alienplayer.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceHelper {
    private static final String COOKIE = "appver=1.7.6;os=Android";

    public static <T> T create(Class<T> clazz) {
        String baseUrl = "";
        try {
            baseUrl = clazz.getField("BASE_URL").get(null).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpLoggingInterceptor.Level logLevel = HttpLoggingInterceptor.Level.BODY;
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(logLevel))
                .addInterceptor(getRequestHeaders()).build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client).baseUrl(baseUrl)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(clazz);
    }

    private static Interceptor getRequestHeaders() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                request = request.newBuilder().addHeader("Cookie", COOKIE).build();
                return chain.proceed(request);
            }
        };
    }
}
