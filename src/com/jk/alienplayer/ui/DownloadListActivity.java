package com.jk.alienplayer.ui;

import java.util.List;

import com.jk.alienplayer.R;
import com.jk.alienplayer.metadata.FileDownloadingInfo;
import com.jk.alienplayer.network.FileDownloadingHelper;
import com.jk.alienplayer.ui.adapter.FileDownloadListAdapter;
import com.jk.alienplayer.ui.lib.ListMenu;
import com.jk.alienplayer.ui.lib.ListMenu.OnMenuItemClickListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

public class DownloadListActivity extends Activity {
    private static final int UPDATE_INTERVAL = 500;

    private TextView mNoItem;
    private ListView mListView;
    private FileDownloadListAdapter mAdapter;
    private FileDownloadingInfo mCurrInfo = null;
    private Handler mHandler;
    private ListMenu mListMenu;
    private PopupWindow mPopupWindow;

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mCurrInfo = mAdapter.getItem(position);
            if (mCurrInfo != null) {
                if (view.getId() == R.id.action) {
                    updateListMenu();
                    mPopupWindow.showAsDropDown(view);
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
        setupPopupWindow();
    }

    private void setupPopupWindow() {
        mListMenu = new ListMenu(this);
        mListMenu.setMenuItemClickListener(mOnMenuItemClickListener);
        mPopupWindow = new PopupWindow(mListMenu, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
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

    @Override
    public void onDestroy() {
        mPopupWindow.dismiss();
        super.onDestroy();
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

    private void updateListMenu() {
        mListMenu.clearMenu();
        if (mCurrInfo.status == FileDownloadingInfo.Status.PENDING
                || mCurrInfo.status == FileDownloadingInfo.Status.DOWALOADING) {
            mListMenu.addMenu(ListMenu.MEMU_ABORT, R.string.abort);
        } else if (mCurrInfo.status == FileDownloadingInfo.Status.COMPLETED) {
            mListMenu.addMenu(ListMenu.MEMU_VIEW, R.string.view);
            mListMenu.addMenu(ListMenu.MEMU_REMOVE, R.string.remove);
        } else if (mCurrInfo.status == FileDownloadingInfo.Status.FAILED
                || mCurrInfo.status == FileDownloadingInfo.Status.CANCELED) {
            mListMenu.addMenu(ListMenu.MEMU_RETRY, R.string.retry);
            mListMenu.addMenu(ListMenu.MEMU_REMOVE, R.string.remove);
        }
        mPopupWindow.setContentView(mListMenu);
    }

    private void viewTrack() {
        Intent intent = new Intent(DownloadListActivity.this, SearchActivity.class);
        intent.putExtra(SearchActivity.QUERY, mCurrInfo.trackInfo.name);
        startActivity(intent);
    }

    private OnMenuItemClickListener mOnMenuItemClickListener = new OnMenuItemClickListener() {
        @Override
        public void onClick(int menuId) {
            mPopupWindow.dismiss();
            if (ListMenu.MEMU_VIEW == menuId) {
                viewTrack();
                return;
            } else if (ListMenu.MEMU_RETRY == menuId) {
                FileDownloadingHelper.getInstance().retryDownloadTrack(mCurrInfo);
            } else if (ListMenu.MEMU_REMOVE == menuId) {
                FileDownloadingHelper.getInstance().removeRecord(mCurrInfo);
            } else if (ListMenu.MEMU_ABORT == menuId) {
                FileDownloadingHelper.getInstance().abortDownloadTrack(mCurrInfo);
            }
            startListUpdate();
        }
    };
}
