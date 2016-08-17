package com.jk.alienplayer.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.jk.alienplayer.MainApplication;
import com.jk.alienplayer.metadata.SongInfo;
import com.jk.alienplayer.metadata.TrackTagInfo;
import com.jk.alienplayer.model.TrackBean;
import com.jk.alienplayer.utils.FileSavingUtils;
import com.squareup.picasso.Picasso;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.images.AndroidArtwork;
import org.jaudiotagger.tag.images.Artwork;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class Mp3TagsHelper {
    private static final String TAG = "Mp3TagsHelper";

    public interface OnMP3AddListener {
        void onMP3Added();

        void onArtworkUpdated();
    }

    public static TrackTagInfo readMp3Tags(String filePath) {
        TrackTagInfo info = new TrackTagInfo();
        if (TextUtils.isEmpty(filePath)) {
            return info;
        }

        try {
            File file = new File(filePath);
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
        writeMp3Info(l, filePath, info.getAlbum().getPicUrl(), info.getName(), info.getShowingArtists(),
                info.getAlbum().getName(), info.getAlbum().getShowingArtist(), String.valueOf(info.getPosition()),
                String.valueOf(info.getAlbum().getPublishTime()));
    }

    public static void writeMp3Info(OnMP3AddListener l, String filePath, String coverUrl, String title, String artists,
                                    String album, String artistAlbum, String track, String year) {
        Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    File file = new File(filePath);
                    MP3File mp3 = (MP3File) AudioFileIO.read(file);
                    writeMp3Tags(mp3, artists, album, artistAlbum, title, track, year);
                    writeMp3Cover(l, mp3, coverUrl);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }

                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {
                        l.onMP3Added();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG + "Subscriber", e.getMessage(), e);
                    }

                    @Override
                    public void onNext(Void aVoid) {
                    }
                });
    }

    public static void writeMp3ListInfo(OnMP3AddListener l, List<SongInfo> list, String coverUrl,
                                        String artists, String album, String artistAlbum, String year) {
        Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    for (SongInfo song : list) {
                        File file = new File(song.path);
                        MP3File mp3 = (MP3File) AudioFileIO.read(file);
                        writeMp3Tags(mp3, artists, album, artistAlbum, null, null, year);
                        writeMp3Cover(l, mp3, coverUrl);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }

                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {
                        l.onMP3Added();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG + "Subscriber", e.getMessage(), e);
                    }

                    @Override
                    public void onNext(Void aVoid) {
                    }
                });
    }

    private static void writeMp3Tags(MP3File mp3, String artists, String album, String artistAlbum,
                                     String title, String track, String year) throws Exception {
        AbstractID3v2Tag tag = mp3.getID3v2Tag();
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
    }

    private static void writeMp3Cover(OnMP3AddListener l, MP3File mp3, String coverUrl) throws Exception {
        if (TextUtils.isEmpty(coverUrl)) {
            return;
        }

        Bitmap bitmap = Picasso.with(MainApplication.app).load(coverUrl).get();
        String filePath = FileSavingUtils.sRootPath + System.currentTimeMillis();
        FileOutputStream fos = new FileOutputStream(filePath);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();

        File file = new File(filePath);
        Artwork cover = AndroidArtwork.createArtworkFromFile(file);
        if (cover != null) {
            AbstractID3v2Tag tags = mp3.getID3v2Tag();
            tags.deleteArtworkField();
            tags.setField(cover);
            mp3.setID3v2Tag(tags);
            mp3.save();
            l.onArtworkUpdated();
        }
        file.delete();
    }

}
