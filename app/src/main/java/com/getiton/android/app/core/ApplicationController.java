package com.getiton.android.app.core;

import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.crashlytics.android.Crashlytics;
import com.getiton.android.app.core.dependency.MainViewModelProvider;

import dagger.ObjectGraph;
import io.fabric.sdk.android.Fabric;

/**
 * Created by christophesmet on 06/09/15.
 */

public class ApplicationController extends MultiDexApplication {

    private ObjectGraph mObjectGraph;


    @Override
    public void onCreate() {
        super.onCreate();
        setupDagger();
        setupDb();
        Fabric.with(this, new Crashlytics());
        Crashlytics.logException(new Exception("Testing non fatal exception for tommy D"));
        Log.d(null,"done logging");
    }

    public void inject(@NonNull Object object) {
        mObjectGraph.inject(object);
    }

    private void setupDagger() {
        ActiveAndroid.initialize(this);
        mObjectGraph = ObjectGraph.create(new MainViewModelProvider(getApplicationContext()));

    }

    private void setupDb() {
        ActiveAndroid.initialize(new Configuration.Builder(this)
                        .setDatabaseName("getiton_extra_info")
                        .setDatabaseVersion(1)
                        .create()
        );
    }
}