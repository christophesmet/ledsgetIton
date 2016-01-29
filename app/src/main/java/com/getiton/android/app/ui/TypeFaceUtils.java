package com.getiton.android.app.ui;

import android.content.Context;
import android.graphics.Typeface;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * Created by Christophe on 20/06/2014.
 */

public class TypeFaceUtils {

    private static TypeFaceUtils mInstance;

    private Context mAppContext;

    private HashMap<String, Typeface> mTypefaceCache;

    public static TypeFaceUtils getInstance(@NotNull Context context) {
        if (mInstance == null) {
            mInstance = new TypeFaceUtils(context);
        }
        return mInstance;
    }

    private TypeFaceUtils(@NotNull Context context) {
        this.mAppContext = context.getApplicationContext();
        mTypefaceCache = new HashMap<String, Typeface>();
    }

    @Nullable
    public Typeface getTypeFaceByNameFromAssets(@NotNull String typeFaceFileName) {
        if (mTypefaceCache.containsKey(typeFaceFileName)) {
            return mTypefaceCache.get(typeFaceFileName);
        }
        else
        {
            Typeface output = Typeface.createFromAsset(mAppContext.getAssets(),typeFaceFileName);
            mTypefaceCache.put(typeFaceFileName,output);
            return output;
        }
    }

}