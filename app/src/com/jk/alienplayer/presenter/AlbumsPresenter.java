package com.jk.alienplayer.presenter;

import com.jk.alienplayer.model.AlbumsBean;
import com.jk.alienplayer.network.HttpHelper;
import com.jk.alienplayer.network.SearchService;
import com.jk.alienplayer.network.ServiceHelper;
import com.jk.alienplayer.ui.network.NetworkAlbumsActivity;

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

    public void fetchAlbums(final long artistId) {
        SearchService service = ServiceHelper.create(SearchService.class);
        service.fetchAlbums(artistId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new AlbumsSubscriber());
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
                if (o.getCode() == HttpHelper.HTTP_OK) {
                    mActivity.fetchAlbumsSuccess(o.getHotAlbums());
                } else {
                    mActivity.fetchAlbumsFail();
                }
            } catch (Exception e) {
                mActivity.fetchAlbumsFail();
            }
        }
    }
}
