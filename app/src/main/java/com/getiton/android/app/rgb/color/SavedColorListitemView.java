package com.getiton.android.app.rgb.color;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;


import com.getiton.android.app.core.bindable.IBindableListItemView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.christophesmet.ledsgetiton.R;

/**
 * Created by christophesmet on 02/11/15.
 */

public class SavedColorListitemView extends FrameLayout implements IBindableListItemView<SavedColor> {


    @InjectView(R.id.img)
    ImageView mImg;

    @NonNull
    SavedColor color;

    public SavedColorListitemView(Context context) {
        super(context);
    }

    public SavedColorListitemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SavedColorListitemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //R.layout.savedcolor_listitemview
        ButterKnife.inject(this);
    }

    @Override
    public void bind(@NonNull SavedColor model, int position) {
        this.color = model;
        if (model.isAddNewColor()) {
            mImg.setBackgroundColor(getContext().getResources().getColor(R.color.add_new_color_bg));
            mImg.setImageResource(R.drawable.ic_add);
        } else {
            mImg.setBackgroundColor(model.getColor());
            mImg.setImageResource(android.R.color.transparent);
        }
    }

    @NonNull
    public SavedColor getColor() {
        return color;
    }
}