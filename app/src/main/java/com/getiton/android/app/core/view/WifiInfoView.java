package com.getiton.android.app.core.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.christophesmet.ledsgetiton.R;
import com.getiton.android.app.core.bindable.IBindableView;
import com.getiton.android.app.ui.views.text.LgioTextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.christophesmet.getiton.library.core.discovery.wifi.model.WifiScanResult;

/**
 * Created by christophesmet on 02/10/15.
 */

public class WifiInfoView extends LinearLayout implements IBindableView<WifiScanResult> {
    @InjectView(R.id.txt_name)
    LgioTextView mTxtName;
    @InjectView(R.id.txt_security)
    LgioTextView mTxtSecurity;
    @InjectView(R.id.img_state)
    ImageView mImgState;

    public WifiInfoView(Context context) {
        super(context);
    }

    public WifiInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WifiInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WifiInfoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //R.layout.wifiscan_info_view
        ButterKnife.inject(this);
    }


    @Override
    public void bind(@NonNull WifiScanResult model) {
        mTxtName.setText(model.getSSID());
        String securityText;
        switch (model.getSecurity()) {
            case OPEN:
                securityText = getContext().getString(R.string.general_security_wifi_open);
                mImgState.setImageResource(R.drawable.ic_wifi_supported);
                break;
            case WEP:
                securityText = getContext().getString(R.string.general_security_wifi_wep);
                mImgState.setImageResource(R.drawable.ic_wifi_supported);
                break;
            case WPA2_PSK:
                securityText = getContext().getString(R.string.general_security_wifi_wpa2);
                mImgState.setImageResource(R.drawable.ic_wifi_supported);
                break;
            case WPA2:
                securityText = getContext().getString(R.string.general_security_wifi_wpa2);
                mImgState.setImageResource(R.drawable.ic_wifi_supported);
                break;
            default:
                securityText = "";
                break;
        }
        mTxtSecurity.setText(securityText);
        if (!model.is24Ghz()) {
            mImgState.setImageResource(R.drawable.ic_wifi_unsupported);
            mTxtSecurity.setText(getContext().getString(R.string.general_securiry_wifi_not_24ghz));
        }
    }
}