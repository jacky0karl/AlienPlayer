package com.jk.alienplayer.ui;

import java.util.List;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.JsonHelper;
import com.jk.alienplayer.metadata.NetworkTrackInfo;
import com.jk.alienplayer.network.FileDownloadingHelper;
import com.jk.alienplayer.network.HttpHelper;
import com.jk.alienplayer.network.HttpHelper.HttpResponseHandler;
import com.jk.alienplayer.ui.adapter.NetworkTracksAdapter;
import com.jk.alienplayer.ui.lib.DialogBuilder;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class NetworkTracksActivity extends Activity {
    public static final String LABEL = "label";
    public static final String ALBUM_ID = "album_id";

    private TextView mNoResult;
    private ProgressBar mLoading;
    private ListView mListView;
    private NetworkTracksAdapter mAdapter;
    private long mAlbumId;
    private List<NetworkTrackInfo> mTracks = null;

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            NetworkTrackInfo info = mAdapter.getItem(position);
            if (info != null) {
                downloadTrack(info);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_list);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.network_tracks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_download) {
            if (mTracks != null && mTracks.size() > 0) {
                for (NetworkTrackInfo info : mTracks) {
                    doDownloadTrack(info);
                }
                makeToast();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        setTitle(getIntent().getStringExtra(LABEL));
        mAlbumId = getIntent().getLongExtra(ALBUM_ID, -1);
        mNoResult = (TextView) findViewById(R.id.no_result);
        mLoading = (ProgressBar) findViewById(R.id.loading);
        mListView = (ListView) findViewById(R.id.list);
        mAdapter = new NetworkTracksAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mOnItemClickListener);

        mLoading.setVisibility(View.VISIBLE);
        mNoResult.setVisibility(View.GONE);
        HttpHelper.getTracks(String.valueOf(mAlbumId), mResponseHandler);
    }

    private HttpResponseHandler mResponseHandler = new HttpResponseHandler() {
        @Override
        public void onSuccess(String response) {
            mTracks = JsonHelper.parseTracks(response);
            NetworkTracksActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.setTracks(mTracks);
                    if (mAdapter.getCount() == 0) {
                        mNoResult.setVisibility(View.VISIBLE);
                    } else {
                        mNoResult.setVisibility(View.GONE);
                    }
                    mLoading.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public void onFail(int status, String response) {
            NetworkTracksActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoading.setVisibility(View.GONE);
                }
            });
        }
    };

    private void downloadTrack(final NetworkTrackInfo info) {
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == Dialog.BUTTON_POSITIVE) {
                    doDownloadTrack(info);
                    makeToast();
                }
            }
        };

        Dialog dialog = DialogBuilder.buildAlertDialog(this, R.string.download_track_confirm,
                listener);
        dialog.show();
    }

    private void doDownloadTrack(NetworkTrackInfo info) {
        String url = HttpHelper.getDownloadTrackUrl(String.valueOf(info.dfsId), info.ext);
        FileDownloadingHelper.getInstance().requstDownloadTrack(info, url);
    }

    private void makeToast() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), R.string.download_task_added,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
