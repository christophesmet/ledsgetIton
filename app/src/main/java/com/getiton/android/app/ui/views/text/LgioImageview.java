package com.getiton.android.app.ui.views.text;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by christophesmet on 26/01/15.
 */

public class LgioImageview extends ImageView {
    private ColorMatrix mMatrix = new ColorMatrix();

    private float mSaturation = 1f;
    private float mBrightness = 0f;

    public LgioImageview(Context context) {
        super(context);
    }

    public LgioImageview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LgioImageview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setSaturation(float sat) {
        mSaturation = sat;
        remakeColorMatrix();

    }

    private void remakeColorMatrix() {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            mMatrix.set(new float[]{
                    1, 0, 0, 0, mBrightness, 0, 1,
                    0, 0, mBrightness,//
                    0, 0, 1, 0, mBrightness, 0, 0, 0, 1, 0});
            mMatrix.setSaturation(mSaturation);
            ColorFilter filter = new ColorMatrixColorFilter(mMatrix);
            drawable.setColorFilter(filter);
        }
    }

    public void setBrightness(float brightness) {
        mBrightness = brightness;
        remakeColorMatrix();
    }
}