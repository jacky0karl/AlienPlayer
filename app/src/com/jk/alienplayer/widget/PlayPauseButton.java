package com.jk.alienplayer.widget;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;

/**
 * Created by jk on 2015/11/24.
 */
public class PlayPauseButton extends FloatingActionButton {
    private PlayPauseDrawable mPlayBtnDrawable;

    public PlayPauseButton(Context context) {
        super(context);
        init();
    }

    public PlayPauseButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayPauseButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPlayBtnDrawable = new PlayPauseDrawable();
        setImageDrawable(mPlayBtnDrawable);
        mPlayBtnDrawable.transformToPlay(false);
    }

    public void transformToPlay(boolean animated) {
        mPlayBtnDrawable.transformToPlay(animated);
    }

    public void transformToPause(boolean animated) {
        mPlayBtnDrawable.transformToPause(animated);
    }
}
