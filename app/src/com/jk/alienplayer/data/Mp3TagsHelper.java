package com.jk.alienplayer.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import com.jk.alienplayer.MainApplication;
import com.jk.alienplayer.metadata.SongInfo;
import com.jk.alienplayer.metadata.TrackTagInfo;
import com.jk.alienplayer.model.TrackBean;
import com.jk.alienplayer.utils.FileSavingUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import org.jaudiotagger.tag.images.AndroidArtwork;
import org.jaudiotagger.tag.images.Artwork;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class Mp3TagsHelper {
    private static final String TAG = "Mp3TagsHelper";

    public interface OnMP3AddListener {
        void onMP3Added();

        void onArtworkUpdated(String artworkPath);
    }

    public static TrackTagInfo readMp3Tags(String filePath) {
        TrackTagInfo info = new TrackTagInfo();
        if (TextUtils.isEmpty(filePath)) {
            return info;
        }

        File file = new File(filePath);
        try {
            MP3File mp3 = (MP3File) AudioFileIO.read(file);
            AbstractID3v2Tag tag = mp3.getID3v2Tag();

            Artwork artwork = tag.getFirstArtwork();
            if (artwork != null) {
                byte[] data = artwork.getBinaryData();
                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                info.setArtwork(bmp);
            }

            info.setTitle(tag.getFirst(FieldKey.TITLE));
            info.setArtists(tag.getFirst(FieldKey.ARTIST));
            info.setAlbum(tag.getFirst(FieldKey.ALBUM));
            info.setArtistAlbum(tag.getFirst(FieldKey.ALBUM_ARTIST));
            info.setTrack(tag.getFirst(FieldKey.TRACK));
            info.setYear(tag.getFirst(FieldKey.YEAR));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return info;
    }

    public static void writeMp3Info(OnMP3AddListener l, TrackBean info, String filePath) {
        writeMp3Info(l, info.getAlbum().getPicUrl(), info.getName(), info.getShowingArtists(),
                info.getAlbum().getName(), info.getAlbum().getShowingArtist(), String.valueOf(info.getPosition()),
                String.valueOf(info.getAlbum().getPublishTime()), filePath);
    }

    public static void writeMp3Info(OnMP3AddListener l, String coverUrl, String title, String artists,
                                    String album, String artistAlbum, String track, String year, String filePath) {
        if (l == null) {
            return;
        }

        try {
            File file = new File(filePath);
            MP3File mp3 = (MP3File) AudioFileIO.read(file);
            writeMp3Tags(mp3, artists, album, artistAlbum, title, track, year);

            if (TextUtils.isEmpty(coverUrl)) {
                l.onMP3Added();
            } else {
                fetchCover(l, mp3, coverUrl);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            l.onMP3Added();
        }
    }

    public static void writeMp3ListInfo(OnMP3AddListener l, List<SongInfo> list, Bitmap cover,
                                        String artists, String album, String artistAlbum, String year) {
        try {
            for (SongInfo song : list) {
                File file = new File(song.path);
                MP3File mp3 = (MP3File) AudioFileIO.read(file);
                writeMp3Tags(mp3, artists, album, artistAlbum, null, null, year);
                writeMp3Cover(mp3, cover);
            }
            l.onMP3Added();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            l.onMP3Added();
        }
    }

    private static void writeMp3Tags(MP3File mp3, String artists, String album, String artistAlbum,
                                     String title, String track, String year) {
        try {
            ID3v23Tag tag = new ID3v23Tag();
            if (!TextUtils.isEmpty(artists)) {
                tag.setField(FieldKey.ARTIST, artists);
            }
            if (!TextUtils.isEmpty(artistAlbum)) {
                tag.setField(FieldKey.ALBUM_ARTIST, artistAlbum);
            }
            if (!TextUtils.isEmpty(album)) {
                tag.setField(FieldKey.ALBUM, album);
            }

            if (!TextUtils.isEmpty(title)) {
                tag.setField(FieldKey.TITLE, title);
            }
            if (!TextUtils.isEmpty(track) && TextUtils.isDigitsOnly(track)) {
                tag.setField(FieldKey.TRACK, track);
            }
            if (!TextUtils.isEmpty(year) && TextUtils.isDigitsOnly(year)) {
                tag.setField(FieldKey.YEAR, year);
            }

            mp3.setID3v2Tag(tag);
            mp3.save();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private static void writeMp3Cover(MP3File mp3, Bitmap cover) {
        try {
            String filePath = FileSavingUtils.sRootPath + System.currentTimeMillis();
            FileOutputStream fos = new FileOutputStream(filePath);
            cover.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            File file = new File(filePath);
            Artwork artwork = AndroidArtwork.createArtworkFromFile(file);
            if (artwork != null) {
                AbstractID3v2Tag tags = mp3.getID3v2Tag();
                tags.deleteArtworkField();
                tags.setField(artwork);
                mp3.setID3v2Tag(tags);
                mp3.save();
            }
            file.delete();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private static void fetchCover(final OnMP3AddListener l, final MP3File mp3, String url) {
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                try {
                    String tmp = FileSavingUtils.sRootPath + System.currentTimeMillis();
                    FileOutputStream fos = new FileOutputStream(tmp);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                    writeMp3Cover(l, mp3, tmp);
                } catch (Exception e) {
                    l.onMP3Added();
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                l.onMP3Added();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };

        Picasso.with(MainApplication.app).load(url).into(target);
    }

    private static void writeMp3Cover(OnMP3AddListener l, MP3File mp3, String filePath) {
        try {
            File file = new File(filePath);
            Artwork cover = AndroidArtwork.createArtworkFromFile(file);
            if (cover != null) {
                AbstractID3v2Tag tags = mp3.getID3v2Tag();
                tags.deleteArtworkField();
                tags.setField(cover);
                mp3.setID3v2Tag(tags);
                mp3.save();
                l.onArtworkUpdated(filePath);
            }
            file.delete();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            l.onMP3Added();
        }
    }

}
