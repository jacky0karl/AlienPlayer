package com.jk.alienplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.utils.PendingIntentUtils;

public class NotificationHelper extends BroadcastReceiver {
    public static final int FOR_SERVICE = 1;
    private static final int FOR_UI = 2;
    private static final String CHANNEL_ID = "default";

    private Context mContext;
    private RemoteViews mViews;
    private String mArtist;
    private String mSong;

    @Override
    public void onReceive(Context context, Intent intent) {
        mViews = updateView(context, intent);
        if (mViews != null) {
            sendNotification(context, intent);
        }
    }

    public NotificationHelper(Context context) {
        mContext = context;
        IntentFilter intentFilter = new IntentFilter(PlayService.ACTION_START);
        intentFilter.addAction(PlayService.ACTION_TRACK_CHANGE);
        intentFilter.addAction(PlayService.ACTION_PAUSE);
        intentFilter.addAction(PlayService.ACTION_STOP);
        context.registerReceiver(this, intentFilter);
    }

    public void finish() {
        mContext.unregisterReceiver(this);
        NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(FOR_UI);
    }

    public static Notification getSeviceNotification(Context context) {
        return new Notification.Builder(context)
                .setSmallIcon(R.drawable.app_icon)
                .build();
    }

    public void sendNotification(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_icon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCustomContentView(mViews)
                .setCustomBigContentView(updateBigView(context, intent))
                .setOngoing(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, importance);
            manager.createNotificationChannel(channel);
        }
        manager.notify(FOR_UI, builder.build());
    }

    private RemoteViews updateView(Context context, Intent intent) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.notification);
        String action = intent.getAction();
        if (action.equals(PlayService.ACTION_START)) {
            views.setImageViewResource(R.id.play, R.drawable.icon_pause);
        } else if (action.equals(PlayService.ACTION_TRACK_CHANGE)) {
            mSong = intent.getStringExtra(PlayService.SONG_NAME);
            mArtist = intent.getStringExtra(PlayService.ARTIST_NAME);
        } else if (action.equals(PlayService.ACTION_PAUSE)) {
            views.setImageViewResource(R.id.play, R.drawable.icon_play);
        } else if (action.equals(PlayService.ACTION_STOP)) {
            views.setImageViewResource(R.id.play, R.drawable.icon_play);
        } else {
            return null;
        }

        views.setTextViewText(R.id.song, mSong);
        views.setTextViewText(R.id.artist, mArtist);
        setOnMainClickEvents(context, views);
        return views;
    }

    private RemoteViews updateBigView(Context context, Intent intent) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.notification_big);
        String action = intent.getAction();
        if (action.equals(PlayService.ACTION_START)) {
            views.setImageViewResource(R.id.play, R.drawable.icon_pause);
        } else if (action.equals(PlayService.ACTION_TRACK_CHANGE)) {
            mSong = intent.getStringExtra(PlayService.SONG_NAME);
            mArtist = intent.getStringExtra(PlayService.ARTIST_NAME);
        } else if (action.equals(PlayService.ACTION_PAUSE)) {
            views.setImageViewResource(R.id.play, R.drawable.icon_play);
        } else if (action.equals(PlayService.ACTION_STOP)) {
            views.setImageViewResource(R.id.play, R.drawable.icon_play);
        }

        syncSongInfo(views);
        setOnAllClickEvents(context, views);
        return views;
    }

    private void syncSongInfo(RemoteViews views) {
        views.setTextViewText(R.id.song, mSong);
        views.setTextViewText(R.id.artist, mArtist);
        Bitmap artwork = PlayingInfoHolder.getInstance().getPlaybarArtwork();
        if (artwork == null) {
            views.setImageViewResource(R.id.artwork, R.drawable.ic_disc);
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
