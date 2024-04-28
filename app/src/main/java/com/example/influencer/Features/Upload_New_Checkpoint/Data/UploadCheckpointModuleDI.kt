package com.example.influencer.Features.Upload_New_Checkpoint.Data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UploadCheckpointModuleDI {

    @Binds
    @Singleton
    abstract fun bindUploadCheckpoint(
        uploadCheckpointRepo: UploadCheckpointRepo
    ): UploadCheckpoint
}