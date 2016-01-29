package com.getiton.android.app.ui.views.text;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.christophesmet.ledsgetiton.R;
import com.getiton.android.app.ui.TypeFaceUtils;


/**
 * Created by Christophe on 20/06/2014.
 */

public class LgioButton extends Button {

    public LgioButton(Context context) {
        super(context);
    }

    public LgioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public LgioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        if (!isInEditMode()) {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.LgioTextView,
                    defStyle,
                    0);

            if (a != null) {
                try {
                    String typeFaceFileName
                            = a.getString(R.styleable.LgioTextView_typeface_file_name);
                    Typeface typeFace = TypeFaceUtils.getInstance(context)
                            .getTypeFaceByNameFromAssets(typeFaceFileName);
                    if (typeFace != null) {
                        setTypeface(typeFace);
                    }
                } finally {
                    a.recycle();
                }
            }
        }
    }
}