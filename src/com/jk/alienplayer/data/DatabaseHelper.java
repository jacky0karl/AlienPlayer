package com.jk.alienplayer.data;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

public class DatabaseHelper {

    public static final int TYPE_ARTIST = 1;
    public static final int TYPE_ALBUM = 2;

    private static final String DISTINCT = "DISTINCT ";

    public static List<ArtistInfo> getArtists(Context context) {
        List<ArtistInfo> artists = new ArrayList<ArtistInfo>();
        String[] projection = new String[] { DISTINCT + MediaStore.Audio.Media.ARTIST };

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            cursor.moveToFirst();
            do {
                String artist = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                ArtistInfo info = new ArtistInfo(artist);
                artists.add(info);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return artists;
    }

    public static List<SongInfo> getSongs(Context context, int keyType, String key) {
        List<SongInfo> songs = new ArrayList<SongInfo>();
        if (TextUtils.isEmpty(key)) {
            return songs;
        }

        String[] projection = new String[] { MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID };
        String selection;
        switch (keyType) {
        case TYPE_ARTIST:
            selection = MediaStore.Audio.Media.ARTIST + "=?";
            break;
        case TYPE_ALBUM:
            selection = MediaStore.Audio.Media.ALBUM + "=?";
            break;
        default:
            return songs;
        }
        String[] selectionArgs = new String[] { key };

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            cursor.moveToFirst();
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String title = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                long duration = cursor.getLong(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                String path = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                long albumId = cursor.getLong(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                SongInfo info = new SongInfo(id, title, duration, path);
                info.albumId = albumId;
                songs.add(info);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return songs;
    }

    private static final Uri AlbumArtUri = Uri.parse("content://media/external/audio/albumart");

    public static Bitmap getArtwork(Context context, long songId, long albumId, int targetSize) {
        Bitmap bmp = null;
        if (albumId < 0 && songId < 0) {
            return null;
        }

        try {
            FileDescriptor fd = null;
            if (albumId < 0) {
                Uri uri = Uri.parse("content://media/external/audio/media/" + songId + "/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver()
                        .openFileDescriptor(uri, "r");
                if (pfd != null) {
                    fd = pfd.getFileDescriptor();
                }
            } else {
                Uri uri = ContentUris.withAppendedId(AlbumArtUri, albumId);
                ParcelFileDescriptor pfd = context.getContentResolver()
                        .openFileDescriptor(uri, "r");
                if (pfd != null) {
                    fd = pfd.getFileDescriptor();
                }
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fd, null, options);

            int ratioW = options.outWidth / targetSize;
            int ratioH = options.outHeight / targetSize;
            options.inSampleSize = ratioW > ratioH ? ratioW : ratioH;

            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bmp = BitmapFactory.decodeFileDescriptor(fd, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bmp;
    }
}
