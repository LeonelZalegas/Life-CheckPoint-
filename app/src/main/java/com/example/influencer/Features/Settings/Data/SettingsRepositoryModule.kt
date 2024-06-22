package com.example.influencer.Features.Settings.Data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        firestoreSettingsRepository: FirestoreSettingsRepository
    ): SettingsRepository
}