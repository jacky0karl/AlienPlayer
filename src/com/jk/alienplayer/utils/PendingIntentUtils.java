package com.jk.alienplayer.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.ui.MainActivity;

public class PendingIntentUtils {

    public static PendingIntent getArtworkIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return PendingIntent.getActivity(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getPlayIntent(Context context) {
        Intent intent = PlayService
                .getPlayingCommandIntent(context, PlayService.COMMAND_PLAY_PAUSE);
        return PendingIntent.getService(context, PlayService.COMMAND_PLAY_PAUSE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getPrevIntent(Context context) {
        Intent intent = PlayService.getPlayingCommandIntent(context, PlayService.COMMAND_PREV);
        return PendingIntent.getService(context, PlayService.COMMAND_PREV, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getNextIntent(Context context) {
        Intent intent = PlayService.getPlayingCommandIntent(context, PlayService.COMMAND_NEXT);
        return PendingIntent.getService(context, PlayService.COMMAND_NEXT, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
    
    public static PendingIntent getExitIntent(Context context) {
        Intent intent = PlayService.getPlayingCommandIntent(context, PlayService.COMMAND_EXIT);
        return PendingIntent.getService(context, PlayService.COMMAND_EXIT, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
