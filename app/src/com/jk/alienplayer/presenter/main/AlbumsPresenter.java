package com.jk.alienplayer.presenter.main;

import android.text.TextUtils;

import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.metadata.AlbumInfo;
import com.jk.alienplayer.ui.main.AlbumsFragment;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AlbumsPresenter {
    private AlbumsFragment mView;

    public AlbumsPresenter(AlbumsFragment view) {
        mView = view;
    }

    public void getAlbums(final String artist) {
        if (mView == null) {
            return;
        }

        Observable.create(new Observable.OnSubscribe<List<AlbumInfo>>() {
            @Override
            public void call(Subscriber<? super List<AlbumInfo>> subscriber) {
                List<AlbumInfo> albums = null;
                if (!TextUtils.isEmpty(artist)) {
                    albums = DatabaseHelper.getAlbums(mView.getActivity(), artist);
                } else {
                    albums = DatabaseHelper.getAllAlbums(mView.getActivity());
                }
                subscriber.onNext(albums);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new AlbumsSubscriber());
    }

    private class AlbumsSubscriber extends Subscriber<List<AlbumInfo>> {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            if (mView.isAdded()) {
                mView.fetchAlbumsFail();
            }
        }

        @Override
        public void onNext(List<AlbumInfo> list) {
            if (mView.isAdded()) {
                mView.fetchAlbumsSucc(list);
            }
        }
    }
}
