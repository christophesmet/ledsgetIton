package com.getiton.android.app.ui;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.getiton.android.app.ui.shadows.viewgroups.ShadowFrameLayout;


/**
 * Created by Christophe on 20/06/2014.
 */

public class ViewUtils {
    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static void makeVerticallyDragable(@NonNull final ShadowFrameLayout view, final float minY, final float maxY) {

        Log.d("ledsgetiton", "min y: " + minY);
        Log.d("ledsgetiton", "max y: " + maxY);
        final float[] rawYOffset = {0};
        final float middle = minY + ((maxY - minY) / 2);
        final float[] rawDown = new float[1];

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        rawDown[0] = event.getRawY();
                        rawYOffset[0] = event.getRawY() - v.getY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        //Is click ?
                        float requestY = event.getRawY() - rawYOffset[0];
                        if (Math.abs(event.getRawY() - rawDown[0]) <= 30) {
                            v.callOnClick();
                        } else {
                            ViewCompat.animate(view).withLayer().y(requestY <= middle ? minY : maxY);
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float requestedY = event.getRawY() - rawYOffset[0];
                        if (requestedY > maxY) {
                            requestedY = maxY;
                        } else if (requestedY < minY) {
                            requestedY = minY;
                        }
                        view.setY(requestedY);
                        return true;
                }
                return false;
            }
        });
    }
}