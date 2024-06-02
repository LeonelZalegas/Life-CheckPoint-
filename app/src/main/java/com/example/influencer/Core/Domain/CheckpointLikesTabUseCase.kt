package com.example.influencer.Core.Domain

import com.example.influencer.Core.Data.Repositories.PostRepository.PostRepository
import com.example.influencer.Features.Upload_New_Checkpoint.Domain.Model.Post
import javax.inject.Inject

class CheckpointLikesTabUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend fun getUserPostsByCategory(userId: String, category: String): List<Post>? {
        return postRepository.getUserPostsByCategory(userId, category)
    }

    suspend fun getUserLikedPosts(userId: String): List<Post>? {
        return postRepository.getUserLikedPosts(userId)
    }
}