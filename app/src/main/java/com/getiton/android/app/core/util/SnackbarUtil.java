package com.getiton.android.app.core.util;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.christophesmet.ledsgetiton.R;


/**
 * Created by christophesmet on 28/09/15.
 */

public class SnackbarUtil {

    private final static int defaultDuration = Snackbar.LENGTH_LONG;

    public static void show(@NonNull Activity act, String body) {
        View v = act.getWindow().getDecorView();
        if (v != null) {
            Snackbar snackbar = Snackbar.make(v, body, defaultDuration);
            snackbar.show();
        }
    }

    public static void show(@NonNull Activity act, Throwable e, @NonNull View.OnClickListener clickListener) {
        View v = act.getWindow().getDecorView();
        if (v != null) {
            Snackbar snackbar = Snackbar.make(v, act.getString(R.string.error_unknown), defaultDuration);
            snackbar.setAction(act.getString(R.string.general_retry_action), clickListener);
            snackbar.show();
        }
    }

}