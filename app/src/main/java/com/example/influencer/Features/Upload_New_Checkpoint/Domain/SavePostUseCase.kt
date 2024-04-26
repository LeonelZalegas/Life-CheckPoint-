package com.example.influencer.Features.Upload_New_Checkpoint.Domain

import com.example.influencer.Core.Data.Repositories.PostRepository.PostRepository
import com.example.influencer.Features.Upload_New_Checkpoint.Domain.Model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

// Use case for saving a post
@Singleton
class SavePostUseCase @Inject constructor(private val repository: PostRepository){
    suspend operator fun invoke(post: Post) = withContext(Dispatchers.IO){
        repository.savePost(post)
    }
}
