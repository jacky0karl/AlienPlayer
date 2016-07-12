package com.jk.alienplayer.metadata;

import com.jk.alienplayer.model.TrackBean;

public class FileDownloadingInfo {

    public enum Status {
        PENDING, DOWALOADING, FAILED, COMPLETED, CANCELED
    }

    public TrackBean trackInfo;
    public Status status = Status.PENDING;
    public String url;

    /** Byte */
    public int size = 0;
    /** Byte */
    public int progress = 0;

    public FileDownloadingInfo(TrackBean trackInfo) {
        this.trackInfo = trackInfo;
    }

}
