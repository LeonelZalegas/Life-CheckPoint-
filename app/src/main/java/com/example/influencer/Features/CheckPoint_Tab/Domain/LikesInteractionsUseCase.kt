package com.example.influencer.Features.CheckPoint_Tab.Domain

import com.example.influencer.Core.Data.Repositories.PostRepository.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LikesInteractionsUseCase @Inject constructor(private val repository: PostRepository) {

    suspend fun likePost(postId: String) = withContext(Dispatchers.IO) {
        repository.likePost(postId)
    }

    suspend fun unlikePost(postId: String) = withContext(Dispatchers.IO) {
        repository.unlikePost(postId)
    }

    suspend fun isPostLiked(postId: String): Boolean = withContext(Dispatchers.IO) {
        repository.isPostLiked(postId)
    }
}