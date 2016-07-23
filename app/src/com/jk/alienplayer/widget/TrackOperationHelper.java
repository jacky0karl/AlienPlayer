package com.jk.alienplayer.widget;

import java.io.File;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.data.PlaylistHelper;
import com.jk.alienplayer.metadata.SongInfo;
import com.jk.alienplayer.network.FileDownloadingHelper;
import com.jk.alienplayer.ui.adapter.PlaylistsSeletorAdapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

public class TrackOperationHelper {

    public static String getLyricPath(String songPath) {
        int index = songPath.lastIndexOf('.');
        return songPath.substring(0, index + 1) + FileDownloadingHelper.LYRIC_EXT;
    }

    /**
     * Delete
     */
    public interface OnDeleteTrackListener {
        void onComplete();
    }

    public static void deleteTrack(final Context context, final SongInfo info,
            final OnDeleteTrackListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_delete, null);
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);

        OnClickListener l = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == Dialog.BUTTON_POSITIVE) {
                    dodeleteTrack(context, info, checkBox.isChecked());
                    listener.onComplete();
                }
            }
        };

        Dialog dialog = new AlertDialog.Builder(context).setView(view)
                .setPositiveButton(R.string.ok, l).setNegativeButton(R.string.cancel, null)
                .create();
        dialog.show();
    }

    private static void dodeleteTrack(Context context, SongInfo info, boolean deleteFile) {
        if (DatabaseHelper.deleteTrack(context, info.id) && deleteFile) {
            File file = new File(info.path);
            if (file.exists()) {
                file.delete();
            }

            File lyric = new File(getLyricPath(info.path));
            if (lyric.exists()) {
                lyric.delete();
            }
        }
    }

    /**
     * Add To Playlist
     */
    private static Dialog mPlaylistSeletor = null;

    public static void addToPlaylist(final Context context, final long trackId) {
        OnItemClickListener listener = new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPlaylistSeletor.dismiss();
                PlaylistHelper.addMemberToPlaylist(context, id, trackId);
            }
        };

        mPlaylistSeletor = buildPlaylistSeletor(context, listener);
        mPlaylistSeletor.show();
    }

    private static Dialog buildPlaylistSeletor(Context context, OnItemClickListener listener) {
        PlaylistsSeletorAdapter adapter = new PlaylistsSeletorAdapter(context);
        adapter.setPlaylists(PlaylistHelper.getPlaylists(context));
        ListView list = new ListView(context);
        list.setAdapter(adapter);
        list.setOnItemClickListener(listener);

        Dialog dialog = new AlertDialog.Builder(context).setView(list)
                .setNegativeButton(R.string.cancel, null).create();
        return dialog;
    }

}
