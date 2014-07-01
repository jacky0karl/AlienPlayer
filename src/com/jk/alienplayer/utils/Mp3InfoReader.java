package com.jk.alienplayer.utils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v24Frames;

import android.util.Log;

public class Mp3InfoReader {

    public static void test(String filePath) {
        File file = new File(filePath);
        try {
            MP3File f = (MP3File) AudioFileIO.read(file);
            AbstractID3v2Tag tag = f.getID3v2Tag();
            String artist = tag.getFirst(ID3v24Frames.FRAME_ID_ARTIST);

            Iterator<TagField> it = tag.getFields();
            while (it.hasNext()) {
                TagField tf = it.next();
                Log.e("#############", tf.getId() + ", " + tf.toString());
            }
        } catch (CannotReadException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TagException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ReadOnlyFileException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidAudioFrameException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
