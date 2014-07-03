package com.jk.alienplayer.utils;

import java.io.File;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.ID3v24Tag;

import com.jk.alienplayer.metadata.NetworkTrackInfo;

public class Mp3InfoUtils {

    public static void writeMp3Tags(NetworkTrackInfo info, String filePath) {
        File file = new File(filePath);
        try {
            MP3File mp3 = (MP3File) AudioFileIO.read(file);
            ID3v24Tag tag = mp3.getID3v2TagAsv24();

            tag.setField(FieldKey.ARTIST, info.artists);
            tag.setField(FieldKey.ALBUM_ARTIST, info.artistAlbum);
            tag.setField(FieldKey.ALBUM, info.album);
            tag.setField(FieldKey.TITLE, info.name);
            tag.setField(FieldKey.TRACK, String.valueOf(info.position));

            mp3.setID3v2Tag(tag);
            mp3.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
