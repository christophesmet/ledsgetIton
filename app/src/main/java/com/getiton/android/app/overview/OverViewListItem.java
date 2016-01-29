package com.getiton.android.app.overview;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.christophesmet.getiton.library.core.discovery.lan.repo.model.LanModule;
import com.christophesmet.getiton.library.core.module.WifiModule;

/**
 * Created by christophesmet on 14/10/15.
 */

public class OverViewListItem {
    @Nullable
    public WifiModule mWifiModule;
    @Nullable
    public LanModule mLanModule;
    private String name="";

    public OverViewListItem(WifiModule wifiModule, @NonNull String name) {
        mWifiModule = wifiModule;
        this.name = name;
    }

    public OverViewListItem(LanModule lanModule) {
        mLanModule = lanModule;
    }

    @Nullable
    public WifiModule getWifiModule() {
        return mWifiModule;
    }

    @Nullable
    public LanModule getLanModule() {
        return mLanModule;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}