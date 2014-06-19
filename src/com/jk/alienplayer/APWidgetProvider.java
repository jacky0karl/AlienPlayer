package com.jk.alienplayer;

import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.ui.MainActivity;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.RemoteViews;

public class APWidgetProvider extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        RemoteViews views = syncView(context, intent);
        ComponentName name = new ComponentName(APWidgetProvider.class.getPackage().getName(),
                APWidgetProvider.class.getName());
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = manager.getAppWidgetIds(name);
        for (int i = 0; i < appWidgetIds.length; i++) {
            manager.updateAppWidget(appWidgetIds[i], views);
        }
    }

    private RemoteViews syncView(Context context, Intent intent) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);
        String action = intent.getAction();
        if (action.equals(PlayService.ACTION_START)) {
            int duration = intent.getIntExtra(PlayService.TOTAL_DURATION, 0);
            views.setImageViewResource(R.id.play, R.drawable.pause);
            views.setProgressBar(R.id.progressBar, duration, 0, false);
        } else if (action.equals(PlayService.ACTION_TRACK_CHANGE)) {
            Bitmap artwork = PlayingInfoHolder.getInstance().getPlaybarArtwork();
            if (artwork == null) {
                views.setImageViewResource(R.id.artwork, R.drawable.disk);
            } else {
                views.setImageViewBitmap(R.id.artwork, artwork);
            }
        } else if (action.equals(PlayService.ACTION_PAUSE)) {
            views.setImageViewResource(R.id.play, R.drawable.play);
        } else if (action.equals(PlayService.ACTION_STOP)) {
            views.setImageViewResource(R.id.play, R.drawable.play);
            views.setProgressBar(R.id.progressBar, 0, 0, false);
        } else if (action.equals(PlayService.ACTION_PROGRESS_UPDATE)) {
            int duration = intent.getIntExtra(PlayService.TOTAL_DURATION, 0);
            int progress = intent.getIntExtra(PlayService.CURRENT_DURATION, 0);
            views.setProgressBar(R.id.progressBar, duration, progress, false);
        }
        views.setOnClickPendingIntent(R.id.artwork, getArtworkIntent(context));
        views.setOnClickPendingIntent(R.id.play, getPlayIntent(context));
        return views;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);
        views.setOnClickPendingIntent(R.id.artwork, getArtworkIntent(context));
        views.setOnClickPendingIntent(R.id.play, getPlayIntent(context));

        Bitmap artwork = PlayingInfoHolder.getInstance().getPlaybarArtwork();
        if (artwork == null) {
            views.setImageViewResource(R.id.artwork, R.drawable.disk);
        } else {
            views.setImageViewBitmap(R.id.artwork, artwork);
        }

        for (int i = 0; i < appWidgetIds.length; i++) {
            appWidgetManager.updateAppWidget(appWidgetIds[i], views);
        }
    }

    private PendingIntent getArtworkIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getPlayIntent(Context context) {
        Intent intent = PlayService
                .getPlayingCommandIntent(context, PlayService.COMMAND_PLAY_PAUSE);       
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
