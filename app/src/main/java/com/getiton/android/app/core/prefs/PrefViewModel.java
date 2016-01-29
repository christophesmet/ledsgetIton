package com.getiton.android.app.core.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Created by christophesmet on 27/09/15.
 */

public class PrefViewModel {

    private static final String KEY_PREF_NAME = "lgio_prefs";

    private Context mContext;
    private SharedPreferences mPrefs;

    public PrefViewModel(Context context) {
        mContext = context;
        mPrefs = context.getSharedPreferences(KEY_PREF_NAME, Context.MODE_PRIVATE);
    }

    public int getInt(@NonNull String key, int defaultValue) {
        return mPrefs.getInt(key, defaultValue);
    }

    public void putInt(@NonNull String key, int value) {
        mPrefs.edit().putInt(key, value).apply();
    }
}