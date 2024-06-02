package com.example.influencer.Core.di

import android.content.Context
import com.example.influencer.Core.Utils.CheckpointsCategoriesList
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CheckpointsModule {

    @Singleton
    @Provides
    fun provideCheckpointsCategoriesList(@ApplicationContext context: Context): CheckpointsCategoriesList {
        return CheckpointsCategoriesList(context.resources)
    }
}
