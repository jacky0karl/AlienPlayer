package com.jk.alienplayer.data;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore.Audio.Artists;
import android.provider.MediaStore.Audio.Media;
import android.text.TextUtils;

import com.jk.alienplayer.metadata.AlbumInfo;
import com.jk.alienplayer.metadata.ArtistInfo;
import com.jk.alienplayer.metadata.CurrentlistInfo;
import com.jk.alienplayer.metadata.SearchResult;
import com.jk.alienplayer.metadata.SongInfo;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    /**
     * Hiding field of MediaStore.Audio.Media, may be disabled in future
     */
    private static final String ALBUM_ARTIST = "album_artist";
    private static final int MIN_MUSIC_SIZE = 1024 * 1024;
    private static final String DISTINCT = "DISTINCT ";
    private static final String MEDIA_SELECTION = Media.SIZE + ">'"
            + String.valueOf(MIN_MUSIC_SIZE) + "' and " + Media.IS_MUSIC + "=1";
    private static final Uri AlbumArtUri = Uri.parse("content://media/external/audio/albumart");

    public static List<ArtistInfo> getAllArtists(Context context) {
        List<ArtistInfo> artists = new ArrayList<ArtistInfo>();
        String[] projection = new String[]{DISTINCT + Artists._ID, Artists.ARTIST,
                Artists.NUMBER_OF_ALBUMS, Artists.NUMBER_OF_TRACKS};

        Cursor cursor = context.getContentResolver().query(Artists.EXTERNAL_CONTENT_URI, projection,
                null, null, Artists.ARTIST);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    long artistId = cursor.getLong(cursor.getColumnIndexOrThrow(Artists._ID));
                    String artist = cursor.getString(cursor.getColumnIndexOrThrow(Artists.ARTIST));
                    ArtistInfo info = new ArtistInfo(artistId, artist);
                    artists.add(info);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return artists;
    }

    public static List<AlbumInfo> getAlbums(Context context, String artist) {
        List<AlbumInfo> albums = new ArrayList<>();
        String[] projection = new String[]{DISTINCT + Albums._ID, Albums.ALBUM, Albums.ARTIST,
                Albums.FIRST_YEAR, Albums.NUMBER_OF_SONGS, Albums.ALBUM_ART};
        String selection = Albums.ARTIST + "=?";
        String[] selectionArgs = new String[]{artist};
        Cursor cursor = context.getContentResolver().query(Albums.EXTERNAL_CONTENT_URI, projection,
                selection, selectionArgs, Albums.FIRST_YEAR);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    AlbumInfo info = bulidAlbums(cursor);
                    if (info != null) {
                        albums.add(info);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return albums;
    }

    public static List<AlbumInfo> getAllAlbums(Context context) {
        List<AlbumInfo> albums = new ArrayList<>();
        String[] projection = new String[]{DISTINCT + Albums._ID, Albums.ALBUM, Albums.ARTIST,
                Albums.FIRST_YEAR, Albums.NUMBER_OF_SONGS, Albums.ALBUM_ART};

        Cursor cursor = context.getContentResolver().query(Albums.EXTERNAL_CONTENT_URI, projection,
                null, null, Albums.ARTIST);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    AlbumInfo info = bulidAlbums(cursor);
                    if (info != null) {
                        albums.add(info);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return albums;
    }

    public static String getAlbumArtwork(Context context, long albumId) {
        String[] projection = new String[]{Albums.ALBUM_ART};
        String selection = Albums._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(albumId)};

        Cursor cursor = context.getContentResolver().query(Albums.EXTERNAL_CONTENT_URI, projection,
                selection, selectionArgs, null);
        String artwork = "file://";
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                artwork += cursor.getString(cursor.getColumnIndexOrThrow(Albums.ALBUM_ART));
            }
            cursor.close();
        }
        return artwork;
    }

    public static void deleteArtworkCache(Context context, long albumId, String artworkPath) {
        if (TextUtils.isEmpty(artworkPath)) {
            return;
        }

        String cachePath = getAlbumArtwork(context, albumId);
        int preCount = "file://".length();
        if (!TextUtils.isEmpty(cachePath) && cachePath.length() > preCount) {
            File cache = new File(cachePath.substring(preCount));
            if (cache.exists()) {
                cache.delete();
            }
        }
        Picasso.with(context).invalidate(cachePath);
    }

    public static Bitmap getArtworkFormFile(Context context, long songId, long albumId, int targetSize) {
        if (albumId < 0 && songId < 0) {
            return null;
        }

        Bitmap bmp = null;
        try {
            ParcelFileDescriptor pfd = null;
            if (albumId < 0) {
                Uri uri = Uri.parse("content://media/external/audio/media/" + songId + "/albumart");
                pfd = context.getContentResolver().openFileDescriptor(uri, "r");
            } else {
                Uri uri = ContentUris.withAppendedId(AlbumArtUri, albumId);
                pfd = context.getContentResolver().openFileDescriptor(uri, "r");
            }
            if (pfd == null) {
                return null;
            }

            bmp = decodeImage(pfd.getFileDescriptor(), targetSize);
            pfd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    private static AlbumInfo bulidAlbums(Cursor cursor) {
        long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(Albums._ID));
        String album = cursor.getString(cursor.getColumnIndexOrThrow(Albums.ALBUM));
        String artist = cursor.getString(cursor.getColumnIndexOrThrow(Albums.ARTIST));
        int year = cursor.getInt(cursor.getColumnIndexOrThrow(Albums.FIRST_YEAR));
        int tracks = cursor.getInt(cursor.getColumnIndexOrThrow(Albums.NUMBER_OF_SONGS));
        String artwork = cursor.getString(cursor.getColumnIndexOrThrow(Albums.ALBUM_ART));

        if (!AlbumInfo.UNKNOWN.equals(artist)) {
            AlbumInfo info = new AlbumInfo(albumId, album, artist);
            info.year = year;
            info.tracks = tracks;
            info.artwork = "file://" + artwork;
            return info;
        }
        return null;
    }

    public static List<SongInfo> getTracks(Context context, String artist) {
        List<SongInfo> songs = new ArrayList<SongInfo>();
        String[] projection = new String[]{Media._ID, Media.TITLE, Media.DURATION, Media.DATA,
                Media.ALBUM_ID, Media.ARTIST};
        String selection = MEDIA_SELECTION + " and " + Media.ARTIST + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + artist + "%"};
        String order = Media.DEFAULT_SORT_ORDER;

        Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projection,
                selection, selectionArgs, order);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    songs.add(bulidSongInfo(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return songs;
    }

    public static List<SongInfo> getTracks(Context context, int keyType, long key) {
        List<SongInfo> songs = new ArrayList<SongInfo>();
        String[] projection = new String[]{Media._ID, Media.TITLE, Media.DURATION, Media.DATA,
                Media.ALBUM_ID, Media.ARTIST};
        String selection = MEDIA_SELECTION;
        String order = Media.DEFAULT_SORT_ORDER;

        switch (keyType) {
            case CurrentlistInfo.TYPE_ARTIST:
                selection += " and " + Media.ARTIST_ID + "=?";
                break;
            case CurrentlistInfo.TYPE_ALBUM:
                selection += " and " + Media.ALBUM_ID + "=?";
                order = Media.TRACK;
                break;
            default:
                break;
        }

        String[] selectionArgs = null;
        if (keyType != CurrentlistInfo.TYPE_ALL) {
            selectionArgs = new String[]{String.valueOf(key)};
        }

        Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projection,
                selection, selectionArgs, order);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    songs.add(bulidSongInfo(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return songs;
    }

    public static SongInfo getTrack(Context context, long id) {
        SongInfo info = null;
        String[] projection = new String[]{Media._ID, Media.TITLE, Media.DURATION, Media.DATA,
                Media.ALBUM_ID, Media.ARTIST};
        String selection = MEDIA_SELECTION;
        selection += " and " + Media._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(id)};

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

    public static boolean deleteTrack(Context context, long id) {
        String selection = Media._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        int ret = context.getContentResolver().delete(Media.EXTERNAL_CONTENT_URI, selection,
                selectionArgs);
        return ret > 0 ? true : false;
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

        String[] projection = new String[]{DISTINCT + Media.ARTIST_ID, Media.ARTIST};
        String selection = MEDIA_SELECTION;
        selection += " and " + Media.ARTIST + " like ?";
        String[] selectionArgs = new String[]{"%" + key + "%"};

        Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projection,
                selection, selectionArgs, Media.ARTIST);
        if (cursor != null) {
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

        String[] projection = new String[]{DISTINCT + Media.ALBUM_ID, Media.ALBUM, Media.YEAR,
                ALBUM_ARTIST};
        String selection = MEDIA_SELECTION;
        selection += " and " + Media.ALBUM + " like ?";
        String[] selectionArgs = new String[]{"%" + key + "%"};

        Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projection,
                selection, selectionArgs, Media.ALBUM);
        if (cursor != null) {
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
        String[] projection = new String[]{Media._ID, Media.TITLE, Media.DURATION, Media.DATA,
                Media.ALBUM_ID, Media.ARTIST};
        String selection = MEDIA_SELECTION;
        selection += " and " + Media.TITLE + " like ?";
        String[] selectionArgs = new String[]{"%" + key + "%"};

        Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projection,
                selection, selectionArgs, Media.DEFAULT_SORT_ORDER);
        if (cursor != null) {
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
        int year = cursor.getInt(cursor.getColumnIndexOrThrow(Media.YEAR));
        String artist = cursor.getString(cursor.getColumnIndexOrThrow(ALBUM_ARTIST));
        AlbumInfo info = new AlbumInfo(albumId, album, artist);
        info.year = year;
        // info.tracks = tracks;
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

    private static Bitmap decodeImage(FileDescriptor fd, int targetSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);

        int ratioW = options.outWidth / targetSize;
        int ratioH = options.outHeight / targetSize;
        options.inSampleSize = ratioW > ratioH ? ratioW : ratioH;

        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }

//    public static List<ArtistInfo> getAlbumArtists(Context context) {
//        List<ArtistInfo> artists = new ArrayList<ArtistInfo>();
//        String[] projection = new String[]{DISTINCT + ALBUM_ARTIST};
//
//        Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projection,
//                MEDIA_SELECTION, null, ALBUM_ARTIST);
//        if (cursor != null) {
//            if (cursor.moveToFirst()) {
//                do {
//                    String artist = cursor.getString(cursor.getColumnIndexOrThrow(ALBUM_ARTIST));
//                    if (!TextUtils.isEmpty(artist)) {
//                        ArtistInfo info = new ArtistInfo(ArtistInfo.ALBUM_ARTIST_ID, artist);
//                        artists.add(info);
//                    }
//                } while (cursor.moveToNext());
//            }
//            cursor.close();
//        }
//        return artists;
//    }

//    public static List<AlbumInfo> getAlbums(Context context, String albumArtist) {
//        List<AlbumInfo> albums = new ArrayList<AlbumInfo>();
//        String[] projection = new String[]{DISTINCT + Media.ALBUM_ID, Media.ALBUM, Media.YEAR,
//                ALBUM_ARTIST};
//        String selection = MEDIA_SELECTION;
//        selection += " and " + ALBUM_ARTIST + "=?";
//        String[] selectionArgs = new String[]{albumArtist};
//
//        Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projection,
//                selection, selectionArgs, Media.YEAR);
//        if (cursor != null) {
//            if (cursor.moveToFirst()) {
//                do {
//                    albums.add(bulidAlbumInfo(cursor));
//                } while (cursor.moveToNext());
//            }
//            cursor.close();
//        }
//        return albums;
//    }
}
