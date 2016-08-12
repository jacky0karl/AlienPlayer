package com.jk.alienplayer.ui.network;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.jk.alienplayer.R;
import com.jk.alienplayer.metadata.FileDownloadingInfo;
import com.jk.alienplayer.network.FileDownloadingHelper;
import com.jk.alienplayer.ui.BaseActivity;
import com.jk.alienplayer.ui.adapter.FileDownloadListAdapter;
import com.jk.alienplayer.ui.main.SearchActivity;

import java.util.List;

public class DownloadListActivity extends BaseActivity {
    private static final int UPDATE_INTERVAL = 500;
    public static final int MEMU_REMOVE = 2;
    public static final int MEMU_VIEW = 3;
    public static final int MEMU_RETRY = 4;
    public static final int MEMU_ABORT = 5;

    private TextView mNoItem;
    private ListView mListView;
    private FileDownloadListAdapter mAdapter;
    private FileDownloadingInfo mCurrInfo = null;
    private Handler mHandler;

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mCurrInfo = mAdapter.getItem(position);
            if (mCurrInfo != null) {
                if (view.getId() == R.id.action) {
                    showPopupMenu(view);
                } else {
                    onFileClick();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_list);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.download_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_clear) {
            FileDownloadingHelper.getInstance().clearDone();
            updateView();
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        mNoItem = (TextView) findViewById(R.id.no_item);
        mListView = (ListView) findViewById(R.id.list);
        mAdapter = new FileDownloadListAdapter(this, mOnItemClickListener);
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
            updateView();
            if (FileDownloadingHelper.getInstance().isAnyTaskUpdating()) {
                mHandler.postDelayed(mUpdateTask, UPDATE_INTERVAL);
            }
        }
    };

    private void updateView() {
        List<FileDownloadingInfo> list = FileDownloadingHelper.getInstance()
                .getFileDownloadingList();

        if (list.size() > 0) {
            mNoItem.setVisibility(View.GONE);
        } else {
            mNoItem.setVisibility(View.VISIBLE);
        }
        mAdapter.setInfos(list);
    }

    private void startListUpdate() {
        mHandler.removeCallbacks(mUpdateTask);
        mHandler.post(mUpdateTask);
    }

    private void onFileClick() {
        if (mCurrInfo.status == FileDownloadingInfo.Status.COMPLETED) {
            viewTrack();
        }
    }

    private void showPopupMenu(View v) {
        PopupMenu menu = new PopupMenu(this, v);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case MEMU_VIEW:
                        viewTrack();
                        return true;
                    case MEMU_RETRY:
                        FileDownloadingHelper.getInstance().retryDownloadTrack(mCurrInfo);
                        break;
                    case MEMU_ABORT:
                        FileDownloadingHelper.getInstance().abortDownloadTrack(mCurrInfo);
                        break;
                    case MEMU_REMOVE:
                        FileDownloadingHelper.getInstance().removeRecord(mCurrInfo);
                        break;
                }
                startListUpdate();
                return true;
            }
        });

        if (mCurrInfo.status == FileDownloadingInfo.Status.PENDING
                || mCurrInfo.status == FileDownloadingInfo.Status.DOWALOADING) {
            menu.getMenu().add(Menu.NONE, MEMU_ABORT, Menu.NONE, R.string.abort);
        } else if (mCurrInfo.status == FileDownloadingInfo.Status.COMPLETED) {
            menu.getMenu().add(Menu.NONE, MEMU_VIEW, Menu.NONE, R.string.view);
            menu.getMenu().add(Menu.NONE, MEMU_REMOVE, Menu.NONE, R.string.remove);
        } else if (mCurrInfo.status == FileDownloadingInfo.Status.FAILED
                || mCurrInfo.status == FileDownloadingInfo.Status.CANCELED) {
            menu.getMenu().add(Menu.NONE, MEMU_RETRY, Menu.NONE, R.string.retry);
            menu.getMenu().add(Menu.NONE, MEMU_REMOVE, Menu.NONE, R.string.remove);
        }
        menu.show();
    }

    private void viewTrack() {
        Intent intent = new Intent(DownloadListActivity.this, SearchActivity.class);
        intent.putExtra(SearchActivity.QUERY, mCurrInfo.trackInfo.getName());
        startActivity(intent);
    }
}
