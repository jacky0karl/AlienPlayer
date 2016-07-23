package com.jk.alienplayer.data;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.jk.alienplayer.MainApplication;
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

public class Mp3TagsHelper {

    public static void writeMp3Tags(OnMP3AddListener l, TrackBean info, String filePath) {
        writeMp3Tags(l, info.getAlbum().getPicUrl(), info.getName(), info.getShowingArtists(),
                info.getAlbum().getName(), info.getAlbum().getShowingArtist(), String.valueOf(info.getPosition()),
                String.valueOf(info.getAlbum().getPublishTime()), filePath);
    }

    public static void writeMp3Tags(OnMP3AddListener l, String coverUrl, String title, String artists,
                                    String album, String artistAlbum, String track, String year, String filePath) {
        if (l == null) {
            return;
        }

        try {
            File file = new File(filePath);
            MP3File mp3 = (MP3File) AudioFileIO.read(file);
            ID3v23Tag tag = new ID3v23Tag();
            tag.setField(FieldKey.ARTIST, artists);
            tag.setField(FieldKey.ALBUM_ARTIST, artistAlbum);
            tag.setField(FieldKey.ALBUM, album);
            tag.setField(FieldKey.TITLE, title);
            if (!TextUtils.isEmpty(track) && TextUtils.isDigitsOnly(track)) {
                tag.setField(FieldKey.TRACK, track);
            }
            if (!TextUtils.isEmpty(year) && TextUtils.isDigitsOnly(year)) {
                tag.setField(FieldKey.YEAR, year);
            }

            mp3.setID3v2Tag(tag);
            mp3.save();

            if (TextUtils.isEmpty(coverUrl)) {
                l.onMP3Added();
            } else {
                fetchCover(l, mp3, coverUrl);
            }
        } catch (Exception e) {
            l.onMP3Added();
        }
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

            info.setTitle(tag.getFirst(FieldKey.TITLE));
            info.setArtists(tag.getFirst(FieldKey.ARTIST));
            info.setAlbum(tag.getFirst(FieldKey.ALBUM));
            info.setArtistAlbum(tag.getFirst(FieldKey.ALBUM_ARTIST));
            info.setTrack(tag.getFirst(FieldKey.TRACK));
            info.setYear(tag.getFirst(FieldKey.YEAR));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
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
                    bitmap.recycle();
                    addCoverField(l, mp3, tmp);
                } catch (Exception e) {
                    l.onMP3Added();
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

    private static void addCoverField(OnMP3AddListener l, MP3File mp3, String filePath) {
        try {
            File file = new File(filePath);
            Artwork cover = AndroidArtwork.createArtworkFromFile(file);
            if (cover != null) {
                AbstractID3v2Tag tags = mp3.getID3v2Tag();
                tags.deleteArtworkField();
                tags.setField(cover);
                mp3.setID3v2Tag(tags);
                mp3.save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            l.onMP3Added();
        }
    }

    public interface OnMP3AddListener {
        void onMP3Added();
    }
}
