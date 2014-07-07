package com.jk.alienplayer.ui.lib;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.ui.adapter.PlaylistsSeletorAdapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;

public class TrackOperationHelper {

    public interface OnDeleteTrackHandler {
        void onDelete(boolean deleteFile);
    }

    public static Dialog buildDeleteConfirmDialog(Context context, final OnDeleteTrackHandler handler) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_delete, null);
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);

        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == Dialog.BUTTON_POSITIVE) {
                    handler.onDelete(checkBox.isChecked());
                }
            }
        };

        Dialog dialog = new AlertDialog.Builder(context).setView(view)
                .setPositiveButton(R.string.ok, listener).setNegativeButton(R.string.cancel, null)
                .create();
        return dialog;
    }

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
