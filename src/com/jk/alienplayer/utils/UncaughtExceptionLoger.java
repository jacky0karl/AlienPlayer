package com.jk.alienplayer.utils;

import java.lang.Thread.UncaughtExceptionHandler;

public final class UncaughtExceptionLoger implements UncaughtExceptionHandler {
    private static UncaughtExceptionLoger sSelf;
    private Thread.UncaughtExceptionHandler mDefaultHandler = null;

    public static synchronized UncaughtExceptionLoger getInstance() {
        if (sSelf == null) {
            sSelf = new UncaughtExceptionLoger();
        }
        return sSelf;
    }

    private UncaughtExceptionLoger() {
    }

    public void init() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable t) {
        FileSavingUtils.logThrowable(t);
        mDefaultHandler.uncaughtException(thread, t);
    }
}
