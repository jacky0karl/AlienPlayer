package com.jk.alienplayer.data;

import java.io.File;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.ID3v24Tag;

import android.text.TextUtils;

import com.jk.alienplayer.metadata.NetworkTrackInfo;
import com.jk.alienplayer.metadata.TrackTagInfo;

public class Mp3TagsHelper {

    public static void writeMp3Tags(NetworkTrackInfo info, String filePath) {
        writeMp3Tags(info.name, info.artists, info.album, info.artistAlbum,
                String.valueOf(info.position), filePath);
    }

    public static void writeMp3Tags(String title, String artists, String album, String artistAlbum,
            String track, String filePath) {
        File file = new File(filePath);
        try {
            MP3File mp3 = (MP3File) AudioFileIO.read(file);
            ID3v24Tag tag = mp3.getID3v2TagAsv24();

            tag.setField(FieldKey.ARTIST, artists);
            tag.setField(FieldKey.ALBUM_ARTIST, artistAlbum);
            tag.setField(FieldKey.ALBUM, album);
            tag.setField(FieldKey.TITLE, title);
            tag.setField(FieldKey.TRACK, track);

            mp3.setID3v2Tag(tag);
            mp3.save();
        } catch (Exception e) {
            e.printStackTrace();
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
            ID3v24Tag tag = mp3.getID3v2TagAsv24();

            info.setTitle(tag.getFirst(FieldKey.TITLE));
            info.setArtists(tag.getFirst(FieldKey.ARTIST));
            info.setAlbum(tag.getFirst(FieldKey.ALBUM));
            info.setArtistAlbum(tag.getFirst(FieldKey.ALBUM_ARTIST));
            info.setTrack(tag.getFirst(FieldKey.TRACK));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }
}
