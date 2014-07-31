package com.jk.alienplayer.impl;

public class VolumeKeyHelper {
    private static boolean sSelfChangeVolume = false;

    public static boolean isSelfChangeVolume() {
        return sSelfChangeVolume;
    }

    public static void setSelfChangeVolume(boolean selfChangeVolume) {
        sSelfChangeVolume = selfChangeVolume;
    }
}
