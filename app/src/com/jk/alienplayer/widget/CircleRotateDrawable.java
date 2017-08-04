package com.jk.alienplayer.widget;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.support.annotation.Nullable;

/**
 * Created by jacky on 04/08/2017.
 */

public class CircleRotateDrawable extends RotateDrawable {

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        Path path = new Path();
        int x = getBounds().width() / 2;
        int y = getBounds().height() / 2;
        int radius = Math.min(x, y);
        path.addCircle(x, y, radius, Path.Direction.CW);
        canvas.clipPath(path);
        super.draw(canvas);
        canvas.restore();
    }

    @Override
    public void setDrawable(@Nullable Drawable dr) {
        super.setDrawable(dr);
    }
}
