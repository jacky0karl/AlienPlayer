package com.jk.alienplayer;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.impl.PlayingHelper;
import com.jk.alienplayer.impl.PlayingHelper.PlayStatus;
import com.jk.alienplayer.impl.PlayingHelper.PlayingInfo;
import com.jk.alienplayer.metadata.SongInfo;
import com.jk.alienplayer.utils.PendingIntentUtils;

public class APWidgetProvider extends AppWidgetProvider {

    private static ComponentName sComponentName = new ComponentName(APWidgetProvider.class
            .getPackage().getName(), APWidgetProvider.class.getName());;

    @Override
    public void onReceive(Context context, Intent intent) {
        RemoteViews views = syncView(context, intent);
        if (views == null) {
            super.onReceive(context, intent);
            return;
        }

        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = manager.getAppWidgetIds(sComponentName);
        for (int i = 0; i < appWidgetIds.length; i++) {
            manager.updateAppWidget(appWidgetIds[i], views);
        }
    }

    private RemoteViews syncView(Context context, Intent intent) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.playbar);
        String action = intent.getAction();
        if (action.equals(PlayService.ACTION_START)) {
            int duration = intent.getIntExtra(PlayService.TOTAL_DURATION, 0);
            views.setImageViewResource(R.id.play, R.drawable.pause);
            views.setProgressBar(R.id.progressBar, duration, 0, false);
        } else if (action.equals(PlayService.ACTION_TRACK_CHANGE)) {
            String song = intent.getStringExtra(PlayService.SONG_NAME);
            String artist = intent.getStringExtra(PlayService.ARTIST_NAME);
            syncSongInfo(views, song, artist);
        } else if (action.equals(PlayService.ACTION_PAUSE)) {
            views.setImageViewResource(R.id.play, R.drawable.play);
        } else if (action.equals(PlayService.ACTION_STOP)) {
            views.setImageViewResource(R.id.play, R.drawable.play);
            views.setProgressBar(R.id.progressBar, 0, 0, false);
        } else if (action.equals(PlayService.ACTION_PROGRESS_UPDATE)) {
            int duration = intent.getIntExtra(PlayService.TOTAL_DURATION, 0);
            int progress = intent.getIntExtra(PlayService.CURRENT_DURATION, 0);
            views.setProgressBar(R.id.progressBar, duration, progress, false);
        } else {
            return null;
        }

        setOnClickEvents(context, views);
        return views;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {
            appWidgetManager.updateAppWidget(appWidgetIds[i], initView(context));
        }
    }

    private RemoteViews initView(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.remote_playbar);
        SongInfo songInfo = PlayingInfoHolder.getInstance().getCurrentSong();
        if (songInfo != null) {
            syncSongInfo(views, songInfo.title, songInfo.artist);
        }

        PlayingInfo info = PlayingHelper.getPlayingInfo();
        if (info.status == PlayStatus.Playing) {
            views.setImageViewResource(R.id.play, R.drawable.pause);
            views.setProgressBar(R.id.progressBar, info.duration, info.progress, false);
        } else if (info.status == PlayStatus.Paused) {
            views.setImageViewResource(R.id.play, R.drawable.play);
            views.setProgressBar(R.id.progressBar, info.duration, info.progress, false);
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
        views.setOnClickPendingIntent(R.id.artwork, PendingIntentUtils.getLaunchIntent(context));
        views.setOnClickPendingIntent(R.id.play, PendingIntentUtils.getPlayIntent(context));
        views.setOnClickPendingIntent(R.id.prev, PendingIntentUtils.getPrevIntent(context));
        views.setOnClickPendingIntent(R.id.next, PendingIntentUtils.getNextIntent(context));
    }
}
