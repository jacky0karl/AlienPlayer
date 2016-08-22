package com.jk.alienplayer.presenter.artistdetail;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.data.PlaylistHelper;
import com.jk.alienplayer.metadata.SongInfo;
import com.jk.alienplayer.ui.artistdetail.SongsActivity;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SongsActivityPresenter {
    private SongsActivity mView;

    public SongsActivityPresenter(SongsActivity view) {
        mView = view;
    }

    public void updateSongList(final long playlistId, final Intent data) {
        if (mView == null) {
            return;
        }

        Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                List<SongInfo> songs = new ArrayList<>();
                if (data.getData() != null) {
                    songs.addAll(DatabaseHelper.getTracks(mView, data.getData()));
                } else {
                    ClipData clipdata = data.getClipData();
                    for (int i = 0; i < clipdata.getItemCount(); i++) {
                        Uri uri = clipdata.getItemAt(i).getUri();
                        songs.addAll(DatabaseHelper.getTracks(mView, uri));
                    }
                }

                PlaylistHelper.addMembersToPlaylist(mView, playlistId, songs);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SongsSubscriber());
    }

    private class SongsSubscriber extends Subscriber<Void> {
        @Override
        public void onCompleted() {
            if (!mView.isFinishing()) {
                mView.updateSongListSucc();
            }
        }

        @Override
        public void onError(Throwable e) {
            Log.e("updateSongList", e.getMessage(), e);
        }

        @Override
        public void onNext(Void aVoid) {
        }
    }
}
