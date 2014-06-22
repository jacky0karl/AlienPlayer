package com.jk.alienplayer.ui.fragment;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.metadata.PlaylistInfo;
import com.jk.alienplayer.ui.SongsActivity;
import com.jk.alienplayer.ui.adapter.PlaylistsAdapter;
import com.jk.alienplayer.ui.lib.ListMenu;
import com.jk.alienplayer.ui.lib.ListMenu.OnMenuItemClickListener;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class PlaylistsFragment extends Fragment implements OnMenuItemClickListener {

    private ListView mListView;
    private PlaylistsAdapter mAdapter;
    private ListMenu mListMenu;
    private PopupWindow mPopupWindow;
    private PopupMenu mPopupMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        init(root);
        return root;
    }

    @Override
    public void onDestroy() {
        mPopupWindow.dismiss();
        super.onDestroy();
    }

    private void init(View root) {
        mListView = (ListView) root.findViewById(R.id.list);
        mAdapter = new PlaylistsAdapter(getActivity(), mOnItemClickListener);
        mListView.setAdapter(mAdapter);
        mAdapter.setPlaylists(DatabaseHelper.getPlaylists(getActivity()));
        mListView.setOnItemClickListener(mOnItemClickListener);
        setupPopupWindow();
    }

    private void setupPopupWindow() {
        mListMenu = new ListMenu(getActivity());
        mListMenu.setMenuItemClickListener(this);
        mListMenu.addMenu(ListMenu.MEMU_ID_DELETE, R.string.delete);
        mListMenu.addMenu(1, R.string.delete);
        mListMenu.addMenu(2, R.string.delete);
        mPopupWindow = new PopupWindow(mListMenu, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            PlaylistInfo info = mAdapter.getItem(position);
            if (view.getId() == R.id.action) {
                mPopupWindow.showAsDropDown(view);
            } else {
                startSongsPage(info.id, info.name);
            }
        }
    };

    private void startSongsPage(long key, String label) {
        Intent intent = new Intent(getActivity(), SongsActivity.class);
        intent.putExtra(SongsActivity.KEY_TYPE, DatabaseHelper.TYPE_PLAYLIST);
        intent.putExtra(SongsActivity.KEY, key);
        intent.putExtra(SongsActivity.LABEL, label);
        startActivity(intent);
    }

    @Override
    public void onClick(int menuId) {
        // TODO Auto-generated method stub
        
    }

}
