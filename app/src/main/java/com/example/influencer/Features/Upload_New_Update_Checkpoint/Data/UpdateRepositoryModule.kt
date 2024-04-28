package com.example.influencer.Features.Upload_New_Update_Checkpoint.Data

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