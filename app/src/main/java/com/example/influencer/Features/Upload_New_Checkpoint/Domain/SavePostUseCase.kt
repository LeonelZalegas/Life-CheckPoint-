package com.example.influencer.Features.Upload_New_Checkpoint.Domain

import com.example.influencer.Features.Upload_New_Checkpoint.Data.UploadCheckpoint
import com.example.influencer.Features.Upload_New_Checkpoint.Domain.Model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

// Use case for saving a post
@Singleton
class SavePostUseCase @Inject constructor(private val uploadCheckpoint: UploadCheckpoint){
    suspend operator fun invoke(post: Post) = withContext(Dispatchers.IO){
        uploadCheckpoint.savePost(post)
    }
}
