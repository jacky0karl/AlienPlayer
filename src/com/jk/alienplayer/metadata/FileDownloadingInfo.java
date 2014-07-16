package com.jk.alienplayer.metadata;

public class FileDownloadingInfo {

    public enum Status {
        PENDING, DOWALOADING, FAILED, COMPLETED, CANCELED
    }

    public NetworkTrackInfo trackInfo;
    public Status status = Status.PENDING;
    public String url;

    /** Byte */
    public int size = 0;
    /** Byte */
    public int progress = 0;

    public FileDownloadingInfo(NetworkTrackInfo trackInfo) {
        this.trackInfo = trackInfo;
    }

}
