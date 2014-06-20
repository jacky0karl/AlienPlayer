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
import android.provider.MediaStore.Audio.Media;
import android.text.TextUtils;
import android.util.Log;

public class DatabaseHelper {
    public static final int TYPE_ALL = 0;
    public static final int TYPE_ARTIST = 1;
    public static final int TYPE_ALBUM = 2;

    private static final int MIN_MUSIC_SIZE = 1024 * 1024;
    private static final String DISTINCT = "DISTINCT ";
    private static final String SELECTION = Media.SIZE + ">'" + String.valueOf(MIN_MUSIC_SIZE)
            + "' and " + Media.IS_MUSIC + "=1";

    public static List<ArtistInfo> getArtists(Context context) {
        List<ArtistInfo> artists = new ArrayList<ArtistInfo>();
        String[] projection = new String[] { DISTINCT + Media.ARTIST_ID, Media.ARTIST };

        Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projection,
                SELECTION, null, Media.ARTIST);
        if (cursor != null) {
            Log.e("#########", "Artists count = " + cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    long artistId = cursor.getLong(cursor.getColumnIndexOrThrow(Media.ARTIST_ID));
                    String artist = cursor.getString(cursor.getColumnIndexOrThrow(Media.ARTIST));
                    ArtistInfo info = new ArtistInfo(artistId, artist);
                    artists.add(info);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return artists;
    }

    public static List<AlbumInfo> getAlbums(Context context) {
        List<AlbumInfo> albums = new ArrayList<AlbumInfo>();
        String[] projection = new String[] { DISTINCT + Media.ALBUM_ID, Media.ALBUM, Media.ARTIST };

        Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projection,
                SELECTION, null, Media.ARTIST);
        if (cursor != null) {
            Log.e("####", "Albums count = " + cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    albums.add(bulidAlbumInfo(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return albums;
    }

    public static List<SongInfo> getTracks(Context context, int keyType, String key) {
        List<SongInfo> songs = new ArrayList<SongInfo>();
        if (TextUtils.isEmpty(key) && keyType != TYPE_ALL) {
            return songs;
        }

        String[] projection = new String[] { Media._ID, Media.TITLE, Media.DURATION, Media.DATA,
                Media.ALBUM_ID, Media.ARTIST };
        String selection = SELECTION;
        switch (keyType) {
        case TYPE_ARTIST:
            selection += " and " + Media.ARTIST_ID + "=?";
            break;
        case TYPE_ALBUM:
            selection += " and " + Media.ALBUM_ID + "=?";
            break;
        default:
            break;
        }

        String[] selectionArgs = null;
        if (keyType != TYPE_ALL) {
            selectionArgs = new String[] { key };
        }

        Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projection,
                selection, selectionArgs, Media.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            Log.e("#########", "Songs count = " + cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    songs.add(bulidSongInfo(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return songs;
    }

    public static SongInfo getSong(Context context, long id) {
        SongInfo info = null;
        String[] projection = new String[] { Media._ID, Media.TITLE, Media.DURATION, Media.DATA,
                Media.ALBUM_ID, Media.ARTIST };
        String selection = SELECTION;
        selection += " and " + Media._ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(id) };

        Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projection,
                selection, selectionArgs, Media.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                info = bulidSongInfo(cursor);
            }
            cursor.close();
        }
        return info;
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

    public static List<SearchResult> search(Context context, String key) {
        List<SearchResult> results = new ArrayList<SearchResult>();
        if (TextUtils.isEmpty(key)) {
            return results;
        }

        results.addAll(searchArtists(context, key));
        results.addAll(searchAlbums(context, key));
        results.addAll(searchTracks(context, key));
        return results;
    }

    private static List<SearchResult> searchArtists(Context context, String key) {
        List<SearchResult> results = new ArrayList<SearchResult>();

        String[] projection = new String[] { DISTINCT + Media.ARTIST_ID, Media.ARTIST };
        String selection = SELECTION;
        selection += " and " + Media.ARTIST + " like ?";
        String[] selectionArgs = new String[] { "%" + key + "%" };

        Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projection,
                selection, selectionArgs, Media.ARTIST);
        if (cursor != null) {
            Log.e("#########", "Artists search count = " + cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    SearchResult result = new SearchResult(SearchResult.TYPE_ARTISTS,
                            bulidArtistInfo(cursor));
                    results.add(result);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return results;
    }

    private static List<SearchResult> searchAlbums(Context context, String key) {
        List<SearchResult> results = new ArrayList<SearchResult>();

        String[] projection = new String[] { DISTINCT + Media.ALBUM_ID, Media.ALBUM, Media.ARTIST };
        String selection = SELECTION;
        selection += " and " + Media.ALBUM + " like ?";
        String[] selectionArgs = new String[] { "%" + key + "%" };

        Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projection,
                selection, selectionArgs, Media.ALBUM);
        if (cursor != null) {
            Log.e("#########", "Albums search count = " + cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    SearchResult result = new SearchResult(SearchResult.TYPE_ALBUMS,
                            bulidAlbumInfo(cursor));
                    results.add(result);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return results;
    }

    private static List<SearchResult> searchTracks(Context context, String key) {
        List<SearchResult> results = new ArrayList<SearchResult>();
        String[] projection = new String[] { Media._ID, Media.TITLE, Media.DURATION, Media.DATA,
                Media.ALBUM_ID, Media.ARTIST };
        String selection = SELECTION;
        selection += " and " + Media.TITLE + " like ?";
        String[] selectionArgs = new String[] { "%" + key + "%" };

        Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projection,
                selection, selectionArgs, Media.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            Log.e("#########", "Songs search count = " + cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    SearchResult result = new SearchResult(SearchResult.TYPE_TRACKS,
                            bulidSongInfo(cursor));
                    results.add(result);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return results;
    }

    private static ArtistInfo bulidArtistInfo(Cursor cursor) {
        long artistId = cursor.getLong(cursor.getColumnIndexOrThrow(Media.ARTIST_ID));
        String artist = cursor.getString(cursor.getColumnIndexOrThrow(Media.ARTIST));
        ArtistInfo info = new ArtistInfo(artistId, artist);
        return info;
    }

    private static AlbumInfo bulidAlbumInfo(Cursor cursor) {
        long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(Media.ALBUM_ID));
        String album = cursor.getString(cursor.getColumnIndexOrThrow(Media.ALBUM));
        String artist = cursor.getString(cursor.getColumnIndexOrThrow(Media.ARTIST));
        AlbumInfo info = new AlbumInfo(albumId, album, artist);
        return info;
    }

    private static SongInfo bulidSongInfo(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(Media._ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(Media.TITLE));
        long duration = cursor.getLong(cursor.getColumnIndexOrThrow(Media.DURATION));
        String path = cursor.getString(cursor.getColumnIndexOrThrow(Media.DATA));
        long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(Media.ALBUM_ID));
        String artist = cursor.getString(cursor.getColumnIndexOrThrow(Media.ARTIST));

        SongInfo info = new SongInfo(id, title, duration, path);
        info.albumId = albumId;
        info.artist = artist;
        return info;
    }
}
