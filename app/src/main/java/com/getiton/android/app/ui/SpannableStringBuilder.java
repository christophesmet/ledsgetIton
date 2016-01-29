package com.getiton.android.app.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;


import com.getiton.android.app.ui.views.text.TypefaceSpan;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Christophe on 17/08/2014.
 */

public class SpannableStringBuilder {

    private Context mContext;
    private SpannableString mInput;

    public SpannableStringBuilder(@NotNull Context context, SpannableString input) {
        this.mContext = context;
        mInput = input;
    }
    public SpannableStringBuilder(@NotNull Context context, CharSequence input) {
        this.mContext = context;
        mInput = new SpannableString(input);
    }

    public SpannableStringBuilder setSpan(@NotNull Object what, int start, int end, int flags) {
        if (end == -1) {
            end = mInput.length();
        }

        mInput.setSpan(what, start, end, flags);
        return this;
    }

    public SpannableStringBuilder setTypeface(@NotNull Typeface typeface) {
        return setSpan(new TypefaceSpan(typeface), 0, mInput.length(),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    }

    public SpannableStringBuilder setTypeface(@NotNull int typefaceResourceId) {
        return setTypeface(TypeFaceUtils.getInstance(mContext)
                .getTypeFaceByNameFromAssets(mContext.getString(typefaceResourceId)));
    }

    public SpannableString build() {
        return mInput;
    }
}