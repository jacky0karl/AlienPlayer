package com.jk.alienplayer.data;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio.Playlists;

import com.jk.alienplayer.metadata.SongInfo;

public class RecentsDBHelper {

    public static final String RECENTS_LIST_NAME = "com.jk.alienplayer.recent";
    private static final int MAX_RECENTS_COUNT = 30;
    private static long sRecentsId = -1;

    public static void initRecents(Context context) {
        if (!hasRecentsList(context)) {
            createRecents(context);
        }
        deleteRedundantRecents(context);
    }

    public static Uri getRecentsUri(Context context) {
        return Uri.parse(Playlists.EXTERNAL_CONTENT_URI + "/" + sRecentsId);
    }

    public static List<SongInfo> getRecentTracks(Context context) {
        return DatabaseHelper.getPlaylistMembers(context, sRecentsId);
    }

    public static void addToRecents(Context context, long audioId, boolean isUpdate) {
        ContentValues values = new ContentValues();
        values.put(Playlists.Members.PLAY_ORDER, System.currentTimeMillis());
        Uri uri = Playlists.Members.getContentUri("external", sRecentsId);

        if (isUpdate) {
            String where = Playlists.Members.AUDIO_ID + "=?";
            String[] selectionArgs = new String[] { String.valueOf(audioId) };
            context.getContentResolver().update(uri, values, where, selectionArgs);
        } else {
            values.put(Playlists.Members.AUDIO_ID, audioId);
            context.getContentResolver().insert(uri, values);
        }
    }

    private static void deleteRedundantRecents(Context context) {
        long lastMember = 0;
        String[] projection = new String[] { Playlists.Members._ID, Playlists.Members.PLAY_ORDER };
        Uri uri = Playlists.Members.getContentUri("external", sRecentsId);
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null,
                Playlists.Members.PLAY_ORDER + " DESC");
        if (cursor != null) {
            if (cursor.getCount() <= MAX_RECENTS_COUNT) {
                cursor.close();
                return;
            }

            cursor.moveToPosition(MAX_RECENTS_COUNT);
            lastMember = cursor.getLong(cursor.getColumnIndexOrThrow(Playlists.Members.PLAY_ORDER));
            cursor.close();
        }

        String selection = Playlists.Members.PLAY_ORDER + "<=?";
        String[] selectionArgs = new String[] { String.valueOf(lastMember) };
        context.getContentResolver().delete(uri, selection, selectionArgs);
    }

    private static boolean hasRecentsList(Context context) {
        String[] projection = new String[] { Playlists._ID };
        String selection = Playlists.NAME + "=?";
        String[] selectionArgs = new String[] { RECENTS_LIST_NAME };
        Cursor cursor = context.getContentResolver().query(Playlists.EXTERNAL_CONTENT_URI,
                projection, selection, selectionArgs, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                sRecentsId = cursor.getLong(cursor.getColumnIndexOrThrow(Playlists._ID));
                return true;
            }
            cursor.close();
        }
        return false;
    }

    private static void createRecents(Context context) {
        ContentValues values = new ContentValues();
        values.put(Playlists.NAME, RECENTS_LIST_NAME);
        values.put(Playlists.DATE_ADDED, System.currentTimeMillis() / 1000);
        Uri ret = context.getContentResolver().insert(Playlists.EXTERNAL_CONTENT_URI, values);
        sRecentsId = Long.parseLong(ret.getLastPathSegment());
    }

}
