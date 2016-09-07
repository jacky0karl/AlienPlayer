package com.jk.alienplayer.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.view.View;

import com.jk.alienplayer.R;

public class UiUtils {

    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int generateStatusBarColor(int primaryColor) {
        float[] arrayOfFloat = new float[3];
        Color.colorToHSV(primaryColor, arrayOfFloat);
        arrayOfFloat[2] *= 0.9F;
        return Color.HSVToColor(arrayOfFloat);
    }

    public static int dpToPixel(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = (int) (dp * (float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static void setAppBarLayoutOffset(AppBarLayout appbar, int offset) {
        appbar.post(new Runnable() {
            @Override
            public void run() {
                CoordinatorLayout cl = (CoordinatorLayout) appbar.getParent();
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appbar.getLayoutParams();
                AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
                behavior.onNestedPreScroll(cl, appbar, null, 0, offset, new int[]{0, 0});
            }
        });
    }

    public static void showCommonSnackbar(View anchor, int message) {
        Snackbar snackbar = Snackbar.make(anchor, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, v -> {});
        snackbar.getView().setBackgroundColor(anchor.getResources().getColor(R.color.primary));
        snackbar.show();
    }
}
