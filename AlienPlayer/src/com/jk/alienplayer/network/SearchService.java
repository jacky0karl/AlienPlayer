package com.jk.alienplayer.network;

import com.jk.alienplayer.model.AlbumsBean;
import com.jk.alienplayer.model.SearchBean;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface SearchService {
    public static final String BASE_URL = "http://music.163.com/api/";
    public static final String ALBUM_LIMIT = "100";

    @POST("search/get")
    Call<SearchBean> search(@QueryMap Map<String, String> params);

    @GET("artist/albums/{artistId}?offset=0&limit=" + ALBUM_LIMIT)
    Observable<AlbumsBean> fetchAlbums(@Path("artistId") String artistId);
}
