package com.jk.alienplayer.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio.Playlists;
import android.text.TextUtils;
import android.util.Log;

import com.jk.alienplayer.metadata.PlaylistInfo;
import com.jk.alienplayer.metadata.SongInfo;

public class PlaylistHelper {

    public static List<PlaylistInfo> getPlaylists(Context context) {
        List<PlaylistInfo> playlists = new ArrayList<PlaylistInfo>();
        String[] projection = new String[] { Playlists._ID, Playlists.NAME };
        String selection = Playlists.NAME + "<>?";
        String[] selectionArgs = new String[] { RecentsDBHelper.RECENTS_LIST_NAME };
        Cursor cursor = context.getContentResolver().query(Playlists.EXTERNAL_CONTENT_URI,
                projection, selection, selectionArgs, Playlists.DATE_MODIFIED + " DESC");
        if (cursor != null) {
            Log.e("####", "Playlists count = " + cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    playlists.add(bulidPlaylist(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return playlists;
    }

    public static void addPlaylist(Context context, String name) {
        if (TextUtils.isEmpty(name) || RecentsDBHelper.RECENTS_LIST_NAME.equals(name)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(Playlists.NAME, name);
        values.put(Playlists.DATE_ADDED, System.currentTimeMillis() / 1000);
        context.getContentResolver().insert(Playlists.EXTERNAL_CONTENT_URI, values);
    }

    public static boolean deletePlaylist(Context context, long id) {
        String selection = Playlists._ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(id) };
        int ret = context.getContentResolver().delete(Playlists.EXTERNAL_CONTENT_URI, selection,
                selectionArgs);
        return ret > 0;
    }

    public static void addMemberToPlaylist(Context context, long playlistId, long trackId) {
        ContentValues values = new ContentValues();
        values.put(Playlists.Members.AUDIO_ID, trackId);
        values.put(Playlists.Members.PLAY_ORDER, System.currentTimeMillis());
        Uri uri = Playlists.Members.getContentUri("external", playlistId);
        context.getContentResolver().insert(uri, values);
    }

    public static boolean removeMemberFromPlaylist(Context context, long playlistId, long trackId) {
        String selection = Playlists.Members.AUDIO_ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(trackId) };
        Uri uri = Playlists.Members.getContentUri("external", playlistId);
        int ret = context.getContentResolver().delete(uri, selection, selectionArgs);
        return ret > 0 ? true : false;
    }

    public static List<SongInfo> getPlaylistMembers(Context context, long playlistId) {
        List<SongInfo> members = new ArrayList<SongInfo>();
        String[] projection = new String[] { Playlists.Members.AUDIO_ID, Playlists.Members.TITLE,
                Playlists.Members.ARTIST, Playlists.Members.ALBUM_ID, Playlists.Members.DURATION,
                Playlists.Members.DATA };
        Uri uri = Playlists.Members.getContentUri("external", playlistId);
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null,
                Playlists.Members.PLAY_ORDER + " DESC");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    members.add(bulidSongInfoByPlaylist(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return members;
    }

    private static PlaylistInfo bulidPlaylist(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(Playlists._ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(Playlists.NAME));
        PlaylistInfo info = new PlaylistInfo(id, name);
        return info;
    }

    private static SongInfo bulidSongInfoByPlaylist(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(Playlists.Members.AUDIO_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(Playlists.Members.TITLE));
        long duration = cursor.getLong(cursor.getColumnIndexOrThrow(Playlists.Members.DURATION));
        String path = cursor.getString(cursor.getColumnIndexOrThrow(Playlists.Members.DATA));
        long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(Playlists.Members.ALBUM_ID));
        String artist = cursor.getString(cursor.getColumnIndexOrThrow(Playlists.Members.ARTIST));

        SongInfo info = new SongInfo(id, title, duration, path);
        info.albumId = albumId;
        info.artist = artist;
        return info;
    }
}
