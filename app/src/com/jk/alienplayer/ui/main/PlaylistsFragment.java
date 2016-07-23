package com.jk.alienplayer.ui.main;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.PlaylistHelper;
import com.jk.alienplayer.metadata.CurrentlistInfo;
import com.jk.alienplayer.metadata.PlaylistInfo;
import com.jk.alienplayer.ui.artistdetail.SongsActivity;
import com.jk.alienplayer.ui.adapter.PlaylistsAdapter;
import com.jk.alienplayer.widget.ListMenu;
import com.jk.alienplayer.widget.ListMenu.OnMenuItemClickListener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.ContentObserver;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Playlists;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class PlaylistsFragment extends Fragment implements OnMenuItemClickListener {

    private LayoutInflater mInflater;
    private ListView mListView;
    private PlaylistsAdapter mAdapter;
    private ListMenu mListMenu;
    private PopupWindow mPopupWindow;
    private PlaylistInfo mCurrPlaylist;

    private ContentObserver mContentObserver = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.setPlaylists(PlaylistHelper.getPlaylists(getActivity()));
                }
            });
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        init(root);
        getActivity().getContentResolver().registerContentObserver(Playlists.EXTERNAL_CONTENT_URI,
                true, mContentObserver);
        return root;
    }

    @Override
    public void onDestroyView() {
        mPopupWindow.dismiss();
        getActivity().getContentResolver().unregisterContentObserver(mContentObserver);
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.playlist, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            addPlaylist();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(int menuId) {
        mPopupWindow.dismiss();
        if (ListMenu.MEMU_DELETE == menuId) {
            PlaylistHelper.deletePlaylist(getActivity(), mCurrPlaylist.id);
        }
    }

    private void init(View root) {
        setHasOptionsMenu(true);
        mInflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mListView = (ListView) root.findViewById(R.id.list);
        mAdapter = new PlaylistsAdapter(getActivity(), mOnItemClickListener);
        mListView.setAdapter(mAdapter);
        mAdapter.setPlaylists(PlaylistHelper.getPlaylists(getActivity()));
        mListView.setOnItemClickListener(mOnItemClickListener);
        setupPopupWindow();
    }

    private void setupPopupWindow() {
        mListMenu = new ListMenu(getActivity());
        mListMenu.setMenuItemClickListener(this);
        mListMenu.addMenu(ListMenu.MEMU_DELETE, R.string.delete);
        mPopupWindow = new PopupWindow(mListMenu, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mCurrPlaylist = mAdapter.getItem(position);
            if (view.getId() == R.id.action) {
                mPopupWindow.showAsDropDown(view);
            } else {
                startSongsPage(mCurrPlaylist.id, mCurrPlaylist.name);
            }
        }
    };

    private void startSongsPage(long key, String label) {
        Intent intent = new Intent(getActivity(), SongsActivity.class);
        intent.putExtra(SongsActivity.KEY_TYPE, CurrentlistInfo.TYPE_PLAYLIST);
        intent.putExtra(SongsActivity.KEY, key);
        intent.putExtra(SongsActivity.LABEL, label);
        startActivity(intent);
    }

    private void addPlaylist() {
        final EditText edit = (EditText) mInflater.inflate(R.layout.edit, null);
        edit.setHeight(getActivity().getResources().getDimensionPixelSize(R.dimen.button_size));
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == Dialog.BUTTON_POSITIVE) {
                    String name = edit.getText().toString().trim();
                    PlaylistHelper.addPlaylist(getActivity(), name);
                }
            }
        };

        Dialog dialog = new AlertDialog.Builder(getActivity()).setView(edit)
                .setPositiveButton(R.string.ok, listener)
                .setNegativeButton(R.string.cancel, listener).create();
        dialog.show();
    }
}
