package com.getiton.android.app.overview.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.getiton.android.app.core.bindable.IBindableListItemView;
import com.getiton.android.app.overview.OverViewListItem;
import com.getiton.android.app.ui.views.text.LgioTextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.christophesmet.ledsgetiton.R;

/**
 * Created by christophesmet on 14/10/15.
 */

public class OverviewListItemView extends CardView implements IBindableListItemView<OverViewListItem> {
    @InjectView(R.id.txt_name)
    LgioTextView mTxtName;
    @InjectView(R.id.txt_desc)
    LgioTextView mTxtDesc;
    @InjectView(R.id.img_type)
    ImageView mImgType;

    public OverviewListItemView(Context context) {
        super(context);
    }

    public OverviewListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OverviewListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //R.layout.overview_module_listitemview
        ButterKnife.inject(this);
    }

    @Override
    public void bind(@NonNull OverViewListItem model, int position) {
        mTxtName.setText(model.getName());
        if (model.getLanModule() != null) {
            mImgType.setImageResource(R.drawable.img_sweestpot_list_connected);
            mTxtDesc.setText(model.getLanModule().getStatus().Type);
        } else {
            mImgType.setImageResource(R.drawable.img_sweestpot_list_setup);
            mTxtDesc.setText(getContext().getString(R.string.module_needs_configuration));
        }
    }
}