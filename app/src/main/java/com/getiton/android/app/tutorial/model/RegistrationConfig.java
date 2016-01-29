package com.getiton.android.app.tutorial.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.christophesmet.getiton.library.core.module.WifiModule;

/**
 * Created by christophesmet on 03/10/15.
 */

public class RegistrationConfig implements Parcelable {

    @NonNull
    private WifiModule mWifiModule;
    @NonNull
    private String mTargetSSID;
    @NonNull
    private String mTargetBSSID;
    @NonNull
    private String mTargetPass;
    @NonNull
    private String mName;

    public RegistrationConfig(@NonNull WifiModule wifiModule, @NonNull String targetSSID, @NonNull String targetBSSID, @NonNull String targetPass, @NonNull String name) {
        mWifiModule = wifiModule;
        mTargetSSID = targetSSID;
        mTargetPass = targetPass;
        mTargetBSSID = targetBSSID;
        mName = name;
    }


    protected RegistrationConfig(Parcel in) {
        mWifiModule = in.readParcelable(WifiModule.class.getClassLoader());
        mTargetSSID = in.readString();
        mTargetBSSID = in.readString();
        mTargetPass = in.readString();
        mName = in.readString();
    }

    public static final Creator<RegistrationConfig> CREATOR = new Creator<RegistrationConfig>() {
        @Override
        public RegistrationConfig createFromParcel(Parcel in) {
            return new RegistrationConfig(in);
        }

        @Override
        public RegistrationConfig[] newArray(int size) {
            return new RegistrationConfig[size];
        }
    };

    @NonNull
    public String getName() {
        return mName;
    }

    @NonNull
    public WifiModule getWifiModule() {
        return mWifiModule;
    }

    @NonNull
    public String getTargetSSID() {
        return mTargetSSID;
    }

    @NonNull
    public String getTargetPass() {
        return mTargetPass;
    }

    @NonNull
    public String getTargetBSSID() {
        return mTargetBSSID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mWifiModule, flags);
        dest.writeString(mTargetSSID);
        dest.writeString(mTargetBSSID);
        dest.writeString(mTargetPass);
        dest.writeString(mName);
    }
}