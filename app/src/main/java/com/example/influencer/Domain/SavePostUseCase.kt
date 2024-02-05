package com.example.influencer.Domain

import com.example.influencer.Data.Repositories.PostRepository
import com.example.influencer.UI.Upload_New_Checkpoint.Model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

// Use case for saving a post
@Singleton
class SavePostUseCase @Inject constructor(private val repository: PostRepository){
    suspend operator fun invoke(post:Post) = withContext(Dispatchers.IO){
        repository.savePost(post)
    }
}
