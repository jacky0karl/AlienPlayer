package com.jk.alienplayer.presenter;

import com.jk.alienplayer.metadata.NetworkAlbumInfo;
import com.jk.alienplayer.model.AlbumBean;
import com.jk.alienplayer.model.AlbumsBean;
import com.jk.alienplayer.network.SearchService;
import com.jk.alienplayer.network.ServiceHelper;
import com.jk.alienplayer.ui.NetworkAlbumsActivity;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by junjie.qu on 6/24/2016.
 */
public class AlbumsPresenter {

    private NetworkAlbumsActivity mActivity;

    public AlbumsPresenter(NetworkAlbumsActivity activity) {
        mActivity = activity;
    }

    public void fetchAlbums(final String artistId) {
        SearchService service = ServiceHelper.create(SearchService.class);
        service.fetchAlbums(artistId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new AlbumsSubscriber());
    }

    private static List<NetworkAlbumInfo> buildAlbumsResult(List<AlbumBean> list, String artist) {
        List<NetworkAlbumInfo> results = new ArrayList<NetworkAlbumInfo>();
        for (AlbumBean bean : list) {
            NetworkAlbumInfo info = new NetworkAlbumInfo(bean.getId(),
                    bean.getName(), bean.getPicUrl(), artist, bean.getPublishTime());
            results.add(info);
        }
        return results;
    }

    public class AlbumsSubscriber extends Subscriber<AlbumsBean> {

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            if (mActivity != null && !mActivity.isFinishing()) {
                mActivity.fetchAlbumsFail();
            }
        }

        @Override
        public void onNext(AlbumsBean o) {
            if (mActivity == null || mActivity.isFinishing()) {
                return;
            }

            try {
                if (o.getCode() == 200) {
                    List<NetworkAlbumInfo> results = buildAlbumsResult(o.getHotAlbums(), o.getArtist().getName());
                    mActivity.fetchAlbumsSuccess(results);
                } else {
                    mActivity.fetchAlbumsFail();
                }
            } catch (Exception e) {
                mActivity.fetchAlbumsFail();
            }
        }
    }
}
