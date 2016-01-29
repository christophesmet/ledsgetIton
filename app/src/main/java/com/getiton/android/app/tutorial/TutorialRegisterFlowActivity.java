package com.getiton.android.app.tutorial;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;
import com.getiton.android.app.core.ApplicationController;
import com.getiton.android.app.core.CoreActivity;
import com.getiton.android.app.core.data.NamedLanModulesViewModel;
import com.getiton.android.app.overview.OverviewActivity;
import com.getiton.android.app.rgb.RgbV1Activity;
import com.getiton.android.app.tutorial.model.RegistrationConfig;
import com.getiton.android.app.ui.views.text.LgioTextView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.christophesmet.getiton.library.GetItOn;
import com.christophesmet.getiton.library.core.discovery.lan.repo.model.LanModule;
import com.christophesmet.getiton.library.core.discovery.wifi.WifiDiscoverer;
import com.christophesmet.getiton.library.core.register.RegistrationState;
import com.christophesmet.getiton.library.logging.ILoggingService;
import com.christophesmet.ledsgetiton.R;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;

/**
 * Created by christophesmet on 03/10/15.
 */

public class TutorialRegisterFlowActivity extends CoreActivity {

    private static final String KEY_CONFIG = "config";

    @Inject
    protected GetItOn mGetItOn;
    @Inject
    protected ILoggingService mLoggingService;
    @Inject
    protected NamedLanModulesViewModel mNamedLanModulesViewModel;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.txt_status)
    LgioTextView mTxtStatus;
    @InjectView(R.id.prg_loading)
    ProgressBar mPrgLoading;
    @InjectView(R.id.txt_title)
    LgioTextView mTxtTitle;

    @Nullable
    private RegistrationConfig mConfig;

    private WifiDiscoverer mWifiDiscoverer;

    private int mLanScanCounter = 0;

    public static Intent createIntent(@NonNull CoreActivity act, @NonNull RegistrationConfig config) {
        Intent output = new Intent(act, TutorialRegisterFlowActivity.class);
        output.putExtra(KEY_CONFIG, config);
        return output;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_register_flow_activity);
        ButterKnife.inject(this);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigateUp();
            }
        });
        ((ApplicationController) getApplicationContext()).inject(this);
        loadData();
    }

    private void loadData() {
        if (getIntent() != null && getIntent().hasExtra(KEY_CONFIG)) {
            mConfig = getIntent().getParcelableExtra(KEY_CONFIG);
            if (mConfig != null) {
                mTxtTitle.setText(getString(R.string.tutorial_register_flow_title).replace("{{name}}", mConfig.getName()));
                startRegistration();
            }
        }
    }

    private void startRegistration() {
        mLanScanCounter = 0;
        if (mConfig == null) {
            return;
        }
        AndroidObservable.bindActivity(this,
                mGetItOn.getRegistrator()
                        .registerModuleToNetwork(mConfig.getTargetBSSID(), mConfig.getTargetSSID(), mConfig.getWifiModule().getSSID(), mConfig.getWifiModule().getBSSID(), mConfig.getTargetPass(), "Get It On")
        ).subscribe(new Subscriber<RegistrationState>() {
            @Override
            public void onCompleted() {
                showloading(mPrgLoading, false);
            }

            @Override
            public void onError(Throwable e) {
                mLoggingService.log(e);
                Crashlytics.logException(e);
                showloading(mPrgLoading, false);
                showError();
            }

            @Override
            public void onNext(RegistrationState registrationState) {
                onUpdatedRegistrationState(registrationState);
                mLoggingService.log("Flow act received registration event : " + registrationState.name());
                if (registrationState == RegistrationState.REGISTRATION_STATE_LAN_SCAN_MODULE_FOUND) {
                    moduleFound((String) registrationState.getExtra());
                    this.unsubscribe();
                }
                if (registrationState == RegistrationState.REGISTRATION_STATE_DONE) {
                    lanScanCompleted();
                }
                if (registrationState == RegistrationState.REGISTRATION_STATE_FAILED) {
                    showError();
                }
            }
        });
    }

    private void showError() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.general_error_title)
                .setMessage(R.string.tutorial_register_state_error)
                .setPositiveButton(R.string.general_ok_got_it, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        navigateBackToOverView();
                    }
                });
    }

    private void onUpdatedRegistrationState(@NonNull RegistrationState registrationState) {
        int statusResourceId = getStatusStringResourceId(registrationState);
        if (statusResourceId == -1) {
            //todo: shit broken ?
        } else {
            mTxtStatus.setText(getStatusStringResourceId(registrationState));
        }
    }

    private void lanScanCompleted() {
        mLoggingService.log("Lan scan completed");
        onNoModuleFoundOnLanScan();
    }

    private void moduleFound(@NonNull String mac) {
        mLoggingService.log("Lan module found !");
        LanModule module = mGetItOn.queryCachedLanModuleForMac(mac);
        if (module != null) {
            mNamedLanModulesViewModel.addNewModule(mConfig.getName(), mac);
            mLoggingService.log("Found a module in the db ! -> " + module.toString());
            openDetail(mac);
        }
    }

    private void onNoModuleFoundOnLanScan() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.general_error_title)
                .setMessage(R.string.tutorial_register_scan_done_non_found_body)
                .setPositiveButton(R.string.general_ok_got_it, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        navigateBackToOverView();
                    }
                });
    }

    private void openDetail(@NonNull String macId) {
        mLoggingService.log("Opening detail for id: " + macId);
        Intent intent = RgbV1Activity.creatIntent(this, macId, 0xFF33000);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }

    private void navigateBackToOverView() {
        Intent intent = new Intent(this, OverviewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private String getStatusForRegistrationState(@NonNull RegistrationState state) {
        if (state == RegistrationState.REGISTRATION_STATE_LAN_SCAN_FOR_ADDED_MODULE) {
            return getString(getStatusStringResourceId(state)) + " " + mLanScanCounter;
        } else {
            return getString(getStatusStringResourceId(state));
        }
    }

    private int getStatusStringResourceId(@NonNull RegistrationState state) {
        switch (state) {
            case REGISTRATION_STATE_STARTING:
                return R.string.tutorial_register_state_starting;
            case REGISTRATION_STATE_CONNECTING:
                return R.string.tutorial_register_state_connecting;
            case REGISTRATION_STATE_CONNECTED:
                return R.string.tutorial_register_state_connected;
            case REGISTRATION_STATE_GETTING_DEVICE_INFO:
                return R.string.tutorial_register_state_getting_device_info;
            case REGISTRATION_STATE_RECEIVED_DEVICE_INFO:
                return R.string.tutorial_register_state_received_device_info;
            case REGISTRATION_STATE_CONFIGURING:
                return R.string.tutorial_register_state_configuring;
            case REGISTRATION_STATE_CONFIGURED:
                return R.string.tutorial_register_state_configured;
            case REGISTRATION_STATE_CONNECTING_BACK:
                return R.string.tutorial_register_state_cleanup;
            case REGISTRATION_STATE_LAN_SCAN_FOR_ADDED_MODULE:
                return R.string.tutorial_register_state_lan_scan;
            case REGISTRATION_STATE_LAN_SCAN_MODULE_FOUND:
                return R.string.tutorial_register_state_lan_module_found;
            case REGISTRATION_STATE_ALREADY_BUSY:
                break;
            case REGISTRATION_STATE_FAILED:
                return R.string.tutorial_register_state_error;
            case REGISTRATION_STATE_DONE:
                return R.string.tutorial_register_state_done;
        }
        return -1;
    }
}