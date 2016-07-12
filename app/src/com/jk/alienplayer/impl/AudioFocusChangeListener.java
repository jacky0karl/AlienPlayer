package com.jk.alienplayer.impl;

import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;

public class AudioFocusChangeListener implements OnAudioFocusChangeListener {
    private PlayingHelper mPlayingHelper = null;

    public AudioFocusChangeListener(PlayingHelper playingHelper) {
        mPlayingHelper = playingHelper;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (mPlayingHelper == null) {
            return;
        }

        switch (focusChange) {
        case AudioManager.AUDIOFOCUS_GAIN:
            break;
        case AudioManager.AUDIOFOCUS_LOSS:
        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            mPlayingHelper.pause();
            break;
        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
            break;
        default:
            break;
        }
    }

}
