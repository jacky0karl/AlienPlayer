package com.jk.alienplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.utils.PendingIntentUtils;

public class NotificationHelper extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1;
    private RemoteViews mViews;

    @Override
    public void onReceive(Context context, Intent intent) {
        mViews = updateView(context, intent);
        if (mViews != null) {
            sendNotification(context, intent);
        }
    }

    public void sendNotification(Context context, Intent intent) {
        Notification n = new Notification.Builder(context).setSmallIcon(R.drawable.app_icon)
                .setPriority(Notification.PRIORITY_MAX)
                .setContent(mViews).setOngoing(true).build();
        n.bigContentView = updateBigView(context, intent);

        NotificationManager manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, n);
    }

    private RemoteViews updateView(Context context, Intent intent) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.notification);
        String action = intent.getAction();
        if (action.equals(PlayService.ACTION_START)) {
            views.setImageViewResource(R.id.play, R.drawable.icon_pause);
        } else if (action.equals(PlayService.ACTION_TRACK_CHANGE)) {
            String song = intent.getStringExtra(PlayService.SONG_NAME);
            String artist = intent.getStringExtra(PlayService.ARTIST_NAME);
            views.setTextViewText(R.id.song, song);
            views.setTextViewText(R.id.artist, artist);
        } else if (action.equals(PlayService.ACTION_PAUSE)) {
            views.setImageViewResource(R.id.play, R.drawable.icon_play);
        } else if (action.equals(PlayService.ACTION_STOP)) {
            views.setImageViewResource(R.id.play, R.drawable.icon_play);
        } else if (action.equals(PlayService.ACTION_EXIT)) {
            NotificationManager manager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(NOTIFICATION_ID);
            return null;
        } else {
            return null;
        }

        setOnMainClickEvents(context, views);
        return views;
    }

    private RemoteViews updateBigView(Context context, Intent intent) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.notification_big);
        String action = intent.getAction();
        if (action.equals(PlayService.ACTION_START)) {
            views.setImageViewResource(R.id.play, R.drawable.icon_pause);
        } else if (action.equals(PlayService.ACTION_TRACK_CHANGE)) {
            String song = intent.getStringExtra(PlayService.SONG_NAME);
            String artist = intent.getStringExtra(PlayService.ARTIST_NAME);
            syncSongInfo(views, song, artist);
        } else if (action.equals(PlayService.ACTION_PAUSE)) {
            views.setImageViewResource(R.id.play, R.drawable.icon_play);
        } else if (action.equals(PlayService.ACTION_STOP)) {
            views.setImageViewResource(R.id.play, R.drawable.icon_play);
        }

        setOnAllClickEvents(context, views);
        return views;
    }

    private void syncSongInfo(RemoteViews views, String song, String artist) {
        views.setTextViewText(R.id.song, song);
        views.setTextViewText(R.id.artist, artist);
        Bitmap artwork = PlayingInfoHolder.getInstance().getPlaybarArtwork();
        if (artwork == null) {
            views.setImageViewResource(R.id.artwork, R.drawable.disk);
        } else {
            views.setImageViewBitmap(R.id.artwork, artwork);
        }
    }

    private void setOnMainClickEvents(Context context, RemoteViews views) {
        views.setOnClickPendingIntent(R.id.root, PendingIntentUtils.getLaunchIntent(context));
        views.setOnClickPendingIntent(R.id.play, PendingIntentUtils.getPlayIntent(context));
    }

    private void setOnAllClickEvents(Context context, RemoteViews views) {
        setOnMainClickEvents(context, views);
        views.setOnClickPendingIntent(R.id.prev, PendingIntentUtils.getPrevIntent(context));
        views.setOnClickPendingIntent(R.id.next, PendingIntentUtils.getNextIntent(context));
        views.setOnClickPendingIntent(R.id.exit, PendingIntentUtils.getExitIntent(context));
    }
}
