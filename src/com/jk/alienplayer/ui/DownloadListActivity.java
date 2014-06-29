package com.jk.alienplayer.ui;

import java.util.List;

import com.jk.alienplayer.R;
import com.jk.alienplayer.metadata.FileDownloadingInfo;
import com.jk.alienplayer.network.FileDownloadingHelper;
import com.jk.alienplayer.ui.adapter.FileDownloadListAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DownloadListActivity extends Activity {
    private static final int UPDATE_INTERVAL = 500;

    private TextView mNoItem;
    private ListView mListView;
    private FileDownloadListAdapter mAdapter;
    private Handler mHandler;

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            FileDownloadingInfo info = mAdapter.getItem(position);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_list);
        init();
    }

    private void init() {
        mNoItem = (TextView) findViewById(R.id.no_item);
        mListView = (ListView) findViewById(R.id.list);
        mAdapter = new FileDownloadListAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mOnItemClickListener);
        mHandler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.post(mUpdateTask);
    }

    @Override
    protected void onPause() {
        mHandler.removeCallbacks(mUpdateTask);
        super.onPause();
    }

    Runnable mUpdateTask = new Runnable() {
        @Override
        public void run() {
            List<FileDownloadingInfo> list = FileDownloadingHelper.getInstance()
                    .getFileDownloadingList();
            mAdapter.setInfos(list);
            if (list.size() > 0) {
                mNoItem.setVisibility(View.GONE);
            } else {
                mNoItem.setVisibility(View.VISIBLE);
            }

            if (FileDownloadingHelper.getInstance().isAnyTaskUpdating()) {
                mHandler.postDelayed(mUpdateTask, UPDATE_INTERVAL);
            }
        }
    };
}
