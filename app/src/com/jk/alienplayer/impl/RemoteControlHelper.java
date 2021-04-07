package com.jk.alienplayer.impl;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;
import android.media.RemoteControlClient.MetadataEditor;

import com.jk.alienplayer.data.PlayingInfoHolder;

public class RemoteControlHelper {
    private Context mContext;
    private RemoteControlClient mRemoteControlClient;
    private AudioManager mAudioManager;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PlayService.ACTION_TRACK_CHANGE)) {
                String title = intent.getStringExtra(PlayService.SONG_NAME);
                String artist = intent.getStringExtra(PlayService.ARTIST_NAME);
                setContent(title, artist);
            } else if (intent.getAction().equals(PlayService.ACTION_START)) {
                mRemoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
            } else if (intent.getAction().equals(PlayService.ACTION_PAUSE)) {
                mRemoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
            } else if (intent.getAction().equals(PlayService.ACTION_STOP)) {
                mRemoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_STOPPED);
            }
        }
    };

    public RemoteControlHelper(Context context, ComponentName mediaButtonReceiver) {
        if (context == null || mediaButtonReceiver == null) {
            return;
        }
        mContext = context;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setComponent(mediaButtonReceiver);
        PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(mContext, 0,
                mediaButtonIntent, 0);

        mRemoteControlClient = new RemoteControlClient(mediaPendingIntent);
        mRemoteControlClient.setTransportControlFlags(RemoteControlClient.FLAG_KEY_MEDIA_PLAY
                | RemoteControlClient.FLAG_KEY_MEDIA_PAUSE
                | RemoteControlClient.FLAG_KEY_MEDIA_NEXT
                | RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS);

        IntentFilter intentFilter = new IntentFilter(PlayService.ACTION_TRACK_CHANGE);
        intentFilter.addAction(PlayService.ACTION_START);
        intentFilter.addAction(PlayService.ACTION_PAUSE);
        intentFilter.addAction(PlayService.ACTION_STOP);
        mContext.registerReceiver(mReceiver, intentFilter);
        registerRemoteControlClient();
    }

    public void finish() {
        unregisterRemoteControlClient();
        mContext.unregisterReceiver(mReceiver);
    }

    private void registerRemoteControlClient() {
        mAudioManager.registerRemoteControlClient(mRemoteControlClient);
    }

    private void unregisterRemoteControlClient() {
        mAudioManager.unregisterRemoteControlClient(mRemoteControlClient);
    }

    private void setContent(String title, String artist) {
        Bitmap artwork = PlayingInfoHolder.getInstance().getPlaybarArtwork();
        mRemoteControlClient.editMetadata(true)
                .putBitmap(MetadataEditor.BITMAP_KEY_ARTWORK, artwork)
                .putString(MediaMetadataRetriever.METADATA_KEY_TITLE, title)
                .putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, artist)
                .apply();
    }
}
