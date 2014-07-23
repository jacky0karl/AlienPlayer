package com.jk.alienplayer.metadata;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.http.util.EncodingUtils;

import android.text.TextUtils;

public class LyricInfo {
    private ArrayList<Sentence> mSentences;

    public LyricInfo(String filePath) {
        mSentences = new ArrayList<Sentence>();
        parseFile(filePath);
    }

    public boolean hasLyric() {
        return mSentences.size() > 0 ? true : false;
    }

    public ArrayList<Sentence> getSentences() {
        return mSentences;
    }

    public ArrayList<String> getLyric() {
        ArrayList<String> lyric = new ArrayList<String>();
        for (Sentence s : mSentences) {
            lyric.add(s.text);
        }
        return lyric;
    }

    public class Sentence {
        public int startTime;
        public String text;

        public Sentence(int startTime, String text) {
            this.startTime = startTime;
            this.text = text;
        }
    }

    class SentenceComparator implements Comparator<Sentence> {
        @Override
        public int compare(Sentence lhs, Sentence rhs) {
            return lhs.startTime - rhs.startTime;
        }
    }

    private void parseFile(String filePath) {
        String content = readFile(filePath);
        if (!TextUtils.isEmpty(content)) {
            String[] lines = content.split("\n");
            for (String line : lines) {
                parseLine(line);
            }
            Collections.sort(mSentences, new SentenceComparator());
        }
    }

    private void parseLine(String line) {
        String[] parts = line.split("]");
        int len = parts.length;
        if (len < 2) {
            mSentences.add(new Sentence(0, line));
            return;
        }

        String text = parts[len - 1];
        for (int i = 0; i < len - 1; i++) {
            String time = parts[i].substring(1);
            mSentences.add(new Sentence(stringToMsec(time), text));
        }
    }

    private String readFile(String filePath) {
        String content = null;
        try {
            FileInputStream fis = new FileInputStream(filePath);
            int length = fis.available();
            byte[] buffer = new byte[length];
            fis.read(buffer);
            content = EncodingUtils.getString(buffer, "utf-8");
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    private int stringToMsec(String str) {
        String[] arrayMsec = str.split("\\.");
        if (arrayMsec.length < 2) {
            return 0;
        }
        String msec = arrayMsec[1];
        String rest = arrayMsec[0];

        String[] arrayMin = rest.split(":");
        if (arrayMin.length < 2) {
            return 0;
        }
        String min = arrayMin[0];
        String sec = arrayMin[1];

        return Integer.parseInt(min) * 60 * 1000 + Integer.parseInt(sec) * 1000
                + Integer.parseInt(msec);
    }
}
