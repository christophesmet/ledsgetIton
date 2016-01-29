package com.getiton.android.app.core.dependency;

import android.content.Context;

import com.getiton.android.app.core.data.NamedLanModulesViewModel;
import com.getiton.android.app.overview.OverviewActivity;
import com.getiton.android.app.rgb.RgbV1Activity;
import com.getiton.android.app.tutorial.TutorialRegisterFlowActivity;
import com.getiton.android.app.tutorial.TutorialSetupInfoActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import com.christophesmet.getiton.library.GetItOn;
import com.christophesmet.getiton.library.logging.DefaultLoggingService;
import com.christophesmet.getiton.library.logging.ILoggingService;

/**
 * Created by christophesmet on 07/09/15.
 */
@Module(
        injects = {RgbV1Activity.class, OverviewActivity.class
                , TutorialSetupInfoActivity.class, TutorialRegisterFlowActivity.class},
        complete = true)
public class MainViewModelProvider {

    private Context mAppContext;

    public MainViewModelProvider(Context appContext) {
        mAppContext = appContext;
    }


    @Provides
    @Singleton
    GetItOn provideGetIton(ILoggingService loggingService) {
        return new GetItOn(mAppContext, loggingService);
    }

    @Provides
    @Singleton
    ILoggingService provideLoggingService() {
        return new DefaultLoggingService(mAppContext);
    }

    @Provides
    @Singleton
    NamedLanModulesViewModel provideNamedLanViewModel() {
        return new NamedLanModulesViewModel();
    }
}