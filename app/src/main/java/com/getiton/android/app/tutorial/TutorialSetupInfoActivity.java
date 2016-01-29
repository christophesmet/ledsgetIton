package com.getiton.android.app.tutorial;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.getiton.android.app.core.ApplicationController;
import com.getiton.android.app.core.CoreActivity;
import com.getiton.android.app.core.view.WifiInfoView;
import com.getiton.android.app.tutorial.model.RegistrationConfig;
import com.getiton.android.app.ui.views.text.LgioTextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.christophesmet.getiton.library.GetItOn;
import com.christophesmet.getiton.library.core.discovery.wifi.model.WifiScanResult;
import com.christophesmet.getiton.library.core.module.WifiModule;
import com.christophesmet.getiton.library.utils.WifiUtils;
import com.christophesmet.ledsgetiton.R;
import rx.Observable;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;
import rx.functions.Func1;

/**
 * Created by christophesmet on 02/10/15.
 */

public class TutorialSetupInfoActivity extends CoreActivity {

    private static final String KEY_EXTRA_WIFIMODULE = "extra_wifimodule";

    @Inject
    protected GetItOn mGetItOn;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.img_state)
    ImageView mImgState;
    @InjectView(R.id.txt_security)
    LgioTextView mTxtSecurity;
    @InjectView(R.id.txt_pass)
    EditText mTxtPass;
    @InjectView(R.id.ck_show_pass)
    CheckBox mCkShowPass;
    @InjectView(R.id.lin_check_pass)
    LinearLayout mLinCheckPass;
    @InjectView(R.id.wifi_info)
    WifiInfoView mWifiInfo;
    @InjectView(R.id.btn_setup)
    Button mBtnSetup;
    @InjectView(R.id.txt_name)
    LgioTextView mTxtName;
    @InjectView(R.id.txt_custom_name)
    EditText mTxtCustomName;

    @Nullable
    private WifiModule mSelectedWifiModule;
    @Nullable
    private WifiScanResult mWifiScanResult;

    public static Intent createIntent(@NonNull Context context, @NonNull WifiModule wifiModule) {
        Intent output = new Intent(context, TutorialSetupInfoActivity.class);
        output.putExtra(KEY_EXTRA_WIFIMODULE, wifiModule);
        return output;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ApplicationController) getApplicationContext()).inject(this);
        setContentView(R.layout.tutorial_setup_info);
        ButterKnife.inject(this);
        loadListeners();
        loadData();
        validate();
    }

    private void loadListeners() {
        mLinCheckPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCkShowPass.setChecked(!mCkShowPass.isChecked());
                flipPassVisibility();
            }
        });
        mCkShowPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                flipPassVisibility();
            }
        });
        mTxtPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validate();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mBtnSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegistration();
            }
        });
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigateUp();
            }
        });
    }

    private void flipPassVisibility() {
        if (mCkShowPass.isChecked()) {
            mTxtPass.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            mTxtPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        mTxtPass.setSelection(mTxtPass.getText().length(), mTxtPass.getText().length());
    }

    private void loadData() {
        if (getIntent() == null || !getIntent().hasExtra(KEY_EXTRA_WIFIMODULE)) {
            return;
        }
        this.mSelectedWifiModule = getIntent().getExtras().getParcelable(KEY_EXTRA_WIFIMODULE);
        if (mSelectedWifiModule == null) {
            onNavigateUp();
            return;
        }

        final WifiInfo[] mCurrentWifiInfo = {WifiUtils.getCurrentWifiInfo(this)};
        if (mCurrentWifiInfo[0] == null) {
            onUnableToGetcurrentWifi();
            return;
        }
        AndroidObservable.bindActivity(this, mGetItOn.getRegistrator().getWifiUpdates())
                .flatMap(new Func1<WifiInfo, Observable<List<WifiScanResult>>>() {
                    @Override
                    public Observable<List<WifiScanResult>> call(WifiInfo wifiInfo) {
                        mCurrentWifiInfo[0] = wifiInfo;
                        return mGetItOn.getRegistrator()
                                .requestWifiScan(true);
                    }
                }).subscribe(new Subscriber<List<WifiScanResult>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<WifiScanResult> wifiScanResults) {
                for (WifiScanResult result : wifiScanResults) {
                    if (result.getBSSID().equals(mCurrentWifiInfo[0].getBSSID())) {
                        onWifiInfoLoaded(result);
                        break;
                    }
                }
            }
        });
    }

    private void onUnableToGetcurrentWifi() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.tutorial_setup_info_unable_to_get_current_wifi_info)
                .show();
    }


    private void onWifiInfoLoaded(@NonNull WifiScanResult scanResult) {
        this.mWifiScanResult = scanResult;
        mWifiInfo.bind(scanResult);
    }

    private void validate() {
        if (!hasValidSettings()) {
            mBtnSetup.setEnabled(false);
            return;
        }
        if (!checkPassword()) {
            mBtnSetup.setEnabled(false);
            return;
        }
        if (!checkFrequencyRange()) {
            mBtnSetup.setEnabled(false);
            return;
        }
        mBtnSetup.setEnabled(true);

    }

    private boolean hasValidSettings() {
        if (mSelectedWifiModule == null) {
            return false;
        }
        if (mWifiScanResult == null) {
            return false;
        }

        mTxtPass.setError(null);
        return true;
    }

    private boolean checkPassword() {
        if (mWifiScanResult.getSecurity() != WifiUtils.WifiSecurity.OPEN && mTxtPass.getText().toString().length() == 0) {
            //mTxtPass.setError(getString(R.string.tutorial_setup_info_error_pass_required));
            return false;
        }
        return true;
    }

    private boolean checkFrequencyRange() {
        if (mWifiScanResult == null) {
            return false;
        }
        return mWifiScanResult.is24Ghz();
    }

    private void startRegistration() {
        if (mWifiScanResult != null && mSelectedWifiModule != null) {
            Intent intent = TutorialRegisterFlowActivity.createIntent(this, new RegistrationConfig(mSelectedWifiModule, mWifiScanResult.getSSID(), mWifiScanResult.getBSSID(), mTxtPass.getText().toString().trim(), mTxtCustomName.getText().toString().trim()));
            startActivity(intent);
        }
    }
}