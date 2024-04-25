package com.example.influencer.Core.Data.Repositories.UpdatesRepository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class UpdateRepositoryModule {

    @Binds
    abstract fun bindUpdatesRepository(
        firestoreUpdatesRepository: FirestoreUpdatesRepository
    ): UpdatesRepository
}