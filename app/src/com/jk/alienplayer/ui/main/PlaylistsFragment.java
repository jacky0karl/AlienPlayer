package com.jk.alienplayer.ui.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.PlaylistHelper;
import com.jk.alienplayer.metadata.CurrentlistInfo;
import com.jk.alienplayer.metadata.PlaylistInfo;
import com.jk.alienplayer.ui.adapter.PlaylistsAdapter;
import com.jk.alienplayer.ui.artistdetail.SongsActivity;

public class PlaylistsFragment extends Fragment {

    private LayoutInflater mInflater;
    private ListView mListView;
    private PlaylistsAdapter mAdapter;
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

    private void init(View root) {
        setHasOptionsMenu(true);
        mInflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mListView = (ListView) root.findViewById(R.id.list);
        mAdapter = new PlaylistsAdapter(getActivity(), mOnItemClickListener);
        mListView.setAdapter(mAdapter);
        mAdapter.setPlaylists(PlaylistHelper.getPlaylists(getActivity()));
        mListView.setOnItemClickListener(mOnItemClickListener);
    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mCurrPlaylist = mAdapter.getItem(position);
            if (view.getId() == R.id.action) {
                showPopupMenu(view);
            } else {
                startSongsPage(mCurrPlaylist.id, mCurrPlaylist.name);
            }
        }
    };

    private void showPopupMenu(View v) {
        PopupMenu menu = new PopupMenu(getActivity(), v);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                PlaylistHelper.deletePlaylist(getActivity(), mCurrPlaylist.id);
                return false;
            }
        });

        menu.getMenu().add(Menu.NONE, Menu.NONE, Menu.NONE, R.string.delete);
        menu.show();
    }

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
