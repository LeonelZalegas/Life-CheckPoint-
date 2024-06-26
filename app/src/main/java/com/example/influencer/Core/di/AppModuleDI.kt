package com.example.influencer.Core.di

import android.app.Application
import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModuleDI {
    // Provide the Application Resources
    @JvmStatic
    @Provides
    @Singleton
    fun provideResources(application: Application): Resources {
        return application.resources
    }

    // Provide the Application context
    @JvmStatic
    @Provides
    @Singleton
    fun provideApplicationContext(application: Application): Context {
        return application
    }
}