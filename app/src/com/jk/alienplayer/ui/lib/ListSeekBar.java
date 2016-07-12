package com.jk.alienplayer.ui.lib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ListSeekBar extends View {
    private static final String BG_COLOR = "#40000000";
    private static final int TEXT_COLOR = 0xFF404040;
    private static final String[] DEFAULT_INDICATORS = { "#", "A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
            "Y", "Z" };

    public interface OnIndicatorChangedListener {
        void onIndicatorShow();

        void onIndicatorDismiss();

        void onIndicatorChange(String indicator);
    }

    private OnIndicatorChangedListener mListener = null;
    private Paint mPaint;
    private String[] mIndicators;

    private boolean mIsPressed = false;
    private int mChosen = -1;
    private int mWidth = -1;
    private int mHeight = -1;
    private int mSingleHeight = -1;

    public ListSeekBar(Context context) {
        super(context);
        init(context);
    }

    public ListSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ListSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mIndicators = DEFAULT_INDICATORS;
        mPaint = new Paint();
        mPaint.setColor(TEXT_COLOR);
        mPaint.setAntiAlias(true);
    }

    public void setIndicators(String[] indicators) {
        if (indicators != null && indicators.length > 0) {
            mIndicators = indicators;
        }
    }

    public void setOnIndicatorChangedListener(OnIndicatorChangedListener listener) {
        mListener = listener;
    }

    protected void onDraw(Canvas canvas) {
        if (mIsPressed) {
            canvas.drawColor(Color.parseColor(BG_COLOR));
        }

        calculateSingleHeight();
        for (int i = 0; i < mIndicators.length; i++) {
            float xPos = mWidth / 2 - mPaint.measureText(mIndicators[i]) / 2;
            float yPos = mSingleHeight * i + mSingleHeight * 3 / 4;
            canvas.drawText(mIndicators[i], xPos, yPos, mPaint);
        }
    }

    private void calculateSingleHeight() {
        if (mSingleHeight < 0) {
            mWidth = getWidth();
            mHeight = getHeight();

            mSingleHeight = mHeight / mIndicators.length;
            mPaint.setTextSize((float) (mSingleHeight * 0.7));
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_MOVE:
            onActionMove(event);
            break;
        case MotionEvent.ACTION_DOWN:
            onActionDown();
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            onActionUp();
            break;
        default:
            break;
        }
        return true;
    }

    private void onActionDown() {
        mIsPressed = true;
        invalidate();

        if (mListener != null) {
            mListener.onIndicatorShow();
        }
    }

    private void onActionUp() {
        mIsPressed = false;
        mChosen = -1;
        invalidate();

        if (mListener != null) {
            mListener.onIndicatorDismiss();
        }
    }

    private void onActionMove(MotionEvent event) {
        int oldChosen = mChosen;
        mChosen = (int) (event.getY() / getHeight() * mIndicators.length);

        if (oldChosen != mChosen) {
            if (mChosen >= 0 && mChosen < mIndicators.length) {
                if (mListener != null) {
                    mListener.onIndicatorChange(mIndicators[mChosen]);
                }
            }
        }
    }

}
