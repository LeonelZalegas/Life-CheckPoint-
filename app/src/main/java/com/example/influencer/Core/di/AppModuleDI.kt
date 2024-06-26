package com.example.influencer.Core.di;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModuleDI {

    // Provide the Application Resources
    @Provides
    @Singleton
    public static Resources provideResources(Application application) {
        return application.getResources();
    }

    // Provide the Application context
    @Provides
    @Singleton
    public static Context provideApplicationContext(Application application) {
        return application;
    }
}
