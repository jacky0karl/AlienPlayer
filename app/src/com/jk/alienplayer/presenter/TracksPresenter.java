package com.jk.alienplayer.presenter;

import com.jk.alienplayer.model.TracksBean;
import com.jk.alienplayer.network.HttpHelper;
import com.jk.alienplayer.network.SearchService;
import com.jk.alienplayer.network.ServiceHelper;
import com.jk.alienplayer.ui.NetworkTracksActivity;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by junjie.qu on 6/24/2016.
 */
public class TracksPresenter {

    private NetworkTracksActivity mActivity;

    public TracksPresenter(NetworkTracksActivity activity) {
        mActivity = activity;
    }

    public void fetchTracks(final long albumId) {
        SearchService service = ServiceHelper.create(SearchService.class);
        service.fetchTracks(albumId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new TracksSubscriber());
    }

    public class TracksSubscriber extends Subscriber<TracksBean> {

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            if (mActivity != null && !mActivity.isFinishing()) {
                mActivity.fetchTracksFail();
            }
        }

        @Override
        public void onNext(TracksBean o) {
            if (mActivity == null || mActivity.isFinishing()) {
                return;
            }

            try {
                if (o.getCode() == HttpHelper.HTTP_OK) {
                    mActivity.fetchTracksSuccess(o.getAlbum().getSongs());
                } else {
                    mActivity.fetchTracksFail();
                }
            } catch (Exception e) {
                mActivity.fetchTracksFail();
            }
        }
    }
}
