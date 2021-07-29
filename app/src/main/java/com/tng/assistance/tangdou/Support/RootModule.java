package com.tng.assistance.tangdou.Support;

import android.app.Application;

import com.tng.assistance.tangdou.infrastructure.AndroidBus;
import com.tng.assistance.tangdou.services.SettingsService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.components.ServiceComponent;
import dagger.hilt.android.scopes.ServiceScoped;

@Module
@InstallIn(value = {
        ActivityComponent.class,
        ServiceComponent.class,
        ApplicationComponent.class
})
public class RootModule {

    @Provides
    public static SettingsService settingsService(Application application) {
        return new SettingsService(application);
    }

    @Provides
    public static TangDouMediaFileScanner mediaFileScanner() {
        return new TangDouMediaFileScanner();
    }

    @Provides
    public static AndroidBus androidBus() {
        return AndroidBus.getInstance();
    }
}
