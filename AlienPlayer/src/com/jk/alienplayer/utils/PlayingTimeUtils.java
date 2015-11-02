package com.jk.alienplayer.utils;

public class PlayingTimeUtils {

    public static String toDisplayTime(long msec) {
        int tmpSec = (int) (msec / 1000);
        int min = tmpSec / 60;
        int sec = tmpSec % 60;

        StringBuilder sb = new StringBuilder();
        if (min < 10) {
            sb.append('0');
        }
        sb.append(min);
        sb.append(':');
        if (sec < 10) {
            sb.append('0');
        }
        sb.append(sec);
        return sb.toString();
    }
}
