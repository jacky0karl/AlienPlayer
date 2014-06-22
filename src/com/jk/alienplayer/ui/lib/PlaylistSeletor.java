package com.jk.alienplayer.ui.lib;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.ui.adapter.PlaylistsSeletorAdapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class PlaylistSeletor {

    public static Dialog buildPlaylistSeletor(Context context, OnItemClickListener listener) {
        PlaylistsSeletorAdapter adapter = new PlaylistsSeletorAdapter(context);
        adapter.setPlaylists(DatabaseHelper.getPlaylists(context));
        ListView list = new ListView(context);
        list.setAdapter(adapter);
        list.setOnItemClickListener(listener);

        Dialog dialog = new AlertDialog.Builder(context).setView(list)
                .setNegativeButton(R.string.cancel, null).create();
        return dialog;
    }
}
