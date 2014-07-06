package com.jk.alienplayer;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.utils.PendingIntentUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

public class NotificationHelper extends BroadcastReceiver {
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
                .setContent(mViews).setPriority(Notification.PRIORITY_MAX).setOngoing(true).build();
        n.bigContentView = updateBigView(context, intent);

        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, n);
    }

    private RemoteViews updateView(Context context, Intent intent) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.notification);
        String action = intent.getAction();
        if (action.equals(PlayService.ACTION_START)) {
            views.setImageViewResource(R.id.play, R.drawable.pause);
        } else if (action.equals(PlayService.ACTION_TRACK_CHANGE)) {
            String song = intent.getStringExtra(PlayService.SONG_NAME);
            String artist = intent.getStringExtra(PlayService.ARTIST_NAME);
            // syncSongInfo(views, song, artist);
        } else if (action.equals(PlayService.ACTION_PAUSE)) {
            views.setImageViewResource(R.id.play, R.drawable.play);
        } else if (action.equals(PlayService.ACTION_STOP)) {
            views.setImageViewResource(R.id.play, R.drawable.play);
        } else if (action.equals(PlayService.ACTION_EXIT)) {
            NotificationManager mNotificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(0);
            return null;
        } else {
            return null;
        }

        // setOnClickEvents(context, views);
        return views;
    }

    private RemoteViews updateBigView(Context context, Intent intent) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.notification_big);
        String action = intent.getAction();
        if (action.equals(PlayService.ACTION_START)) {
            views.setImageViewResource(R.id.play, R.drawable.pause);
        } else if (action.equals(PlayService.ACTION_TRACK_CHANGE)) {
            String song = intent.getStringExtra(PlayService.SONG_NAME);
            String artist = intent.getStringExtra(PlayService.ARTIST_NAME);
            syncSongInfo(views, song, artist);
        } else if (action.equals(PlayService.ACTION_PAUSE)) {
            views.setImageViewResource(R.id.play, R.drawable.play);
        } else if (action.equals(PlayService.ACTION_STOP)) {
            views.setImageViewResource(R.id.play, R.drawable.play);
        }

        setOnClickEvents(context, views);
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

    private void setOnClickEvents(Context context, RemoteViews views) {
        views.setOnClickPendingIntent(R.id.artwork, PendingIntentUtils.getArtworkIntent(context));
        views.setOnClickPendingIntent(R.id.play, PendingIntentUtils.getPlayIntent(context));
        views.setOnClickPendingIntent(R.id.prev, PendingIntentUtils.getPrevIntent(context));
        views.setOnClickPendingIntent(R.id.next, PendingIntentUtils.getNextIntent(context));
        views.setOnClickPendingIntent(R.id.exit, PendingIntentUtils.getExitIntent(context));
    }
}
