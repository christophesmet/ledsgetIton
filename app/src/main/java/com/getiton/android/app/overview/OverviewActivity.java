package com.getiton.android.app.overview;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.getiton.android.app.core.ApplicationController;
import com.getiton.android.app.core.CoreActivity;
import com.getiton.android.app.core.bindable.BindableRecyclerViewAdapter;
import com.getiton.android.app.core.collections.ArrayQueryable;
import com.getiton.android.app.core.data.NamedLanModulesViewModel;
import com.getiton.android.app.core.data.db.NamedLanModule;
import com.getiton.android.app.core.util.SnackbarUtil;
import com.getiton.android.app.rgb.RgbV1Activity;
import com.getiton.android.app.tutorial.TutorialSetupInfoActivity;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.christophesmet.getiton.library.GetItOn;
import com.christophesmet.getiton.library.core.discovery.lan.repo.model.LanModule;
import com.christophesmet.getiton.library.core.module.WifiModule;
import com.christophesmet.getiton.library.logging.ILoggingService;
import com.christophesmet.getiton.library.modules.RGBV1Module;
import com.christophesmet.ledsgetiton.R;
import rx.Observable;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;
import rx.functions.Func1;

/**
 * Created by christophesmet on 06/09/15.
 */

public class OverviewActivity extends CoreActivity {

    private static final int MAX_SCAN_TRIES = 3;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.lst_items)
    RecyclerView mLstItems;
    @InjectView(R.id.prg_loading)
    ProgressBar mPrgLoading;
    private int mScanTries = 0;

    @Inject
    protected GetItOn mGetItOn;
    @Inject
    protected ILoggingService mILoggingService;
    @Inject
    protected NamedLanModulesViewModel mNamedLanModulesViewModel;

    private BindableRecyclerViewAdapter<OverViewListItem> mAdapter;
    private ArrayList<OverViewListItem> mItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ApplicationController) getApplicationContext()).inject(this);
        setContentView(R.layout.overview_activity);
        ButterKnife.inject(this);
        loadData();
        loadListeners();
        startSearching();
    }

    private void loadData() {
        mAdapter = new BindableRecyclerViewAdapter<>(this, R.layout.overview_module_listitemview, new ArrayQueryable<>(mItems));
        mLstItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mLstItems.setAdapter(mAdapter);
    }

    private void loadListeners() {
        mAdapter.setItemClickListener(new BindableRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(@NonNull Object view, int position) {
                openDetail(mAdapter.getCollection().getItem(position));
            }
        });
    }

    private void openDetail(@NonNull OverViewListItem item) {
        if (item.getLanModule() != null && item.getLanModule() instanceof RGBV1Module) {
            Intent intent = RgbV1Activity.creatIntent(this, item.getLanModule().getMac(), 0xFFFF0000);
            startActivity(intent);
        } else if (item.getWifiModule() != null) {
            Intent intent = TutorialSetupInfoActivity.createIntent(this, item.getWifiModule());
            startActivity(intent);
        }
    }

    private void startSearching() {
        showLoading(true);
        mItems.clear();
        mAdapter.setCollection(new ArrayQueryable<OverViewListItem>(mItems), true);
        AndroidObservable.bindActivity(this,
                getNamedLanModuleScanResults()
                        .mergeWith(getWifiScanResults())
        )
                .subscribe(new Subscriber<OverViewListItem>() {
                    @Override
                    public void onCompleted() {
                        onSearchComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        showloading(mPrgLoading, false);
                        mILoggingService.log(e);
                        SnackbarUtil.show(OverviewActivity.this, e, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mScanTries = 0;
                                startSearching();
                            }
                        });
                    }

                    @Override
                    public void onNext(OverViewListItem overViewListItem) {
                        mItems.add(0, overViewListItem);
                        mAdapter.notifyItemInserted(0);
                    }
                });
    }

    private void onSearchComplete() {
        mScanTries++;
        mILoggingService.log("Scan completed: times -> " + mScanTries);
        showloading(mPrgLoading, false);
        if (mScanTries >= MAX_SCAN_TRIES) {
            if (mItems.size() == 0) {
                onNoModulesFound();
            }
        } else {
            if (mItems.size() == 0) {
                startSearching();
            }
        }
    }

    private Observable<OverViewListItem> getNamedLanModuleScanResults() {
        return mGetItOn.scanForLanModules()
                .map(new Func1<LanModule, OverViewListItem>() {
                    @Override
                    public OverViewListItem call(LanModule lanModule) {
                        OverViewListItem output = new OverViewListItem(lanModule);
                        NamedLanModule namedLanModule = mNamedLanModulesViewModel.findModuleForId(lanModule.getMac());
                        if (namedLanModule != null) {
                            output.setName(namedLanModule.getName());
                        } else {
                            output.setName("Unknown");
                        }
                        return output;
                    }
                });
    }

    private Observable<OverViewListItem> getWifiScanResults() {
        return mGetItOn
                .getRegistrator()
                .scanWifiForCanidates(false)
                .flatMap(new Func1<ArrayList<WifiModule>, Observable<? extends WifiModule>>() {
                    @Override
                    public Observable<? extends WifiModule> call(ArrayList<WifiModule> wifiModules) {
                        return Observable.from(wifiModules);
                    }
                })
                .map(new Func1<WifiModule, OverViewListItem>() {
                    @Override
                    public OverViewListItem call(WifiModule wifiModule) {
                        String name = wifiModule.getSSID().equalsIgnoreCase("leds get it on") ? "RGB Module" : "Unknown Module";
                        OverViewListItem output = new OverViewListItem(wifiModule, name);
                        return output;
                    }
                });
    }

    private void onNoModulesFound() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.tutorial_scan_timeout_title))
                .setMessage(getString(R.string.tutorial_scan_timeout_description))
                .setPositiveButton(getString(R.string.general_ok_got_it), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mScanTries = 0;
                        dialog.dismiss();
                        startSearching();
                    }
                }).show();
    }

    private void showLoading(boolean show) {
        mPrgLoading.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}