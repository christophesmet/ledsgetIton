package com.getiton.android.app.ui.shadows.viewgroups;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by christophesmet on 04/12/14.
 */

public class ShadowFrameLayout extends FrameLayout {

    private List<OnTouchListener> mShadowTouchListeners;
    private List<OnTouchListener> mInterceptTouchListeners;


    public ShadowFrameLayout(Context context) {
        super(context);
        init();
    }

    public ShadowFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShadowFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mShadowTouchListeners = new ArrayList<>();
        mInterceptTouchListeners = new ArrayList<>();
    }

    public void addShadowTouchListener(@NotNull OnTouchListener listener) {
        if (!mShadowTouchListeners.contains(listener)) {
            mShadowTouchListeners.add(listener);
        }
    }

    public void addInterceptTouchListener(@NonNull OnTouchListener listener) {
        if (!mInterceptTouchListeners.contains(listener)) {
            mInterceptTouchListeners.add(listener);
        }
    }

    public void removeInterceptTouchListener(@NonNull OnTouchListener listener) {
        if (mInterceptTouchListeners.contains(listener)) {
            mInterceptTouchListeners.remove(listener);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean someoneResponded = false;
        for (OnTouchListener listener : mShadowTouchListeners) {
            if (listener != null) {
                if (listener.onTouch(this, event)) {
                    someoneResponded = true;
                }
            }
        }
        if (someoneResponded) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean someoneResponded = false;
        for (OnTouchListener listener : mInterceptTouchListeners) {
            if (listener != null) {
                if (listener.onTouch(this, ev)) {
                    someoneResponded = true;
                }
            }
        }
        if (someoneResponded) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        addShadowTouchListener(l);
    }

}