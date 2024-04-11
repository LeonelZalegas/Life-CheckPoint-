package com.example.influencer.Domain

import com.example.influencer.Data.Repositories.PostRepository.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LikesInteractionsUseCase @Inject constructor(private val repository: PostRepository) {

    suspend fun likePost(postId: String,postOwnerId: String) = withContext(Dispatchers.IO) {
        repository.likePost(postId,postOwnerId)
    }

    suspend fun unlikePost(postId: String, postOwnerId: String) = withContext(Dispatchers.IO) {
        repository.unlikePost(postId,postOwnerId)
    }

    suspend fun isPostLiked(postId: String): Boolean = withContext(Dispatchers.IO) {
        repository.isPostLiked(postId)
    }
}