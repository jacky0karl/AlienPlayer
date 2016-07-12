package com.jk.alienplayer;

import com.jk.alienplayer.impl.PlayService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

public class MediaButtonReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent key = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (key.getAction() == KeyEvent.ACTION_DOWN) {
                switch (key.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    Intent intentPlay = PlayService.getPlayingCommandIntent(context,
                            PlayService.COMMAND_PLAY_PAUSE);
                    context.startService(intentPlay);
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    Intent intentNext = PlayService.getPlayingCommandIntent(context,
                            PlayService.COMMAND_NEXT);
                    context.startService(intentNext);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    Intent intentPrev = PlayService.getPlayingCommandIntent(context,
                            PlayService.COMMAND_PREV);
                    context.startService(intentPrev);
                    break;
                default:
                    break;
                }
            }
            abortBroadcast();
        }
    }

}
